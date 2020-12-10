package com.application.twksupport.UIUX;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.application.twksupport.BugsFragment;
import com.application.twksupport.FeatureFragment;
import com.application.twksupport.R;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.StaffListActivity;
import com.application.twksupport.UserActivity;
import com.application.twksupport.auth.MainActivity;
import com.application.twksupport.model.AppsUserData;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserAppManager;
import com.application.twksupport.model.UserManager;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
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
    private List<AppsUserData> listAppData = new ArrayList<>();
    public static final String EXTRA_BUG = "extra_bug";

    public void showPopupFilter(final Context appContext, final String extra_code) {
        SharedPreferences getCompanyUser = appContext.getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = appContext.getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "missing");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Spinner spinPriorityFiler, spinAppnameFilter;
        final UserAppManager userAppManager = new UserAppManager(appContext);
        final String[] priority = new String[1];
        Button reset, apply;
        ImageButton close;
        popUpFilter = new Dialog(appContext, R.style.AppBottomSheetDialogTheme);
        popUpFilter.setContentView(R.layout.filter_popup);
        popUpFilter.setCanceledOnTouchOutside(false);
        spinPriorityFiler = popUpFilter.findViewById(R.id.priorityFilter);
        spinAppnameFilter = popUpFilter.findViewById(R.id.appNameFilter);
        reset = popUpFilter.findViewById(R.id.btnResetFilter);
        apply = popUpFilter.findViewById(R.id.btnApplyFilter);
        close = popUpFilter.findViewById(R.id.btnCloseFilter);
        spinnerPriority(appContext, spinPriorityFiler);
        spinPriorityFiler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Priority")){

                }else{
                String priorityValue = parent.getItemAtPosition(position).toString();
                priority[0] = priorityValue;
                Log.d("value", "" + priority[0]);
                userAppManager.setPriority(priority[0]);
                Toast.makeText(appContext, ""+priority[0], Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setFilterSpinnerApps(appContext, spinAppnameFilter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpFilter.dismiss();
            }
        });
        popUpFilter.show();

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (extra_code){
                    case "extra_bugs":
                        Toast.makeText(appContext, "open from bug", Toast.LENGTH_SHORT).show();
                        break;

                    case "extra_feature":
                        Toast.makeText(appContext, "open from feature", Toast.LENGTH_SHORT).show();
                        break;

                    case "extra_done":
                        Toast.makeText(appContext, "open from done", Toast.LENGTH_SHORT).show();
                        break;

                    case "extra_jobs":
                        Toast.makeText(appContext, "open from todo", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void setFilterSpinnerApps(final Context appContext, final Spinner spinAppName){
        final UserAppManager userAppManager = new UserAppManager(appContext);
        SharedPreferences getCompanyUser = appContext.getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = appContext.getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "missing");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getApps = api.getUserApps(idCompany, "Bearer "+getToken);
        getApps.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    listAppData = response.body().getUserApp();
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
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
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

    public void spinnerPriority(Context context, Spinner spinner) {
        String priority[] = {"Choose Priority","Low", "Middle", "High"};
        ArrayAdapter<String> spinner1Adapter = new ArrayAdapter<String>(context, R.layout.spinner_style, priority);
        spinner.setAdapter(spinner1Adapter);
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
                            if (adapterView.getItemAtPosition(i).equals("Choose Priority")){
                                userAppManager.setPriority(null);
                            }else{
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
