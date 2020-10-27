package com.application.twksupport.UIUX;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.application.twksupport.R;
import com.application.twksupport.UserActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class UserInteraction extends AppCompatActivity {
    Dialog popUpFilter;
    TextView btmSheetTitle;

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
            blurView.setAlpha(1);
        } else {
            blurView.setAlpha(0);

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
                blurView.setAlpha(1);
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                blurView.setAlpha(0);
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
        bottomSheetDialog.show();
    }
}
