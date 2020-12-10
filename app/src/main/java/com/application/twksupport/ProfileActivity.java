package com.application.twksupport;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.auth.MainActivity;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout;
    private RoundedImageView profilbg;
    private AppBarLayout appbar;
    private ImageButton btnBack;
    private TextView tv_name, tv_email, tv_company;
    private CircleImageView userImage;
    private FloatingActionButton chooseImage;
    private UserManager userManager;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99 ;
    private static final int CAPTURE_REQUEST_CODE = 0;
    public static final int REQUEST_GALLERY = 9544;
    private Bitmap bitmap;
    String part_image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize();
        CheckPermission();
        userManager = new UserManager(getApplicationContext());
        SharedPreferences getUserInfo = getSharedPreferences("userInformation", 0);
        String name = getUserInfo.getString("name", "");
        String email = getUserInfo.getString("email", "");
        String companyName = getUserInfo.getString("company_name", "");
        String photo_user = getUserInfo.getString("photo", "");

        Log.d("urlimage", ""+photo_user);

        tv_name.setText(name);
        tv_email.setText(email);
        tv_company.setText(companyName);

        appbar.setStateListAnimator(null);
        Glide.with(profilbg.getContext())
                .load(R.drawable.collapse)
                .into(profilbg);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences getUserInfo = getSharedPreferences("userInformation", 0);
        String photo_user = getUserInfo.getString("photo", "");
        applyPhoto(photo_user);
    }

    private void applyPhoto(String url_image){
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(userImage.getContext()).load(url_image).apply(options).into(userImage);
    }

    private void initialize() {
        appbar = findViewById(R.id.profile_appbar);
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);
        profilbg = findViewById(R.id.profilbg);
        tv_name = findViewById(R.id.profile_name);
        tv_email = findViewById(R.id.profile_email);
        tv_company = findViewById(R.id.tv_company);
        userImage = findViewById(R.id.userImage);
        chooseImage = findViewById(R.id.chooseUserImage);
    }

    private void logout() {
        final SharedPreferences logoutPreferences = getSharedPreferences("JWTTOKEN", 0);
        final SharedPreferences logoutPrefer = getSharedPreferences("userInformation", 0);
        String logoutToken = logoutPreferences.getString("jwttoken", "");
        String deleteFcmWithId = logoutPrefer.getString("id", "");
        Log.d("logouttoken", "" + logoutToken);
        Log.d("logouttoken", "" + deleteFcmWithId);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> logout = api.logoutUser(deleteFcmWithId, logoutToken);
        logout.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String JSONRes = response.body().string();
                        Log.d(TAG, "Response : " + JSONRes);
                        logoutPreferences.edit().remove("jwttoken").commit();
                        Intent login = new Intent(getApplicationContext(), MainActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(login);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Unkonwn Error, please try again", Toast.LENGTH_SHORT).show();
                    logoutPreferences.edit().remove("jwttoken").commit();
                    Intent login = new Intent(getApplicationContext(), MainActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void selectImage() {
        if (CheckPermission()){
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] imageprojection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, imageprojection, null, null, null);
            if (cursor != null){
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageprojection[0]);
                part_image = cursor.getString(indexImage);
                if (part_image != null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageDecodedUpload(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void imageSelectForUpload(final Bitmap bitmap, String pathImage) {
        File file = new File(pathImage);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        SharedPreferences getId_user = getSharedPreferences("userInformation", 0);
        String id_user = getId_user.getString("id", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> uploadImage = api.uploadImageUser(id_user, parts);
        uploadImage.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String JSONResponse = response.body().string();
                    Gson objGson = new Gson();
                    ResponseData objResp = objGson.fromJson(JSONResponse, ResponseData.class);
                    if (objResp.getImage_url() != null){
                        userImage.setImageBitmap(bitmap);
                        userManager.addPict(objResp.getImage_url());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imageDecodedUpload(final Bitmap bitmap){
        String image = imageToString();
        SharedPreferences getId_user = getSharedPreferences("userInformation", 0);
        String id_user = getId_user.getString("id", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> upload = api.uploadBase64Pict(id_user, image);
        upload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String JSONResponse = response.body().string();
                        Gson objGson = new Gson();
                        ResponseData objResp = objGson.fromJson(JSONResponse, ResponseData.class);
                        if (objResp.getImage_url() != null){
                            userImage.setImageBitmap(bitmap);
                            userManager.addPict(objResp.getImage_url());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private String imageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,75,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please accept the permissions")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ProfileActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                                startActivity(new Intent(ProfileActivity
                                        .this, ProfileActivity.class));
                                ProfileActivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {

            return true;

        }
    }
}