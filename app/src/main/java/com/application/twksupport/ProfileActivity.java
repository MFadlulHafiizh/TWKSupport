package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.auth.MainActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        final SharedPreferences logoutPreferences = getSharedPreferences("JWTTOKEN", 0);
        final SharedPreferences logoutPrefer = getSharedPreferences("userInformation", 0);
        String logoutToken = logoutPreferences.getString("jwttoken", "");
        String deleteFcmWithId = logoutPrefer.getString("id", "");
        Log.d("logouttoken", ""+logoutToken);
        Log.d("logouttoken", ""+deleteFcmWithId);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> logout = api.logoutUser(deleteFcmWithId, logoutToken);
        logout.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String JSONRes = response.body().string();
                        Log.d(TAG, "Response : "+JSONRes);
                        logoutPreferences.edit().remove("jwttoken").commit();
                        Intent login = new Intent(getApplicationContext(), MainActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(login);
                        finish();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Unkonwn Error, please try again", Toast.LENGTH_SHORT).show();
                    logoutPreferences.edit().remove("jwttoken").commit();
                    Intent login = new Intent(getApplicationContext(), MainActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error, check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
}