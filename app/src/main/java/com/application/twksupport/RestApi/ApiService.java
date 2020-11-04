package com.application.twksupport.RestApi;

import com.application.twksupport.model.ResponseData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded",
    })
    @POST("login")
    Call<ResponseBody> getToken(@Field("email") String email,
                                @Field("password") String password);

    @GET("user/data-bug")
    Call<ResponseData> getUserBugData(@Query("email") String set_idUser);

    @GET("user")
    Call<ResponseBody> getUserInformation(@Header("Authorization") String authorization);

}
