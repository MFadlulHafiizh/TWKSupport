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

    @FormUrlEncoded
    @POST("user/report-bug")
    Call<ResponseBody> reportBug(@Field("id_apps") int id_apps,
                                 @Field("priority") String priority,
                                 @Field("subject") String subject,
                                 @Field("detail") String detail,
                                 @Field("status") String status,
                                 @Header("Authorization") String authToken);
    @FormUrlEncoded
    @POST("user/request-feature")
    Call<ResponseBody> requestFeature(@Field("id_apps") int id_apps,
                                      @Field("priority") String priority,
                                      @Field("subject") String subject,
                                      @Field("detail") String detail,
                                      @Field("status") String status,
                                      @Header("Authorization") String authToken);

    @GET("user/data-bug")
    Call<ResponseData> getUserBugData(@Query("email") String set_emailUser,@Header("Authorization") String authToken);
    @GET("user/data-feature")
    Call<ResponseData> getUserFeatureData(@Query("email") String set_emailUser,@Header("Authorization") String authToken);
    @GET("user/data-done")
    Call<ResponseData> getUserDoneData(@Query("email") String set_emailUser,@Header("Authorization") String authToken);

    @GET("admin/data-bug")
    Call<ResponseData> getAdminBugData(@Header("Authorization") String authToken);
    @GET("admin/data-feature")
    Call<ResponseData> getAdminFeatureData(@Header("Authorization") String authToken);
    @GET("admin/data-done")
    Call<ResponseData> getAdminDoneData(@Header("Authorization") String authToken);

    @GET("user/getapp")
    Call<ResponseData> getUserApps(@Query("email") String set_emailUser,@Header("Authorization") String authToken);

    @GET("user")
    Call<ResponseBody> getUserInformation(@Header("Authorization") String authorization);

    @GET("logout")
    Call<ResponseBody> logoutUser(@Query("token") String logoutToken);

}
