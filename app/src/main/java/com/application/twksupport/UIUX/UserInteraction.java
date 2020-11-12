package com.application.twksupport.UIUX;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.application.twksupport.UserActivity;
import com.application.twksupport.model.AppsUserData;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserAppManager;
import com.application.twksupport.model.UserManager;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

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

    public void showPopupFilter(Context appContext){
        Spinner spinPriorityFiler, spinAppnameFilter;
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpFilter.dismiss();
            }
        });
        popUpFilter.show();
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

    public void spinnerPriority(Context context, Spinner spinner){
        String priority[] = {"Low", "Middle", "High"};
        ArrayAdapter<String> spinner1Adapter = new ArrayAdapter<String>(context, R.layout.spinner_style, priority);
        spinner.setAdapter(spinner1Adapter);
    }

    public void showBottomSheet(final Context appContext, final FloatingActionsMenu fabMenu, final BlurView blurView,  LayoutInflater yourLayout, String title, final String type) {
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
        switch (type){
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

        SharedPreferences getEmailUser = appContext.getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = appContext.getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "missing");
        final String email = getEmailUser.getString("email", "not Authenticated");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getApps = api.getUserApps(email, "Bearer "+getToken);
        final String[] priority = new String[1];
        bottomSheetDialog.show();
        getApps.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    Log.d("BottomSheet", ""+response.body().getUserApp());
                    listAppData = response.body().getUserApp();

                    //SpinnerPriority
                    spinnerPriority(appContext, spinPriority);
                    spinPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String priorityValue = adapterView.getItemAtPosition(i).toString();
                            priority[0] = priorityValue;
                            Log.d("value", ""+ priority[0]);
                            userAppManager.setPriority(priority[0]);
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
                            Log.d("appsselect", "id : "+id_apps);
                            userAppManager.setIdApps(id_apps);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    Log.d("BottomSheet", ""+listAppData);

                }
                else {
                    Log.d("BottomSheet", ""+response.body());
                    Toast.makeText(appContext, "Error 401, Unauthorized", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("BottomSheet", ""+t.getMessage());
            }
        });

        btnReportRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!etSubject.getText().toString().equals("") && !etDetails.getText().toString().equals("")) {
                    SharedPreferences getPriorityAndId = appContext.getSharedPreferences("UserAppManager", 0);
                    String prio = getPriorityAndId.getString("priority", "");
                    int id = getPriorityAndId.getInt("id_apps", 0);
                    Log.d("input", ""+id);
                    Log.d("input", ""+prio);
                    switch (type){
                        case "report":
                            ApiService apiBug = ApiClient.getClient().create(ApiService.class);
                            Call<ResponseBody> report = apiBug.reportBug(id, prio, etSubject.getText().toString(), etDetails.getText().toString(), "Reported", "Bearer "+getToken);
                            report.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()){
                                        try {
                                            String ResponseJson = response.body().string();
                                            Log.d("reportBug", ""+ResponseJson);
                                            AlertDialog.Builder alertBuild = new AlertDialog.Builder(view.getContext());
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
                                            alertDialog.show();
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        Toast.makeText(appContext, "Unauthorized", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(appContext, "Error"+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        case "request":
                            ApiService apiFeature = ApiClient.getClient().create(ApiService.class);
                            Call<ResponseBody> request = apiFeature.requestFeature(id, prio, etSubject.getText().toString(), etDetails.getText().toString(),"Requested", "Bearer "+getToken);
                            request.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()){
                                        try {
                                            String responJSON = response.body().string();
                                            Log.d("reportBug", ""+responJSON);
                                            AlertDialog.Builder alertBuild = new AlertDialog.Builder(view.getContext());
                                            alertBuild.setTitle("Your "+ type + " has sended");
                                            alertBuild.setMessage("Wait for next response");
                                            alertBuild.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    FeatureFragment.getInstance().addListDataFeatureUser();
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = alertBuild.create();
                                            alertDialog.show();
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        Toast.makeText(appContext, "Unauthorized", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(appContext, "Error"+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                } else {
                    Toast.makeText(appContext, "Please input data correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
