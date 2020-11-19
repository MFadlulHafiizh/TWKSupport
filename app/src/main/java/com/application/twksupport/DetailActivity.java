package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.adapter.RvStaffListAdapter;
import com.application.twksupport.auth.MainActivity;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.FeatureData;
import com.application.twksupport.model.StaffResponse;
import com.application.twksupport.model.TokenResponse;
import com.application.twksupport.model.UserData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private TextView txtPriority, txtAppname, txtSubject, txtDetail, txtTitle;
    private Button btnAssign;
    private LinearLayout container;
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

        if (getIntent().hasExtra(EXTRA_BUG)){
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
            if (status.equals("Requested")){
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
    }
}