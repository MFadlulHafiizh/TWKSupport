package com.application.twksupport.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.twksupport.R;
import com.application.twksupport.RestApi.LoginService;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.UIUX.BtnProgress;
import com.application.twksupport.model.TokenResponse;
import com.application.twksupport.UserActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextInputEditText etPassword;
    private ImageView twkLogo;
    private View btnSignIn;
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.edtEmail);
        etPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btn_progress);
        twkLogo = findViewById(R.id.twklogo);

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
        if (exit){
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
        try {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            final BtnProgress btnProgress = new BtnProgress(MainActivity.this, view);
            final Handler handler = new Handler();
            LoginService service = ApiClient.getClient().create(LoginService.class);
            Call<ResponseBody> srvLogin = service.getToken(email, password);
            srvLogin.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String ResponseJson = response.body().string();
                            Gson objGson = new Gson();
                            TokenResponse objResp = objGson.fromJson(ResponseJson, TokenResponse.class);
                            if (objResp.getToken() != null){
                                getSharedPreferences("valid", MODE_PRIVATE).edit().putString("token", objResp.getToken()).commit();
                                getSharedPreferences("role", MODE_PRIVATE).edit().putString("role", objResp.getRole()).commit();
                                Log.d(TAG, ResponseJson);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnProgress.buttonFinished();
                                        Intent toUser = new Intent(getApplicationContext(), UserActivity.class);
                                        startActivity(toUser);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    }
                                },2000);
                            }else{
                                Toast.makeText(MainActivity.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnProgress.buttonError();
                                    }
                                },1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            btnProgress.buttonError();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    btnProgress.buttonError();
                    Toast.makeText(MainActivity.this, "System error occured", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "System error occured, please check our internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}