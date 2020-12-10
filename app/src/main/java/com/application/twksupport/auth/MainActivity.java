package com.application.twksupport.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.twksupport.R;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.StaffListActivity;
import com.application.twksupport.TwkStaffActivity;
import com.application.twksupport.UIUX.BtnProgress;
import com.application.twksupport.model.TokenResponse;
import com.application.twksupport.UserActivity;
import com.application.twksupport.model.UserData;
import com.application.twksupport.model.UserManager;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextInputEditText etPassword;
    private ImageView twkLogo;
    private View btnSignIn;
    private SessionManager sessionManager;
    private UserManager userInformation;
    private String fcm = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            SweetAlertDialog sweet = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                            sweet.setTitleText("Some service may be not working properly");
                            sweet.setContentText("Your device maybe will not get notification, please sync or update google play services on your devices");
                            sweet.show();
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("fcmoeoe", "" + token);
                    }
                });

        etEmail = findViewById(R.id.edtEmail);
        etPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btn_progress);
        twkLogo = findViewById(R.id.twklogo);

        sessionManager = new SessionManager(getApplicationContext());
        userInformation = new UserManager(getApplicationContext());
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnProgress btnProgress = new BtnProgress(MainActivity.this, view);
                btnProgress.buttonActivated();
                callLoginService(view);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            return;
        }
        exit = true;
        Toast.makeText(this, "Tap back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }

    private void callLoginService(final View view) {
        final BtnProgress btnProgress = new BtnProgress(MainActivity.this, view);
        try {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            final Handler handler = new Handler();
            SharedPreferences getFcm = getSharedPreferences("JWTTOKEN", 0);
            fcm = getFcm.getString("fcm_token", "");
            Log.d("fcmoemoe", "" + fcm);
            ApiService service = ApiClient.getClient().create(ApiService.class);

            Call<ResponseBody> srvLogin = service.getToken(email, password, fcm);
            srvLogin.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String ResponseJson = response.body().string();
                            Gson objGson = new Gson();
                            TokenResponse objResp = objGson.fromJson(ResponseJson, TokenResponse.class);
                            if (objResp.getToken() != null) {
                                userInformation.addUserInformation(objResp.getUser().getId(), objResp.getUser().getId_perusahaan(), objResp.getUser().getPhoto(), objResp.getUser().getName(), objResp.getUser().getEmail(), objResp.getUser().getRole(), objResp.getUser().getFcm_token(), objResp.getUser().getNama_perusahaan());
                                Log.d("nama_perusahaan", "" + objResp.getUser().getNama_perusahaan());
                                sessionManager.createSession(objResp.getToken());
                                String role = objResp.getUser().getRole();
                                Log.d(TAG, ResponseJson);
                                if (role.equals("twk-head") || role.equals("client-head") || role.equals("client-staff")) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnProgress.buttonFinished();
                                            Intent toUser = new Intent(getApplicationContext(), UserActivity.class);
                                            startActivity(toUser);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            finish();
                                        }
                                    }, 500);
                                } else {
                                    btnProgress.buttonFinished();
                                    Intent toStaff = new Intent(getApplicationContext(), TwkStaffActivity.class);
                                    startActivity(toStaff);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }

                            } else {
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Sign In Failed")
                                        .setContentText("Your email or password is incorrect")
                                        .setConfirmText("Try again")
                                        .show();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnProgress.buttonError();
                                    }
                                }, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity", "" + e.getMessage());
                            btnProgress.buttonError();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    btnProgress.buttonError();
                    Toast.makeText(MainActivity.this, "System error occured, please check our internet connection", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            btnProgress.buttonError();
            Toast.makeText(this, "System error occured", Toast.LENGTH_SHORT).show();
        }
    }
}