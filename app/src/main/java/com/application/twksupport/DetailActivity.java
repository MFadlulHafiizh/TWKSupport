package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.FeatureData;
import java.util.Calendar;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.twksupport.R.id.client_ageedisaree;
import static com.application.twksupport.R.id.containerAssign;

public class DetailActivity extends AppCompatActivity {
    private TextView txtPriority, txtAppname, txtSubject, txtDetail, txtTitle, txtDeadlineOrTimePeriodic;
    private Button btnAssign, btnAgreement;
    private EditText etPrice;
    private LinearLayout container, container_price, containerAdminAct, clientAgreeDisagree;
    public static final String EXTRA_BUG = "extra_bug";
    public static final String EXTRA_FEATURE = "extra_feature";
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

        final BugsData bugsData = getIntent().getParcelableExtra(EXTRA_BUG);
        final FeatureData fiturData = getIntent().getParcelableExtra(EXTRA_FEATURE);
        final SharedPreferences _objpref = getSharedPreferences("JWTTOKEN", 0);
        final SharedPreferences role = getSharedPreferences("userInformation", 0);
        final String getToken = _objpref.getString("jwttoken", "");
        final String getRole = role.getString("role", "");
        Log.d("roleDetail", ""+getRole);

        switch (getRole){
            case "twk-head":
                clientAgreeDisagree.setVisibility(View.GONE);
                if (getIntent().hasExtra(EXTRA_BUG)){
                    container_price.setVisibility(View.GONE);
                    btnAgreement.setVisibility(View.GONE);
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
                    txtPriority.setText(fiturData.getPriority());
                    txtAppname.setText(fiturData.getApps_name());
                    txtSubject.setText(fiturData.getSubject());
                    txtDetail.setText(fiturData.getDetail());
                    txtTitle.setText("Feature Request");
                    String aprovalStat = fiturData.getAproval_stat();
                    String status = fiturData.getStatus();
                    datePicker(etyear, etmonth, etday);
                    if(status.equals("Requested") && aprovalStat == null){
                        btnAssign.setVisibility(View.GONE);
                        btnAgreement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String datePick = etyear.getText().toString() + "-" + etmonth.getText().toString() + "-" + etday.getText().toString();
                                if (!datePick.equals("--")){
                                    ApiService api = ApiClient.getClient().create(ApiService.class);
                                    Call<ResponseBody> makeAgreement = api.makeAgreement("Bearer "+getToken,fiturData.getId_ticket(), etPrice.getText().toString(), datePick, "Need Agreement");
                                    makeAgreement.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()){
                                                try {
                                                    String JSONResponse = response.body().string();
                                                    Toast.makeText(DetailActivity.this, ""+JSONResponse, Toast.LENGTH_SHORT).show();
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
                                            Log.d("detailactivity", ""+t.getMessage());
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else if (status.equals("Requested") && aprovalStat != null){
                        btnAgreement.setVisibility(View.GONE);
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
                break;

            case "client-head":
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
                break;
        }

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

    private void initialize() {
        txtPriority = findViewById(R.id.priorityDetail);
        txtAppname = findViewById(R.id.appnameDetail);
        txtSubject = findViewById(R.id.subjectDetail);
        txtDetail = findViewById(R.id.detail_content);
        txtTitle = findViewById(R.id.item_type);
        btnAssign = findViewById(R.id.btn_assign);
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
    }
}