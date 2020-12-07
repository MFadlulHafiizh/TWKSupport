package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.application.twksupport.model.TodoData;

import java.util.Calendar;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;
import static com.application.twksupport.R.id.client_ageedisaree;

public class DetailActivity extends AppCompatActivity {
    private TextView txtPriority, txtAppname, txtSubject, txtDetail, txtTitle, txtDeadlineOrTimePeriodic, txtAprovalStat, txtPtname,txtDeadlineStaff;
    private TableRow rowDeadlineStaff;
    private Button btnAssign, btnAgreement, btnStaff;
    private EditText etPrice;
    private Toolbar det_toolbar;
    private LinearLayout container, container_price, containerAdminAct, clientAgreeDisagree;
    public static final String EXTRA_BUG = "extra_bug";
    public static final String EXTRA_FEATURE = "extra_feature";
    public static final String EXTRA_DONE = "extra_done";
    public static final String EXTRA_JOBS = "extra_jobs";
    public static final String EXTRA_NOTIF = "extra_notif";
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
        final SharedPreferences _objpref = getSharedPreferences("JWTTOKEN", 0);
        final SharedPreferences role = getSharedPreferences("userInformation", 0);
        final String getToken = _objpref.getString("jwttoken", "");
        final String getRole = role.getString("role", "");
        Log.d("roleDetail", ""+getRole);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDetail.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        switch (getRole){
            case "twk-head":
                clientAgreeDisagree.setVisibility(View.GONE);
                if (getIntent().hasExtra(EXTRA_BUG)){
                    container_price.setVisibility(View.GONE);
                    btnAgreement.setVisibility(View.GONE);
                    txtPtname.setVisibility(View.VISIBLE);
                    txtPtname.setText(bugsData.getNama_perusahaan());
                    txtPriority.setText(bugsData.getPriority());
                    txtAppname.setText(bugsData.getApps_name());
                    txtSubject.setText(bugsData.getSubject());
                    txtDetail.setText(bugsData.getDetail());
                    txtTitle.setText("Bug Report");
                    String status = bugsData.getStatus();
                    datePicker(etyear, etmonth, etday);
                    if (status.equals("Reported")) {
                        btnAssign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                                if (!datePick.equals("--")){
                                    Intent toStafflist = new Intent(DetailActivity.this, StaffListActivity.class);
                                    toStafflist.putExtra(StaffListActivity.EXTRA_TICKET_BUG, bugsData);
                                    toStafflist.putExtra(StaffListActivity.EXTRA_DATE, datePick);
                                    startActivity(toStafflist);
                                }
                                else{
                                    new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Please input the deadline")
                                            .setConfirmText("Ok")
                                            .setConfirmButtonBackgroundColor(Color.parseColor("#FFFF9800"))
                                            .show();
                                }
                            }
                        });
                    }
                    else {
                        container.setVisibility(View.INVISIBLE);
                    }
                }
                else if (getIntent().hasExtra(EXTRA_FEATURE)){
                    txtPtname.setVisibility(View.VISIBLE);
                    txtPtname.setText(fiturData.getNama_perusahaan());
                    txtPriority.setText(fiturData.getPriority());
                    txtAppname.setText(fiturData.getApps_name());
                    txtSubject.setText(fiturData.getSubject());
                    txtDetail.setText(fiturData.getDetail());
                    txtDeadlineOrTimePeriodic.setText("Time Periodic :");
                    txtTitle.setText("Feature Request");
                    String aprovalStat = fiturData.getAproval_stat();
                    Log.d("aproval", ""+aprovalStat);
                    String status = fiturData.getStatus();
                    datePicker(etyear, etmonth, etday);
                    if(status.equals("Requested") && aprovalStat == null){
                        btnAssign.setVisibility(View.GONE);
                        btnAgreement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final SweetAlertDialog pDialog = new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Loading");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                                if (!datePick.equals("--")){
                                    ApiService api = ApiClient.getClient().create(ApiService.class);
                                    Call<ResponseBody> makeAgreement = api.makeAgreement("Bearer "+getToken,fiturData.getId_ticket(), etPrice.getText().toString(), datePick, "Need Agreement");
                                    makeAgreement.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            pDialog.dismiss();
                                            if (response.isSuccessful() && etPrice.getText().toString() != null){
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
                                                            finish();
                                                        }
                                                    });
                                                    sweet.show();
                                                }
                                                catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                Log.d("401", "result : "+response.body());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            pDialog.dismiss();
                                            Log.d("detailactivity", ""+t.getMessage());
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else if (status.equals("Requested") && aprovalStat.equals("Accepted")){
                        btnAgreement.setVisibility(View.GONE);
                        containerAdminAct.setVisibility(View.GONE);
                        txtAprovalStat.setText(aprovalStat);
                        txtAprovalStat.setTextColor(this.getResources().getColor(R.color.greenFigma));
                        txtAprovalStat.setVisibility(View.VISIBLE);
                        btnAssign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                                if (!datePick.equals("--")){
                                    Intent toStafflist = new Intent(DetailActivity.this, StaffListActivity.class);
                                    toStafflist.putExtra(StaffListActivity.EXTRA_TICKET_FITUR, fiturData);
                                    toStafflist.putExtra(StaffListActivity.EXTRA_DATE, datePick);
                                    startActivity(toStafflist);
                                }
                                else{
                                    new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Please input the deadline")
                                            .setConfirmText("Ok")
                                            .setConfirmButtonBackgroundColor(Color.parseColor("#FFFF9800"))
                                            .show();
                                }
                            }
                        });
                    }
                    else {
                        container.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    setDetailDone(doneData);
                }
                break;

            case "twk-staff":
                container.setVisibility(View.GONE);
                rowDeadlineStaff.setVisibility(View.VISIBLE);
                if (getIntent().hasExtra(EXTRA_JOBS)){
                    btnStaff.setVisibility(View.VISIBLE);
                    txtPtname.setVisibility(View.VISIBLE);
                    if (jobsData.getType().equals("Report")){
                        txtTitle.setText("Bug Report");
                    }else {
                        txtTitle.setText("Feature Request");
                    }
                    txtDeadlineStaff.setText("Deadline : "+jobsData.getDeadline());
                    txtPtname.setText(jobsData.getNama_perusahaan());
                    txtPriority.setText(jobsData.getPriority());
                    txtAppname.setText(jobsData.getApps_name());
                    txtSubject.setText(jobsData.getSubject());
                    txtDetail.setText(jobsData.getDetail());
                }
                else{
                    txtPtname.setVisibility(View.VISIBLE);
                    if (doneData.getType().equals("Report")){
                        txtTitle.setText("Bug Report");
                    }else {
                        txtTitle.setText("Feature Request");
                    }
                    txtPtname.setText(doneData.getNama_perusahaan());
                    txtPriority.setText(doneData.getPriority());
                    txtAppname.setText(doneData.getApps_name());
                    txtSubject.setText(doneData.getSubject());
                    txtDetail.setText(doneData.getDetail());
                }
                break;

            default:
                container_price.setVisibility(View.GONE);
                if (getIntent().hasExtra(EXTRA_BUG)){
                    container.setVisibility(View.GONE);
                    txtPriority.setText(bugsData.getPriority());
                    txtAppname.setText(bugsData.getApps_name());
                    txtSubject.setText(bugsData.getSubject());
                    txtDetail.setText(bugsData.getDetail());
                    txtTitle.setText("Bug Report");
                }
                else if (getIntent().hasExtra(EXTRA_FEATURE)){
                    txtPriority.setText(fiturData.getPriority());
                    txtAppname.setText(fiturData.getApps_name());
                    txtSubject.setText(fiturData.getSubject());
                    txtDetail.setText(fiturData.getDetail());
                    txtTitle.setText("Feature Request");
                    String status = fiturData.getStatus();
                    String aprovalStat = fiturData.getAproval_stat();
                    if (status.equals("Need Agreement")){
                        btnAssign.setVisibility(View.GONE);
                        btnAgreement.setVisibility(View.GONE);
                        containerAdminAct.setVisibility(View.GONE);
                    }
                    else {
                        container.setVisibility(View.GONE);
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
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return true;
    }

    private void datePicker(final EditText dateYear, EditText dateMonth, EditText dateDay) {
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
                etyear.setText(tahun);
                etmonth.setText(bulan);
                etday.setText(hari);
            }
        };

    }

    private void setDetailDone(DoneData doneData){
        container.setVisibility(View.GONE);
        txtPriority.setText(doneData.getPriority());
        txtAppname.setText(doneData.getApps_name());
        txtSubject.setText(doneData.getSubject());
        txtDetail.setText(doneData.getDetail());
        String getType = doneData.getType();
        switch (getType){
            case "Report":
                txtTitle.setText("Bug Report");
                break;

            case "Request":
                txtTitle.setText("Feature Request");
                break;
        }
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
    }

}