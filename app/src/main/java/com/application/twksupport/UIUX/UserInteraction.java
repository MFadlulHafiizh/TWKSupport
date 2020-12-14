package com.application.twksupport.UIUX;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.application.twksupport.BugsFragment;
import com.application.twksupport.DetailActivity;
import com.application.twksupport.DoneFragment;
import com.application.twksupport.FeatureFragment;
import com.application.twksupport.NotificationActivity;
import com.application.twksupport.R;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.StaffListActivity;
import com.application.twksupport.StaffToDoFragment;
import com.application.twksupport.UserActivity;
import com.application.twksupport.auth.MainActivity;
import com.application.twksupport.model.AppsUserData;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserAppManager;
import com.application.twksupport.model.UserManager;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInteraction extends AppCompatActivity {
    Dialog popUpFilter;
    TextView btmSheetTitle;
    String apps_name = null;
    String assigned = null;
    DatePickerDialog.OnDateSetListener setListenerFrom;
    DatePickerDialog.OnDateSetListener setListenerUntil;
    private List<AppsUserData> listAppData = new ArrayList<>();

    public void showPopupFilter(final Context appContext, final String extra_code) {
        //getUsedData
        SharedPreferences getUserinfo = appContext.getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = appContext.getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "missing");
        final String id_user = getUserinfo.getString("id", "");
        final String role = getUserinfo.getString("role", "");
        Spinner spinPriorityFiler, spinAppnameFilter;
        final UserAppManager userAppManager = new UserAppManager(appContext);
        final String[] priority = new String[1];

        //initialize
        popUpFilter = new Dialog(appContext, R.style.AppBottomSheetDialogTheme);
        popUpFilter.setContentView(R.layout.filter_popup);
        popUpFilter.setCanceledOnTouchOutside(false);
        Button reset, apply;
        final ImageButton close, dateFrom, dateUntil;
        final CheckBox ckAssignTicket;
        final EditText etYearFr, etMonthFr, etDayFr, etYearUn, etMonthUn, etDayUn;
        final LinearLayout containerTwkAct;
        spinPriorityFiler = popUpFilter.findViewById(R.id.priorityFilter);
        spinAppnameFilter = popUpFilter.findViewById(R.id.appNameFilter);
        reset = popUpFilter.findViewById(R.id.btnResetFilter);
        apply = popUpFilter.findViewById(R.id.btnApplyFilter);
        close = popUpFilter.findViewById(R.id.btnCloseFilter);
        dateFrom = popUpFilter.findViewById(R.id.btn_openDate_filter_from);
        dateUntil = popUpFilter.findViewById(R.id.btn_openDate_filter_until);
        ckAssignTicket = popUpFilter.findViewById(R.id.filter_assigned_ticket);
        etYearFr = popUpFilter.findViewById(R.id.et_year_filter_from);
        etMonthFr = popUpFilter.findViewById(R.id.et_month_filter_from);
        etDayFr = popUpFilter.findViewById(R.id.et_day_filter_from);
        etYearUn = popUpFilter.findViewById(R.id.et_year_filter_until);
        etMonthUn = popUpFilter.findViewById(R.id.et_month_filter_until);
        etDayUn = popUpFilter.findViewById(R.id.et_day_filter_until);
        containerTwkAct = popUpFilter.findViewById(R.id.container_twkact);

        //spinnerSet
        spinnerPriority(appContext, spinPriorityFiler);
        setFilterSpinnerApps(appContext, spinAppnameFilter);
        spinPriorityFiler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    priority[0] = null;
                } else {
                    String priorityValue = parent.getItemAtPosition(position).toString();
                    priority[0] = priorityValue;
                    Log.d("value", "" + priority[0]);
                    userAppManager.setPriority(priority[0]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Handler delay = new Handler();

        popUpFilter.show();

        //clickEvent
        ckAssignTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ckAssignTicket.isChecked()){
                    assigned = "On Proccess";
                }else{
                    assigned = null;
                }
            }
        });

        if (role.equals("twk-head")){
            if (!extra_code.equals("extra_done")){
                ckAssignTicket.setVisibility(View.VISIBLE);
            }
        }
        else if(!extra_code.equals("extra_notif") && role.equals("client-head") || role.equals("client-staff")){
            containerTwkAct.setVisibility(View.GONE);
        }

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        appContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , setListenerFrom, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        setListenerFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;
                String tahun = "" + year;
                String bulan = "" + month;
                String hari = "" + dayOfMonth;
                etYearFr.setText(tahun);
                etMonthFr.setText(bulan);
                etDayFr.setText(hari);
            }
        };

        datePicker(appContext, etYearUn, etMonthUn, etDayUn, dateUntil);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateFromValue = etYearFr.getText().toString() + "-" + etMonthFr.getText().toString() + "-" + etDayFr.getText().toString();
                String dateUntilValue = etYearUn.getText().toString() + "-" + etMonthUn.getText().toString() + "-" + etDayUn.getText().toString();
                switch (extra_code) {
                    case "extra_bugs":
                        switch (role) {
                            case "twk-head":
                                BugsFragment.getInstance().setPage(1);
                                BugsFragment.getInstance().getListBugs().clear();
                                BugsFragment.getInstance().setPriority(priority[0]);
                                BugsFragment.getInstance().setApps_name(apps_name);
                                BugsFragment.getInstance().setAssigned(assigned);
                                BugsFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                BugsFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                if (dateFromValue.equals("--") && dateUntilValue.equals("--")){

                                }else{
                                    BugsFragment.getInstance().setFromDate(dateFromValue);
                                    BugsFragment.getInstance().setUntilDate(dateUntilValue);
                                }
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BugsFragment.getInstance().addListAdminBug();
                                        assigned = null;
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;

                            default:
                                BugsFragment.getInstance().setPage(1);
                                BugsFragment.getInstance().getListBugs().clear();
                                BugsFragment.getInstance().setPriority(priority[0]);
                                BugsFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                BugsFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                BugsFragment.getInstance().setApps_name(apps_name);
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BugsFragment.getInstance().addListDataBugsUser();
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;
                        }

                        break;

                    case "extra_feature":
                        switch (role){
                            case "twk-head":
                                FeatureFragment.getInstance().setPage(1);
                                FeatureFragment.getInstance().getListFeature().clear();
                                FeatureFragment.getInstance().setPriority(priority[0]);
                                FeatureFragment.getInstance().setApps_name(apps_name);
                                FeatureFragment.getInstance().setAssigned(assigned);
                                FeatureFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                FeatureFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                if (dateFromValue.equals("--") && dateUntilValue.equals("--")){

                                }else{
                                    FeatureFragment.getInstance().setFromDate(dateFromValue);
                                    FeatureFragment.getInstance().setUntilDate(dateUntilValue);
                                }
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        FeatureFragment.getInstance().addListAdminFeature();
                                        assigned = null;
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;

                            default:
                                FeatureFragment.getInstance().setPage(1);
                                FeatureFragment.getInstance().getListFeature().clear();
                                FeatureFragment.getInstance().setPriority(priority[0]);
                                FeatureFragment.getInstance().setApps_name(apps_name);
                                FeatureFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                FeatureFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        FeatureFragment.getInstance().addListDataFeatureUser();
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;
                        }

                        break;

                    case "extra_done":
                        switch (role){
                            case "twk-head":
                                DoneFragment.getInstance().setPage(1);
                                DoneFragment.getInstance().getListDone().clear();
                                DoneFragment.getInstance().setPriority(priority[0]);
                                DoneFragment.getInstance().setApps_name(apps_name);
                                DoneFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                DoneFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                if (dateFromValue.equals("--") && dateUntilValue.equals("--")){

                                }else{
                                    DoneFragment.getInstance().setFromDate(dateFromValue);
                                    DoneFragment.getInstance().setUntilDate(dateUntilValue);
                                }
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoneFragment.getInstance().addListDoneAdmin();
                                        assigned = null;
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;

                            case "twk-staff":
                                DoneFragment.getInstance().setPage(1);
                                DoneFragment.getInstance().getListDone().clear();
                                DoneFragment.getInstance().setPriority(priority[0]);
                                DoneFragment.getInstance().setApps_name(apps_name);
                                DoneFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                DoneFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                if (dateFromValue.equals("--") && dateUntilValue.equals("--")){

                                }else{
                                    DoneFragment.getInstance().setFromDate(dateFromValue);
                                    DoneFragment.getInstance().setUntilDate(dateUntilValue);
                                }
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoneFragment.getInstance().addListStaffHasDone();
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;

                            default:
                                DoneFragment.getInstance().setPage(1);
                                DoneFragment.getInstance().getListDone().clear();
                                DoneFragment.getInstance().setPriority(priority[0]);
                                DoneFragment.getInstance().setApps_name(apps_name);
                                DoneFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                                DoneFragment.getInstance().getmAdapter().notifyDataSetChanged();
                                delay.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoneFragment.getInstance().addListDataDone();
                                    }
                                }, 20);
                                popUpFilter.dismiss();
                                break;
                        }
                        break;

                    case "extra_jobs":
                        StaffToDoFragment.getInstance().setPage(1);
                        StaffToDoFragment.getInstance().getListjobs().clear();
                        StaffToDoFragment.getInstance().setPriority(priority[0]);
                        StaffToDoFragment.getInstance().setApps_name(apps_name);
                        StaffToDoFragment.getInstance().getmAdapter().notifyDataSetChanged();
                        StaffToDoFragment.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                        if (dateFromValue.equals("--") && dateUntilValue.equals("--")){

                        }else{
                            StaffToDoFragment.getInstance().setFromDate(dateFromValue);
                            StaffToDoFragment.getInstance().setUntilDate(dateUntilValue);
                        }
                        delay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StaffToDoFragment.getInstance().getJobs();
                            }
                        }, 20);
                        popUpFilter.dismiss();
                        break;

                    case "extra_notif":
                        NotificationActivity.getInstance().setPage(1);
                        NotificationActivity.getInstance().getListnotif().clear();
                        NotificationActivity.getInstance().setPriority(priority[0]);
                        NotificationActivity.getInstance().setApps_name(apps_name);
                        NotificationActivity.getInstance().getmAdapter().notifyDataSetChanged();
                        NotificationActivity.getInstance().getSwipeRefreshLayout().setRefreshing(true);
                        if (dateFromValue.equals("--") && dateUntilValue.equals("--")){

                        }else{
                            NotificationActivity.getInstance().setFromDate(dateFromValue);
                            NotificationActivity.getInstance().setUntilDate(dateUntilValue);
                        }
                        delay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                NotificationActivity.getInstance().addListNotification(id_user);
                            }
                        }, 20);
                        popUpFilter.dismiss();
                        break;
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpFilter.dismiss();
            }
        });
    }

    private void datePicker(final Context appcontext, final EditText dateYear, final EditText dateMonth, final EditText dateDay, ImageButton btn_open_calendar) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        btn_open_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        appcontext, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , setListenerUntil, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListenerUntil = new DatePickerDialog.OnDateSetListener() {
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

    public void setFilterSpinnerApps(final Context appContext, final Spinner spinAppName) {
        SharedPreferences getUserInformation = appContext.getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = appContext.getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "missing");
        int idCompany = getUserInformation.getInt("id_perushaan", 0);
        String id_user = getUserInformation.getString("id", "");
        String role = getUserInformation.getString("role", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        switch (role) {
            case "twk-head":
                Call<ResponseData> getTicketApps = api.getAllTicketApps("Bearer " + getToken);
                getTicketApps.enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (response.isSuccessful()) {
                            listAppData = response.body().getUserApp();
                            final List<String> listapps = new ArrayList<>();
                            listapps.add(0, "Choose apps");
                            for (int i = 0; i < listAppData.size(); i++) {
                                listapps.add(listAppData.get(i).getApps_name());
                            }
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(appContext, R.layout.spinner_style, listapps);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinAppName.setAdapter(spinnerAdapter);
                            spinAppName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    //String app = listAppData.get(position).getApps_name();
                                    if (position == 0) {
                                        apps_name = null;
                                    } else {
                                        String app = listapps.get(position);
                                        Log.d("appsselect", "id : " + app);
                                        apps_name = app;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                            Log.d("BottomSheet", "" + listAppData);
                        } else {
                            Toast.makeText(appContext, "error when getting apps", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Toast.makeText(appContext, "error when getting apps, please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case "twk-staff":
                Call<ResponseData> getAssignedApps = api.getAssignedApps("Bearer "+getToken, id_user);
                getAssignedApps.enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (response.isSuccessful()){
                            listAppData = response.body().getUserApp();
                            final List<String> listapps = new ArrayList<>();
                            listapps.add(0, "Choose apps");
                            for (int i = 0; i < listAppData.size(); i++) {
                                listapps.add(listAppData.get(i).getApps_name());
                            }
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(appContext, R.layout.spinner_style, listapps);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinAppName.setAdapter(spinnerAdapter);
                            spinAppName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    //String app = listAppData.get(position).getApps_name();
                                    if (position == 0) {
                                        apps_name = null;
                                    } else {
                                        String app = listapps.get(position);
                                        Log.d("appsselect", "id : " + app);
                                        apps_name = app;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                            Log.d("BottomSheet", "" + listAppData);
                        } else {
                            Toast.makeText(appContext, "error when getting apps", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Toast.makeText(appContext, "error when getting apps, please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            default:
                Call<ResponseData> getApps = api.getUserApps(idCompany, "Bearer " + getToken);
                getApps.enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (response.isSuccessful()) {
                            listAppData = response.body().getUserApp();
                            final List<String> listapps = new ArrayList<>();
                            listapps.add(0, "Choose apps");
                            for (int i = 0; i < listAppData.size(); i++) {
                                listapps.add(listAppData.get(i).getApps_name());
                            }
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(appContext, R.layout.spinner_style, listapps);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinAppName.setAdapter(spinnerAdapter);
                            spinAppName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    //String app = listAppData.get(position).getApps_name();
                                    if (position == 0) {
                                        apps_name = null;
                                    } else {
                                        String app = listapps.get(position);
                                        Log.d("appsselect", "id : " + app);
                                        apps_name = app;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                            Log.d("BottomSheet", "" + listAppData);
                        }else {
                            Toast.makeText(appContext, "error when getting apps", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Toast.makeText(appContext, "error when getting apps, please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    public void spinnerPriority(Context context, Spinner spinner) {
        String priority[] = {"Choose Priority", "Low", "Middle", "High"};
        ArrayAdapter<String> spinner1Adapter = new ArrayAdapter<String>(context, R.layout.spinner_style, priority);
        spinner.setAdapter(spinner1Adapter);
    }

    public void setBlurBackground(boolean state, BlurView blurView, View getViewDecor, Context appContext) {
        float radius = 2;

        View decorView = getViewDecor;
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        if (state == true) {
            blurView.setupWith(rootView)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurAlgorithm(new RenderScriptBlur(appContext))
                    .setBlurRadius(radius)
                    .setHasFixedTransformationMatrix(false);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(appContext, R.anim.fade_in);
            blurView.setAnimation(fadeInAnimation);
            blurView.setBlurEnabled(true);
        } else {
            blurView.setBlurEnabled(false);
        }
    }

    public void showBottomSheet(final Context appContext, final FloatingActionsMenu fabMenu, final BlurView blurView, LayoutInflater yourLayout, String title, final String type) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(appContext, R.style.AppBottomSheetDialogTheme);
        View content = yourLayout.inflate(R.layout.bottom_sheet, null);
        final Spinner spinPriority = (Spinner) content.findViewById(R.id.prioritySpinner);
        final Spinner spinAppName = (Spinner) content.findViewById(R.id.appnameSpin);
        Button btnReportRequest = (Button) content.findViewById(R.id.btn_report_request);
        final UserAppManager userAppManager = new UserAppManager(appContext);
        bottomSheetDialog.setContentView(content);
        final EditText etSubject = bottomSheetDialog.findViewById(R.id.edtSubject);
        final EditText etDetails = bottomSheetDialog.findViewById(R.id.edtDetails);
        btmSheetTitle = bottomSheetDialog.findViewById(R.id.request_type);
        btmSheetTitle.setText(title);
        switch (type) {
            case "report":
                btnReportRequest.setText("Report");
                break;

            case "request":
                btnReportRequest.setText("Request");
                break;
        }
        btmSheetTitle.setText(title);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                fabMenu.collapse();
                blurView.setBlurEnabled(true);
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                blurView.setBlurEnabled(false);
            }
        });

        SharedPreferences getCompanyUser = appContext.getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = appContext.getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "missing");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getApps = api.getUserApps(idCompany, "Bearer " + getToken);
        final String[] priority = new String[1];
        bottomSheetDialog.show();
        getApps.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    Log.d("BottomSheet", "" + response.body().getUserApp());
                    listAppData = response.body().getUserApp();

                    //SpinnerPriority
                    spinnerPriority(appContext, spinPriority);
                    spinPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (adapterView.getItemAtPosition(i).equals("Choose Priority")) {
                                userAppManager.setPriority(null);
                            } else {
                                String priorityValue = adapterView.getItemAtPosition(i).toString();
                                priority[0] = priorityValue;
                                Log.d("value", "" + priority[0]);
                                userAppManager.setPriority(priority[0]);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    //SpinnerApp
                    ArrayAdapter<AppsUserData> spinnerAdapter = new ArrayAdapter<AppsUserData>(appContext, R.layout.spinner_style, listAppData);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinAppName.setAdapter(spinnerAdapter);
                    spinAppName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            int id_apps = listAppData.get(position).getId_apps();
                            Log.d("appsselect", "id : " + id_apps);
                            userAppManager.setIdApps(id_apps);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    Log.d("BottomSheet", "" + listAppData);

                } else {
                    Log.d("BottomSheet", "" + response.body());
                    Toast.makeText(appContext, "Error 401, Unauthorized", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("BottomSheet", "" + t.getMessage());
            }
        });

        btnReportRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                SharedPreferences getPriorityAndId = appContext.getSharedPreferences("UserAppManager", 0);
                String prio = getPriorityAndId.getString("priority", "");
                if (!etSubject.getText().toString().equals("") && !etDetails.getText().toString().equals("") && !prio.equals("")) {
                    int id = getPriorityAndId.getInt("id_apps", 0);
                    Log.d("input", "" + id);
                    Log.d("input", "" + prio);
                    final SweetAlertDialog pDialog = new SweetAlertDialog(appContext, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Please wait...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    switch (type) {
                        case "report":
                            ApiService apiBug = ApiClient.getClient().create(ApiService.class);
                            Call<ResponseBody> report = apiBug.reportBug(id, type, prio, etSubject.getText().toString(), etDetails.getText().toString(), "Reported", "Bearer " + getToken);
                            report.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        pDialog.dismiss();
                                        try {
                                            String ResponseJson = response.body().string();
                                            Log.d("reportBug", "" + ResponseJson);
                                            SweetAlertDialog sweet = new SweetAlertDialog(appContext, SweetAlertDialog.SUCCESS_TYPE);
                                            sweet.setTitleText("Your " + type + " has sended");
                                            sweet.setContentText("Wait for next response");
                                            sweet.setCanceledOnTouchOutside(false);
                                            sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();
                                                    BugsFragment.getInstance().getListBugs().clear();
                                                    BugsFragment.getInstance().setPage(1);
                                                    BugsFragment.getInstance().addListDataBugsUser();
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            sweet.show();
                                           /* AlertDialog.Builder alertBuild = new AlertDialog.Builder(view.getContext());
                                            alertBuild.setTitle("Your "+ type + " has sended");
                                            alertBuild.setMessage("Wait for next response");
                                            alertBuild.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    BugsFragment.getInstance().addListDataBugsUser();
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = alertBuild.create();
                                            alertDialog.show();*/
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.d("reportBug", "" + response.body());
                                        pDialog.dismiss();
                                        new SweetAlertDialog(appContext, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oppss")
                                                .setContentText("Server error, please try again later")
                                                .show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    pDialog.dismiss();
                                    new SweetAlertDialog(appContext, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oppss")
                                            .setContentText("Can't connect to server, please check your internet connection")
                                            .show();
                                }
                            });
                            break;

                        case "request":
                            ApiService apiFeature = ApiClient.getClient().create(ApiService.class);
                            Call<ResponseBody> request = apiFeature.requestFeature(id, type, prio, etSubject.getText().toString(), etDetails.getText().toString(), "Requested", "Bearer " + getToken);
                            request.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        pDialog.dismiss();
                                        try {
                                            String responJSON = response.body().string();
                                            Log.d("requestFeature", "" + responJSON);
                                            SweetAlertDialog sweet = new SweetAlertDialog(appContext, SweetAlertDialog.SUCCESS_TYPE);
                                            sweet.setTitleText("Your " + type + " has sended");
                                            sweet.setContentText("Wait for next response");
                                            sweet.setCanceledOnTouchOutside(false);
                                            sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();
                                                    FeatureFragment.getInstance().getListFeature().clear();
                                                    FeatureFragment.getInstance().setPage(1);
                                                    FeatureFragment.getInstance().addListDataFeatureUser();
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            sweet.show();
                                            /*AlertDialog.Builder alertBuild = new AlertDialog.Builder(view.getContext());
                                            alertBuild.setTitle("Your "+ type + " has sended");
                                            alertBuild.setMessage("Wait for next response");
                                            alertBuild.setNegativeButton("Ok", new DialogInterface.OnClickListener(                        ) {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    FeatureFragment.getInstance().addListDataFeatureUser();
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = alertBuild.create();
                                            alertDialog.setCanceledOnTouchOutside(false);
                                            alertDialog.show();*/
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(appContext, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oppss")
                                                .setContentText("Server error, please try again later")
                                                .show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    pDialog.dismiss();
                                    new SweetAlertDialog(appContext, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oppss")
                                            .setContentText("Can't connect to server, please check your internet connection")
                                            .show();
                                }
                            });
                            break;
                    }
                } else {
                    new SweetAlertDialog(appContext, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Please input the required form data")
                            .show();
                }
            }
        });

    }

    public void showDetailBottomSheet(Context appContext, LayoutInflater layoutInflater) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(appContext, R.style.AppBottomSheetDialogTheme);
        View content = layoutInflater.inflate(R.layout.bottom_sheet, null);
    }
}
