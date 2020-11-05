package com.application.twksupport.UIUX;

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

import com.application.twksupport.R;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.UserActivity;
import com.application.twksupport.model.AppsUserData;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserManager;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
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

    public void showBottomSheet(final Context appContext, final FloatingActionsMenu fabMenu, final BlurView blurView,  LayoutInflater yourLayout, String title, final String type) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(appContext, R.style.AppBottomSheetDialogTheme);
        View content = yourLayout.inflate(R.layout.bottom_sheet, null);
        final Spinner spinPriority = (Spinner) content.findViewById(R.id.prioritySpinner);
        final Spinner spinAppName = (Spinner) content.findViewById(R.id.appnameSpin);
        bottomSheetDialog.setContentView(content);
        final EditText etSubject = bottomSheetDialog.findViewById(R.id.edtSubject);
        final EditText etDetails = bottomSheetDialog.findViewById(R.id.edtDetails);
        btmSheetTitle = bottomSheetDialog.findViewById(R.id.reqeust_type);
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
        Button btnReport = bottomSheetDialog.findViewById(R.id.btn_report);

        spinPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etSubject.getText().toString().equals("") && !etDetails.getText().toString().equals("")) {
                    AlertDialog.Builder alertBuild = new AlertDialog.Builder(view.getContext());
                    alertBuild.setTitle("Your "+ type + " has sended");
                    alertBuild.setMessage("Wait for next response");
                    alertBuild.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuild.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(appContext, "Please input data correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences getEmailUser = appContext.getSharedPreferences("userInformation", 0);
        String email = getEmailUser.getString("email", "not Authenticated");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getApps = api.getUserApps(email);
        getApps.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    Log.d("BottomSheet", ""+response.body().getUserApp());
                    listAppData = response.body().getUserApp();
                    ArrayAdapter<AppsUserData> spinnerAdapter = new ArrayAdapter<AppsUserData>(appContext, android.R.layout.simple_spinner_item, listAppData);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinAppName.setAdapter(spinnerAdapter);
                    Log.d("BottomSheet", ""+listAppData);
                    bottomSheetDialog.show();

                }
                else {
                    Log.d("BottomSheet", ""+response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("BottomSheet", ""+t.getMessage());
            }
        });
    }
}
