package com.application.twksupport.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.twksupport.R;
import com.application.twksupport.RestApi.LoginService;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.model.TokenResponse;
import com.application.twksupport.UserActivity;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private ImageView twkLogo;
    private Button btnSignIn;
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.edtEmail);
        etPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        twkLogo = findViewById(R.id.twklogo);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginService();
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

    private void callLoginService() {
        try {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();

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
                                Log.d(TAG, ResponseJson);
                                Toast.makeText(MainActivity.this, "Password got successful", Toast.LENGTH_SHORT).show();
                                Intent toUser = new Intent(getApplicationContext(), UserActivity.class);
                                startActivity(toUser);
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "System error occured", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "System error occured, please check our internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}