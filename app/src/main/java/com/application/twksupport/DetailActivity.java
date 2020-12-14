package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Currency;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.DoneData;
import com.application.twksupport.model.FeatureData;
import com.application.twksupport.model.NotificationData;
import com.application.twksupport.model.TodoData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.abhinay.input.CurrencyEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;
import static com.application.twksupport.R.id.aprovalStat;
import static com.application.twksupport.R.id.client_ageedisaree;
import static com.application.twksupport.R.id.container_reported_request_at;

public class DetailActivity extends AppCompatActivity {
    private TextView txtPriority, txtAppname, txtSubject, txtDetail, txtTitle, txtDeadlineOrTimePeriodic, txtAprovalStat, txtPtname, txtDeadlineStaff, txtTimePeriodic, txtPrice, txt_assignDate, txt_report_request_date, txt_completed_date, tv_report_request, txt_clientstaff_remember;
    private TableRow rowDeadlineStaff, tv_time_periodic_container, tv_price_container, tv_container_assignDate, tv_container_report_requestDate, tv_container_completed_at;
    private Button btnAssign, btnAgreement, btnStaff, btnClientAgree, btnClientIgnore;
    private CurrencyEditText etPrice;
    private Toolbar det_toolbar;
    private LinearLayout container, container_price, containerAdminAct, clientAgreeDisagree;
    public static final String EXTRA_BUG = "extra_bug";
    public static final String EXTRA_FEATURE = "extra_feature";
    public static final String EXTRA_DONE = "extra_done";
    public static final String EXTRA_JOBS = "extra_jobs";
    public static final String EXTRA_NOTIF = "extra_notif";
    private String setAproval;
    DatePickerDialog.OnDateSetListener setListener;
    ImageButton btnOpenDate;
    EditText etyear;
    EditText etmonth;
    EditText etday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initialize();

