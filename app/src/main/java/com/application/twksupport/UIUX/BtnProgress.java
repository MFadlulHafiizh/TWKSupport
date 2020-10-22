package com.application.twksupport.UIUX;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.application.twksupport.R;

public class BtnProgress {
    private CardView cardView;
    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    TextView textView;

    Animation fade_in;

    public BtnProgress(Context mContext, View view){
        cardView = view.findViewById(R.id.btn_container);
        constraintLayout = view.findViewById(R.id.consraint);
        progressBar = view.findViewById(R.id.loading_login_icon);
        textView = view.findViewById(R.id.txtSignIn);
    }

    public void buttonActivated(){
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(R.string.loading);
    }
    public void buttonFinished(){
        constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.greenFigma));
        progressBar.setVisibility(View.GONE);
        textView.setText(R.string.success);
    }
    public void buttonError(){
        progressBar.setVisibility(View.GONE);
        textView.setText("Sign In");
    }
}