        det_toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        setSupportActionBar(det_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final BugsData bugsData = getIntent().getParcelableExtra(EXTRA_BUG);
        final FeatureData fiturData = getIntent().getParcelableExtra(EXTRA_FEATURE);
        final DoneData doneData = getIntent().getParcelableExtra(EXTRA_DONE);
        final TodoData jobsData = getIntent().getParcelableExtra(EXTRA_JOBS);
        final NotificationData notifData = getIntent().getParcelableExtra(EXTRA_NOTIF);
        final SharedPreferences _objpref = getSharedPreferences("JWTTOKEN", 0);
        final SharedPreferences userInfo = getSharedPreferences("userInformation", 0);
        final String getToken = _objpref.getString("jwttoken", "");
        final String getRole = userInfo.getString("role", "");
        final String id_user = userInfo.getString("id", "");
        Log.d("roleDetail", "" + getRole);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDetail.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        etPrice.setCurrency("Rp");
        etPrice.setDelimiter(true);
        etPrice.setSpacing(true);
        etPrice.setDecimals(false);
        etPrice.setSeparator(".");

        switch (getRole) {
            case "twk-head":
                clientAgreeDisagree.setVisibility(View.GONE);
                if (getIntent().hasExtra(EXTRA_BUG)) {
                    tv_time_periodic_container.setVisibility(View.GONE);
                    tv_price_container.setVisibility(View.GONE);
                    container_price.setVisibility(View.GONE);
                    btnAgreement.setVisibility(View.GONE);
                    txtPtname.setVisibility(View.VISIBLE);
                    txtPtname.setText(bugsData.getNama_perusahaan());
                    txtPriority.setText(bugsData.getPriority());
                    txtAppname.setText(bugsData.getApps_name());
                    txtSubject.setText(bugsData.getSubject());
                    txtDetail.setText(bugsData.getDetail());
                    txt_report_request_date.setText(bugsData.getCreated_at());
                    txtTitle.setText("Bug Report");
                    String status = bugsData.getStatus();
                    datePicker(etyear, etmonth, etday);
                    if (status.equals("Reported")) {
                        btnAssign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                                if (!datePick.equals("--")) {
                                    Intent toStafflist = new Intent(DetailActivity.this, StaffListActivity.class);
                                    toStafflist.putExtra(StaffListActivity.EXTRA_TICKET_BUG, bugsData);
                                    toStafflist.putExtra(StaffListActivity.EXTRA_DATE, datePick);
                                    startActivity(toStafflist);
                                } else {
                                    new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Please input the deadline")
                                            .setConfirmText("Ok")
                                            .setConfirmButtonBackgroundColor(Color.parseColor("#FFFF9800"))
                                            .show();
                                }
                            }
                        });
                    }else if(status.equals("On Proccess")) {
                        txtAprovalStat.setVisibility(View.VISIBLE);
                        txtAprovalStat.setText("Assigned");
                        tv_container_assignDate.setVisibility(View.VISIBLE);
                        txt_assignDate.setText(bugsData.getAssign_at());
                        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                        container.setVisibility(View.GONE);
                    }
                    else {
                        container.setVisibility(View.INVISIBLE);
                    }
                }
                else if (getIntent().hasExtra(EXTRA_FEATURE)) {
                    setAdminFeatureAct(fiturData, "Bearer "+getToken);
                }
                else if(getIntent().hasExtra(EXTRA_NOTIF)){
                    setAdminNotifAct(notifData, "Bearer "+getToken, id_user);
                }
                else {
                    setDetailDone(doneData);
                }
                break;

            case "twk-staff":
                tv_container_report_requestDate.setVisibility(View.GONE);
                tv_time_periodic_container.setVisibility(View.GONE);
                tv_price_container.setVisibility(View.GONE);
                tv_container_assignDate.setVisibility(View.VISIBLE);
                container.setVisibility(View.GONE);
                rowDeadlineStaff.setVisibility(View.VISIBLE);
                if (getIntent().hasExtra(EXTRA_JOBS)) {
                    txt_assignDate.setText(jobsData.getCreated_at());
                    tv_price_container.setVisibility(View.GONE);
                    tv_time_periodic_container.setVisibility(View.GONE);
                    btnStaff.setVisibility(View.VISIBLE);
                    txtPtname.setVisibility(View.VISIBLE);
                    if (jobsData.getType().equals("Report")) {
                        txtTitle.setText("Bug Report");
                    } else {
                        txtTitle.setText("Feature Request");
                    }
                    markAsComplete(getToken, jobsData.getId_ticket(), id_user, "from_todo");
                    txtDeadlineStaff.setText("Deadline : " + jobsData.getDeadline());
                    txtPtname.setText(jobsData.getNama_perusahaan());
                    txtPriority.setText(jobsData.getPriority());
                    txtAppname.setText(jobsData.getApps_name());
                    txtSubject.setText(jobsData.getSubject());
                    txtDetail.setText(jobsData.getDetail());
                }
                else if(getIntent().hasExtra(EXTRA_NOTIF)){
                    switch (notifData.getType()) {
                        case "Report":
                            if (!notifData.getStatus().equals("Done")){
                                btnStaff.setVisibility(View.VISIBLE);
                                markAsComplete(getToken, notifData.getId_ticket(), id_user, "from_notif");
                            }else {
                                btnStaff.setVisibility(View.GONE);
                            }
                            txt_assignDate.setText(notifData.getAssign_at());
                            txtPtname.setVisibility(View.VISIBLE);
                            txtTitle.setText("Bug Report");
                            txtDeadlineStaff.setText("Deadline : " + notifData.getDead_line());
                            txtPtname.setText(notifData.getNama_perusahaan());
                            txtPriority.setText(notifData.getPriority());
                            txtAppname.setText(notifData.getApps_name());
                            txtSubject.setText(notifData.getSubject());
                            txtDetail.setText(notifData.getDetail());
                            break;

                        case "Request":
                            if (!notifData.getStatus().equals("Done")){
                                btnStaff.setVisibility(View.VISIBLE);
                                markAsComplete(getToken, notifData.getId_ticket(), id_user, "from_notif");
                            }else {
                                btnStaff.setVisibility(View.GONE);
                            }
                            txtTitle.setText("Feature Request");
                            txtPtname.setVisibility(View.VISIBLE);
                            txtTitle.setText("Bug Report");
                            txtDeadlineStaff.setText("Deadline : " + notifData.getDead_line());
                            txtPtname.setText(notifData.getNama_perusahaan());
                            txtPriority.setText(notifData.getPriority());
                            txtAppname.setText(notifData.getApps_name());
                            txtSubject.setText(notifData.getSubject());
                            txtDetail.setText(notifData.getDetail());
                            break;
                    }
                }
                else {
                    txtPtname.setVisibility(View.VISIBLE);
                    if (doneData.getType().equals("Report")) {
                        txtTitle.setText("Bug Report");
                    } else {
                        txtTitle.setText("Feature Request");
                    }
                    txtDeadlineStaff.setText("Deadline : "+doneData.getDead_line());
                    txtAprovalStat.setVisibility(View.VISIBLE);
                    txtAprovalStat.setText(doneData.getStatus());
                    txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                    tv_container_completed_at.setVisibility(View.VISIBLE);
                    txt_completed_date.setText(doneData.getUpdated_at());
                    txt_assignDate.setText(doneData.getAssign_at());
                    txtPtname.setText(doneData.getNama_perusahaan());
                    txtPriority.setText(doneData.getPriority());
                    txtAppname.setText(doneData.getApps_name());
                    txtSubject.setText(doneData.getSubject());
                    txtDetail.setText(doneData.getDetail());
                }
                break;

            //Client-act
            default:
                container_price.setVisibility(View.GONE);
                if (getIntent().hasExtra(EXTRA_BUG)) {
                    tv_price_container.setVisibility(View.GONE);
                    tv_time_periodic_container.setVisibility(View.GONE);
                    container.setVisibility(View.GONE);

                    txt_report_request_date.setText(bugsData.getCreated_at());
                    txtPriority.setText(bugsData.getPriority());
                    txtAppname.setText(bugsData.getApps_name());
                    txtSubject.setText(bugsData.getSubject());
                    txtDetail.setText(bugsData.getDetail());
                    txtTitle.setText("Bug Report");
                    if (bugsData.getStatus().equals("On Proccess")){
                        txtAprovalStat.setVisibility(View.VISIBLE);
                        tv_container_assignDate.setVisibility(View.VISIBLE);
                        txt_assignDate.setText(bugsData.getAssign_at());
                        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.onProccess));
                        txtAprovalStat.setText(bugsData.getStatus());
                    }
                }
                else if (getIntent().hasExtra(EXTRA_FEATURE)) {
                    txtPriority.setText(fiturData.getPriority());
                    txtAppname.setText(fiturData.getApps_name());
                    txt_report_request_date.setText(fiturData.getCreated_at());
                    tv_report_request.setText("Requested at");
                    txtSubject.setText(fiturData.getSubject());
                    txtDetail.setText(fiturData.getDetail());
                    txtTitle.setText("Feature Request");
                    String status = fiturData.getStatus();
                    String aproval_stat = fiturData.getAproval_stat();
                    if (aproval_stat!=null && aproval_stat.equals("Ignored")){
                        txtAprovalStat.setText("Ignored");
                        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.RedDefault));
                        txtAprovalStat.setVisibility(View.VISIBLE);
                    }else if(aproval_stat!=null && aproval_stat.equals("Accepted")){
                        txtAprovalStat.setText("Accepted");
                        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                        txtAprovalStat.setVisibility(View.VISIBLE);
                    }
                    if (status.equals("Requested")){
                        tv_price_container.setVisibility(View.GONE);
                        tv_time_periodic_container.setVisibility(View.GONE);
                        container.setVisibility(View.GONE);
                    }
                    else if(status.equals("On Proccess")){
                        container.setVisibility(View.GONE);
                        txtAprovalStat.setText("On Proccess");
                        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.onProccess));
                        tv_container_assignDate.setVisibility(View.VISIBLE);
                        txt_assignDate.setText(fiturData.getAssign_at());
                        txtTimePeriodic.setText(fiturData.getTime_periodic());
                        txtPrice.setText(toRupiah(String.valueOf(fiturData.getPrice())));
                    }
                    else if (status.equals("Need Agreement")) {
                        btnAssign.setVisibility(View.GONE);
                        btnAgreement.setVisibility(View.GONE);
                        txtTimePeriodic.setText(fiturData.getTime_periodic());
                        txtPrice.setText(toRupiah(String.valueOf(fiturData.getPrice())));
                        containerAdminAct.setVisibility(View.GONE);
                        if (getRole.equals("client-staff")){
                            btnClientAgree.setEnabled(false);
                            btnClientIgnore.setEnabled(false);
                            btnClientAgree.setBackgroundColor(Color.parseColor("#777777"));
                            btnClientIgnore.setBackgroundColor(Color.parseColor("#777777"));
                            txt_clientstaff_remember.setVisibility(View.VISIBLE);
                        }
                        btnClientAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setAproval = "Accepted";
                                final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Please wait...");
                                pDialog.setCancelable(false);
                                final SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                sweet.setTitleText("Are you sure?");
                                sweet.setContentText("this action cannot be aborted");
                                sweet.setConfirmText("Yes");
                                sweet.setCancelText("Cancel");
                                sweet.showCancelButton(true);
                                sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweet.dismiss();
                                        pDialog.show();
                                        setClientAgreeDisagree("Bearer " + getToken, fiturData.getId_ticket(), fiturData.getNama_perusahaan(), fiturData.getApps_name(), setAproval, pDialog, id_user);
                                    }
                                });
                                sweet.show();
                            }
                        });
                        btnClientIgnore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setAproval = "Ignored";
                                final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Loading");
                                pDialog.setCancelable(false);
                                final SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                sweet.setTitleText("Are you sure?");
                                sweet.setCustomImage(R.drawable.ic_sad);
                                sweet.setContentText("this action cannot be aborted");
                                sweet.setConfirmText("Yes");
                                sweet.setCancelText("Cancel");
                                sweet.showCancelButton(true);
                                sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweet.dismiss();
                                        pDialog.show();
                                        setClientAgreeDisagree("Bearer " + getToken, fiturData.getId_ticket(), fiturData.getNama_perusahaan(), fiturData.getApps_name(), setAproval, pDialog, id_user);
                                    }
                                });
                                sweet.show();
                            }
                        });

                    }
                    else {
                        container.setVisibility(View.GONE);
                        txtPrice.setText(toRupiah(String.valueOf(fiturData.getPrice())));
                        txtTimePeriodic.setText(fiturData.getTime_periodic());
                    }
                }
                else if (getIntent().hasExtra(EXTRA_NOTIF)) {
                    switch (notifData.getType()) {
                        case "Report":
                            tv_price_container.setVisibility(View.GONE);
                            tv_time_periodic_container.setVisibility(View.GONE);
                            container.setVisibility(View.GONE);
                            txtPriority.setText(notifData.getPriority());
                            txtAppname.setText(notifData.getApps_name());
                            txtSubject.setText(notifData.getSubject());
                            txtDetail.setText(notifData.getDetail());
                            txtTitle.setText("Bug Report");
                            if (notifData.getStatus().equals("On Proccess")){
                                txtAprovalStat.setVisibility(View.VISIBLE);
                                tv_container_assignDate.setVisibility(View.VISIBLE);
                                txt_assignDate.setText(notifData.getAssign_at());
                                txtAprovalStat.setTextColor(this.getResources().getColor(R.color.onProccess));
                                txtAprovalStat.setText(notifData.getStatus());
                            }else {
                                txtAprovalStat.setVisibility(View.VISIBLE);

                                txt_report_request_date.setText(notifData.getCreated_at());
                                tv_container_assignDate.setVisibility(View.VISIBLE);
                                txt_assignDate.setText(notifData.getAssign_at());
                                tv_container_completed_at.setVisibility(View.VISIBLE);
                                txt_completed_date.setText(notifData.getUpdated_at());
                                txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                                txtAprovalStat.setText(notifData.getStatus());
                            }
                            break;

                        case "Request":
                            txtPriority.setText(notifData.getPriority());
                            txtAppname.setText(notifData.getApps_name());
                            txtSubject.setText(notifData.getSubject());
                            txtDetail.setText(notifData.getDetail());
                            txtTimePeriodic.setText(notifData.getTime_periodic());
                            txtPrice.setText(toRupiah(String.valueOf(notifData.getPrice())));
                            txtTitle.setText("Feature Request");
                            String status = notifData.getStatus();
                            String aproval_stat = notifData.getAproval_stat();
                            if (aproval_stat!=null && aproval_stat.equals("Ignored")){
                                txtAprovalStat.setText("Ignored");
                                txtAprovalStat.setTextColor(this.getResources().getColor(R.color.RedDefault));
                                txtAprovalStat.setVisibility(View.VISIBLE);
                            }else if(aproval_stat!=null && aproval_stat.equals("Accepted")){
                                txtAprovalStat.setText("Accepted");
                                txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                                txtAprovalStat.setVisibility(View.VISIBLE);
                            }
                            if (status.equals("Need Agreement")) {
                                btnAssign.setVisibility(View.GONE);
                                btnAgreement.setVisibility(View.GONE);
                                containerAdminAct.setVisibility(View.GONE);
                                btnClientAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setAproval = "Accepted";
                                        final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                        pDialog.setTitleText("Please wait...");
                                        pDialog.setCancelable(false);
                                        final SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                        sweet.setTitleText("Are you sure?");
                                        sweet.setContentText("this action cannot be aborted");
                                        sweet.setConfirmText("Yes");
                                        sweet.setCancelText("Cancel");
                                        sweet.showCancelButton(true);
                                        sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweet.dismiss();
                                                pDialog.show();
                                                setClientAgreeDisagree("Bearer " + getToken, notifData.getId_ticket(), notifData.getNama_perusahaan(), notifData.getApps_name(), setAproval, pDialog, id_user);
                                            }
                                        });
                                        sweet.show();
                                    }
                                });
                                btnClientIgnore.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setAproval = "Ignored";
                                        final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                        pDialog.setTitleText("Loading");
                                        pDialog.setCancelable(false);
                                        final SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                        sweet.setTitleText("Are you sure?");
                                        sweet.setCustomImage(R.drawable.ic_sad);
                                        sweet.setContentText("this action cannot be aborted");
                                        sweet.setConfirmText("Yes");
                                        sweet.setCancelText("Cancel");
                                        sweet.showCancelButton(true);
                                        sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweet.dismiss();
                                                pDialog.show();
                                                setClientAgreeDisagree("Bearer " + getToken, notifData.getId_ticket(), notifData.getNama_perusahaan(), notifData.getApps_name(), setAproval, pDialog, id_user);
                                            }
                                        });
                                        sweet.show();
                                    }
                                });

                            }
                            else if(notifData.getStatus().equals("On Proccess")){
                                btnAssign.setVisibility(View.GONE);
                                btnAgreement.setVisibility(View.GONE);
                                containerAdminAct.setVisibility(View.GONE);
                                clientAgreeDisagree.setVisibility(View.GONE);
                                tv_container_assignDate.setVisibility(View.VISIBLE);
                                txt_assignDate.setText(notifData.getAssign_at());
                                txtAprovalStat.setTextColor(this.getResources().getColor(R.color.onProccess));
                                txtAprovalStat.setText(notifData.getStatus());
                            }
                            else if(notifData.getStatus().equals("Done")){

                                tv_report_request.setText("Requested at");
                                txt_report_request_date.setText(notifData.getCreated_at());
                                tv_container_assignDate.setVisibility(View.VISIBLE);
                                txt_assignDate.setText(notifData.getAssign_at());
                                tv_container_completed_at.setVisibility(View.VISIBLE);
                                txt_completed_date.setText(notifData.getUpdated_at());
                                txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                                txtAprovalStat.setText(notifData.getStatus());
                                container.setVisibility(View.GONE);
                                txtPrice.setText(toRupiah(String.valueOf(notifData.getPrice())));
                                txtTimePeriodic.setText(notifData.getTime_periodic());
                            }
                            else {
                                container.setVisibility(View.GONE);
                                txtPrice.setText(toRupiah(String.valueOf(notifData.getPrice())));
                                txtTimePeriodic.setText(notifData.getTime_periodic());
                            }
                    }
                }
                else {
                    setDetailDone(doneData);
                }
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return true;
    }

    private void datePicker(final EditText dateYear, final EditText dateMonth, final EditText dateDay) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        btnOpenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DetailActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;
                String tahun = "" + year;
                String bulan = "" + month;
                String hari = "" + dayOfMonth;
                dateYear.setText(tahun);
                dateMonth.setText(bulan);
                dateDay.setText(hari);
            }
        };

    }

    private void setDetailDone(DoneData doneData) {
        final SharedPreferences userInfo = getSharedPreferences("userInformation", 0);
        final String getRole = userInfo.getString("role", "");
        container.setVisibility(View.GONE);
        txtAprovalStat.setVisibility(View.VISIBLE);
        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
        txtAprovalStat.setText(doneData.getStatus());
        txtPriority.setText(doneData.getPriority());
        txtAppname.setText(doneData.getApps_name());
        txtSubject.setText(doneData.getSubject());
        txtDetail.setText(doneData.getDetail());
        if (getRole.equals("twk-staff") || getRole.equals("twk-head")){
            txtPtname.setVisibility(View.VISIBLE);
        }
        String getType = doneData.getType();
        switch (getType) {
            case "Report":
                txtPtname.setText(doneData.getNama_perusahaan());
                tv_time_periodic_container.setVisibility(View.GONE);
                tv_price_container.setVisibility(View.GONE);
                tv_container_completed_at.setVisibility(View.VISIBLE);
                txt_report_request_date.setText(doneData.getCreated_at());
                txt_completed_date.setText(doneData.getUpdated_at());
                txtTitle.setText("Bug Report");
                break;

            case "Request":
                txtPtname.setText(doneData.getNama_perusahaan());
                txtTitle.setText("Feature Request");
                tv_container_completed_at.setVisibility(View.VISIBLE);
                txt_report_request_date.setText(doneData.getCreated_at());
                txt_completed_date.setText(doneData.getUpdated_at());
                tv_report_request.setText("Requested at");
                txtTimePeriodic.setText(doneData.getTime_periodic());
                txtPrice.setText(String.valueOf(doneData.getPrice()));
                break;
        }
    }

    private void setClientAgreeDisagree(String token, String id_ticket, String nama_perusahaan, String apps_name, final String setAproval, final SweetAlertDialog sweetAlertDialog, final String id_user) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<JsonObject> agreement = api.clientAgreementAct(token, id_ticket, nama_perusahaan, apps_name, setAproval, "Agreement " + setAproval);
        Log.d("RETROACCEPT", "" + token + " " +id_ticket + " " + nama_perusahaan + " " + apps_name + " " + setAproval + "Agreement " + setAproval);
        agreement.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    sweetAlertDialog.dismiss();
                    try {
                        SweetAlertDialog sweet;
                        JSONObject object = new JSONObject(new Gson().toJson(response.body()));
                        String message = object.getString("message");
                        Log.d("objectValue", "" + message);
                        if (setAproval.equals("Ignore")) {
                            sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.NORMAL_TYPE);
                        } else {
                            sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        }

                        sweet.setTitleText(message);
                        sweet.setCanceledOnTouchOutside(false);
                        sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (getIntent().hasExtra(EXTRA_NOTIF)){
                                    NotificationActivity.getInstance().setPage(1);
                                    NotificationActivity.getInstance().getListnotif().clear();
                                    NotificationActivity.getInstance().addListNotification(id_user);
                                }else {
                                    FeatureFragment.getInstance().setPage(1);
                                    FeatureFragment.getInstance().getListFeature().clear();
                                    FeatureFragment.getInstance().addListDataFeatureUser();
                                }
                                finish();
                            }
                        });
                        sweet.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    sweetAlertDialog.dismiss();
                    Toast.makeText(DetailActivity.this, "Connected but failed", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                sweetAlertDialog.dismiss();
                SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweet.setTitleText("Oppss...");
                sweet.setContentText("Can't connect to server, please check your internet connection");
                sweet.show();
            }
        });
    }

    private void setAdminFeatureAct(final FeatureData fiturData, final String token){
        txtPtname.setVisibility(View.VISIBLE);
        txtPtname.setText(fiturData.getNama_perusahaan());
        txtPriority.setText(fiturData.getPriority());
        txtAppname.setText(fiturData.getApps_name());
        txtSubject.setText(fiturData.getSubject());
        txtDetail.setText(fiturData.getDetail());
        txtTimePeriodic.setText(fiturData.getTime_periodic());
        tv_report_request.setText("Requested at");
        txt_report_request_date.setText(fiturData.getCreated_at());
        txtDeadlineOrTimePeriodic.setText("Time Periodic :");
        txtTitle.setText("Feature Request");
        String status = fiturData.getStatus();
        datePicker(etyear, etmonth, etday);
        String aproval_stat = fiturData.getAproval_stat();
        if (aproval_stat!=null && aproval_stat.equals("Ignored")){
            txtAprovalStat.setText("Ignored");
            txtAprovalStat.setTextColor(this.getResources().getColor(R.color.RedDefault));
            txtAprovalStat.setVisibility(View.VISIBLE);
        }else if(aproval_stat!=null && aproval_stat.equals("Accepted")){
            txtAprovalStat.setText("Accepted");
            txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
            txtAprovalStat.setVisibility(View.VISIBLE);
        }
        if (status.equals("Requested")) {
            btnAssign.setVisibility(View.GONE);
            tv_time_periodic_container.setVisibility(View.GONE);
            tv_price_container.setVisibility(View.GONE);
            btnAgreement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Loading");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                    if (!datePick.equals("--") && etPrice.getCleanIntValue() != 0) {
                        ApiService api = ApiClient.getClient().create(ApiService.class);
                        Call<ResponseBody> makeAgreement = api.makeAgreement(token, fiturData.getId_ticket(), etPrice.getCleanIntValue(), datePick, "Need Agreement");
                        makeAgreement.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                pDialog.dismiss();
                                if (response.isSuccessful() && etPrice.getText().toString() != null) {
                                    try {
                                        SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                        sweet.setTitleText("Success");
                                        sweet.setContentText("Aproval was submitted");
                                        sweet.setCanceledOnTouchOutside(false);
                                        sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent goBack = new Intent(DetailActivity.this, UserActivity.class);
                                                goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(goBack);
                                                FeatureFragment.getInstance().setPage(1);
                                                FeatureFragment.getInstance().getListFeature().clear();
                                                FeatureFragment.getInstance().addListAdminFeature();
                                                finish();
                                            }
                                        });
                                        sweet.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    Log.d("401", "result : " + response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                pDialog.dismiss();
                                Log.d("detailactivity", "" + t.getMessage());
                            }
                        });
                    }else {
                        pDialog.dismiss();
                        new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Please input the blank field")
                                .setConfirmText("Ok")
                                .setConfirmButtonBackgroundColor(Color.parseColor("#FFFF9800"))
                                .show();
                    }
                }
            });
        }
        else if (status.equals("Agreement Accepted")) {
            btnAgreement.setVisibility(View.GONE);
            containerAdminAct.setVisibility(View.GONE);
            txtAprovalStat.setText("Accepted");
            final String time_periodic = fiturData.getTime_periodic();
            int price = fiturData.getPrice();
            txtTimePeriodic.setText(time_periodic);
            txtPrice.setText(toRupiah(String.valueOf(price)));
            txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
            txtAprovalStat.setVisibility(View.VISIBLE);
            btnAssign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent toStafflist = new Intent(DetailActivity.this, StaffListActivity.class);
                        toStafflist.putExtra(StaffListActivity.EXTRA_TICKET_FITUR, fiturData);
                        toStafflist.putExtra(StaffListActivity.EXTRA_DATE, time_periodic);
                        startActivity(toStafflist);

                }
            });
        }else if (status.equals("Need Agreement")){
            final String time_periodic = fiturData.getTime_periodic();
            int price = fiturData.getPrice();
            txtTimePeriodic.setText(time_periodic);
            txtPrice.setText(toRupiah(String.valueOf(price)));
            container.setVisibility(View.GONE);
        }else if(status.equals("On Proccess")){
            tv_container_assignDate.setVisibility(View.VISIBLE);
            txt_assignDate.setText(fiturData.getAssign_at());
            txtAprovalStat.setText(fiturData.getStatus());
            txtPrice.setText(toRupiah(String.valueOf(fiturData.getPrice())));
            txtAprovalStat.setTextColor(this.getResources().getColor(R.color.onProccess));
            container.setVisibility(View.GONE);
        }
        else {
            txtPrice.setText(String.valueOf(fiturData.getPrice()));
            txtTimePeriodic.setText(fiturData.getTime_periodic());
            container.setVisibility(View.INVISIBLE);

        }
    }

    private void setAdminNotifAct(final NotificationData dataNotif, final String token, final String id_user){
        String type = dataNotif.getType();
        switch (type){
            case "Report":
                tv_time_periodic_container.setVisibility(View.GONE);
                tv_price_container.setVisibility(View.GONE);
                container_price.setVisibility(View.GONE);
                btnAgreement.setVisibility(View.GONE);
                txtPtname.setVisibility(View.VISIBLE);
                txt_report_request_date.setText(dataNotif.getCreated_at());
                txtPtname.setText(dataNotif.getNama_perusahaan());
                txtPriority.setText(dataNotif.getPriority());
                txtAppname.setText(dataNotif.getApps_name());
                txtSubject.setText(dataNotif.getSubject());
                txtDetail.setText(dataNotif.getDetail());
                txtTitle.setText("Bug Report");
                String status = dataNotif.getStatus();
                datePicker(etyear, etmonth, etday);
                if (status.equals("Reported")) {
                    btnAssign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                            if (!datePick.equals("--")) {
                                Intent toStafflist = new Intent(DetailActivity.this, StaffListActivity.class);
                                toStafflist.putExtra(StaffListActivity.EXTRA_NOTIF, dataNotif);
                                toStafflist.putExtra(StaffListActivity.EXTRA_DATE, datePick);
                                startActivity(toStafflist);
                            } else {
                                new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Please input the deadline")
                                        .setConfirmText("Ok")
                                        .setConfirmButtonBackgroundColor(Color.parseColor("#FFFF9800"))
                                        .show();
                            }
                        }
                    });
                } else if(status.equals("On Proccess")){
                    txtAprovalStat.setVisibility(View.VISIBLE);
                    txtAprovalStat.setText("Assigned");
                    txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                    container.setVisibility(View.GONE);
                }
                else {
                    container.setVisibility(View.INVISIBLE);
                    tv_container_assignDate.setVisibility(View.VISIBLE);
                    txt_assignDate.setText(dataNotif.getAssign_at());
                    tv_container_completed_at.setVisibility(View.VISIBLE);
                    txt_completed_date.setText(dataNotif.getUpdated_at());
                    txtAprovalStat.setVisibility(View.VISIBLE);
                    txtAprovalStat.setText(dataNotif.getStatus());
                    txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                }
                break;

            case "Request":
                txtPtname.setVisibility(View.VISIBLE);
                txtPtname.setText(dataNotif.getNama_perusahaan());
                txtPriority.setText(dataNotif.getPriority());
                txtAppname.setText(dataNotif.getApps_name());
                txtSubject.setText(dataNotif.getSubject());
                tv_report_request.setText("Requested at");
                txt_report_request_date.setText(dataNotif.getCreated_at());
                txtDetail.setText(dataNotif.getDetail());
                txtDeadlineOrTimePeriodic.setText("Time Periodic :");
                txtTitle.setText("Feature Request");
                String statusFitur = dataNotif.getStatus();
                datePicker(etyear, etmonth, etday);
                String aproval_stat = dataNotif.getAproval_stat();
                if (aproval_stat!=null && aproval_stat.equals("Ignored")){
                    txtAprovalStat.setText("Ignored");
                    txtAprovalStat.setTextColor(this.getResources().getColor(R.color.RedDefault));
                    txtAprovalStat.setVisibility(View.VISIBLE);
                }else if(aproval_stat!=null && aproval_stat.equals("Accepted")){
                    txtAprovalStat.setText("Accepted");
                    txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                    txtAprovalStat.setVisibility(View.VISIBLE);
                }
                if (statusFitur.equals("Requested")) {
                    btnAssign.setVisibility(View.GONE);
                    tv_time_periodic_container.setVisibility(View.GONE);
                    tv_price_container.setVisibility(View.GONE);
                    btnAgreement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Loading");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                            if (!datePick.equals("--") && etPrice.getCleanIntValue() != 0) {
                                ApiService api = ApiClient.getClient().create(ApiService.class);
                                Call<ResponseBody> makeAgreement = api.makeAgreement(token, dataNotif.getId_ticket(), etPrice.getCleanIntValue(), datePick, "Need Agreement");
                                makeAgreement.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        pDialog.dismiss();
                                        if (response.isSuccessful() && etPrice.getText().toString() != null) {
                                            try {
                                                SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                                sweet.setTitleText("Success");
                                                sweet.setContentText("Aproval was submitted");
                                                sweet.setCanceledOnTouchOutside(false);
                                                sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        NotificationActivity.getInstance().setPage(1);
                                                        NotificationActivity.getInstance().getListnotif().clear();
                                                        NotificationActivity.getInstance().addListNotification(id_user);
                                                        finish();
                                                    }
                                                });
                                                sweet.show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            Log.d("401", "result : " + response.body());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        pDialog.dismiss();
                                        Log.d("detailactivity", "" + t.getMessage());
                                    }
                                });
                            }
                            else {
                                pDialog.dismiss();
                                new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Please input the blank field")
                                        .setConfirmText("Ok")
                                        .setConfirmButtonBackgroundColor(Color.parseColor("#FFFF9800"))
                                        .show();
                            }
                        }
                    });
                }
                else if (statusFitur.equals("Agreement Accepted")) {
                    btnAgreement.setVisibility(View.GONE);
                    containerAdminAct.setVisibility(View.GONE);
                    txtAprovalStat.setText("Accepted");
                    final String time_periodic = dataNotif.getTime_periodic();
                    int price = dataNotif.getPrice();
                    txtTimePeriodic.setText(time_periodic);
                    txtPrice.setText(toRupiah(String.valueOf(price)));
                    txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                    txtAprovalStat.setVisibility(View.VISIBLE);
                    btnAssign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent toStafflist = new Intent(DetailActivity.this, StaffListActivity.class);
                            toStafflist.putExtra(StaffListActivity.EXTRA_NOTIF, dataNotif);
                            toStafflist.putExtra(StaffListActivity.EXTRA_DATE, time_periodic);
                            startActivity(toStafflist);

                        }
                    });
                }else if (statusFitur.equals("Need Agreement")) {
                    final String time_periodic = dataNotif.getTime_periodic();
                    int price = dataNotif.getPrice();
                    txtTimePeriodic.setText(time_periodic);
                    txtPrice.setText(toRupiah(String.valueOf(price)));
                    container.setVisibility(View.GONE);
                }
                else {
                    txtPrice.setText(String.valueOf(dataNotif.getPrice()));
                    txtTimePeriodic.setText(dataNotif.getTime_periodic());
                    container.setVisibility(View.INVISIBLE);

                }
                break;
        }
    }

    public void markAsComplete(final String token, final String id_ticket, final String id_user, final String extra_condition){
        btnStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
                ApiService api = ApiClient.getClient().create(ApiService.class);
                Call<ResponseBody> markComplete = api.markAsComplete("Bearer "+token, id_ticket);
                markComplete.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            pDialog.dismiss();
                            SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            sweet.setTitleText("Marked as Complete");
                            sweet.setCanceledOnTouchOutside(false);
                            sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent goBack = new Intent(DetailActivity.this, TwkStaffActivity.class);
                                    goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(goBack);
                                    finish();
                                }
                            });
                            sweet.show();
                        }else {
                            pDialog.dismiss();
                            Toast.makeText(DetailActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pDialog.dismiss();
                        SweetAlertDialog sweet = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweet.setCustomImage(R.drawable.ic_sad);
                        sweet.setTitleText("Oppss");
                        sweet.setContentText("Please check your internet connection");
                        sweet.show();
                    }
                });
            }
        });
    }

    private void initialize() {
        txtPriority = findViewById(R.id.priorityDetail);
        txtAppname = findViewById(R.id.appnameDetail);
        txtSubject = findViewById(R.id.subjectDetail);
        txtDetail = findViewById(R.id.detail_content);
        txtTitle = findViewById(R.id.item_type);
        btnAssign = findViewById(R.id.btn_assign);
        btnStaff = findViewById(R.id.btn_staff);
        etyear = findViewById(R.id.et_year);
        etmonth = findViewById(R.id.et_month);
        etday = findViewById(R.id.et_day);
        btnOpenDate = findViewById(R.id.btn_openDate);
        container = findViewById(R.id.containerAssign);
        container_price = findViewById(R.id.container_price);
        btnAgreement = findViewById(R.id.btn_agreement);
        etPrice = findViewById(R.id.price);
        clientAgreeDisagree = findViewById(client_ageedisaree);
        txtDeadlineOrTimePeriodic = findViewById(R.id.txt_deadlineOrTimePeriodic);
        containerAdminAct = findViewById(R.id.container_adminAction);
        det_toolbar = findViewById(R.id.tlbar_detail);
        txtAprovalStat = findViewById(R.id.aprovalStat);
        txtPtname = findViewById(R.id.pt_name);
        txtDeadlineStaff = findViewById(R.id.txtDeadlineStaff);
        rowDeadlineStaff = findViewById(R.id.rowDeadlineStaff);
        btnClientAgree = findViewById(R.id.btn_agree);
        btnClientIgnore = findViewById(R.id.btn_ignore);
        tv_time_periodic_container = findViewById(R.id.tv_time_periodic_container);
        tv_price_container = findViewById(R.id.tv_price_container);
        txtTimePeriodic = findViewById(R.id.tv_time_periodic);
        txtPrice = findViewById(R.id.tv_price);
        tv_container_assignDate = findViewById(R.id.container_assign_at);
        txt_assignDate = findViewById(R.id.tv_assigned_date);
        txt_report_request_date = findViewById(R.id.tv_report_request_date);
        tv_container_report_requestDate = findViewById(R.id.container_reported_request_at);
        tv_container_completed_at = findViewById(R.id.container_completed_at);
        txt_completed_date = findViewById(R.id.tv_completed_date);
        tv_report_request = findViewById(R.id.tv_report_request);
        txt_clientstaff_remember = findViewById(R.id.client_staff_disable_act);
    }

    private String toRupiah(String nominal){
        String hasil = "";

        DecimalFormat toRupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatAngka = new DecimalFormatSymbols();

        formatAngka.setCurrencySymbol("Rp. ");
        formatAngka.setMonetaryDecimalSeparator(',');
        formatAngka.setGroupingSeparator('.');
        toRupiah.setDecimalFormatSymbols(formatAngka);

        hasil = toRupiah.format(Double.valueOf(nominal));

        return hasil;
    }
}