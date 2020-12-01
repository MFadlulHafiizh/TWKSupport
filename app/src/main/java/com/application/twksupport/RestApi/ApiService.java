package com.application.twksupport.RestApi;

import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.StaffResponse;
import com.application.twksupport.model.TokenResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded",
    })
    @POST("login")
    Call<ResponseBody> getToken(@Field("email") String email,
                                @Field("password") String password,
                                @Field("fcm_token") String fcm_token);

    @GET("logout/{id}")
    Call<ResponseBody> logoutUser(@Path("id") String id_user,
                                  @Query("token") String logoutToken);

    @GET("user/getapp")
    Call<ResponseData> getUserApps(@Query("id_perusahaan") int idCompany,@Header("Authorization") String authToken);

    @GET("user")
    Call<ResponseBody> getUserInformation(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("user/report-bug")
    Call<ResponseBody> reportBug(@Field("id_apps") int id_apps,
                                 @Field("type") String type,
                                 @Field("priority") String priority,
                                 @Field("subject") String subject,
                                 @Field("detail") String detail,
                                 @Field("status") String status,
                                 @Header("Authorization") String authToken);
    @FormUrlEncoded
    @POST("user/request-feature")
    Call<ResponseBody> requestFeature(@Field("id_apps") int id_apps,
                                      @Field("type") String type,
                                      @Field("priority") String priority,
                                      @Field("subject") String subject,
                                      @Field("detail") String detail,
                                      @Field("status") String status,
                                      @Header("Authorization") String authToken);
    @GET("user/data-bug")
    Call<ResponseData> getUserBugData(@Query("id_perusahaan") int idCompany,@Header("Authorization") String authToken);
    @GET("user/data-feature")
    Call<ResponseData> getUserFeatureData(@Query("id_perusahaan") int idCompany,@Header("Authorization") String authToken);
    @GET("user/data-done")
    Call<ResponseData> getUserDoneData(@Query("id_perusahaan") int idCompany,@Header("Authorization") String authToken);

    @GET("admin/data-bug")
    Call<ResponseData> getAdminBugData(@Header("Authorization") String authToken);
    @GET("admin/data-feature")
    Call<ResponseData> getAdminFeatureData(@Header("Authorization") String authToken);
    @GET("admin/data-done")
    Call<ResponseData> getAdminDoneData(@Header("Authorization") String authToken);
    @GET("admin/getStaff")
    Call<StaffResponse> getStaff(@Header("Authorization") String adminToken);

    @FormUrlEncoded
    @POST("admin/assignment")
    Call<ResponseBody> assign(@Header("Authorization") String adminToken,
                              @Field("id_user[]") ArrayList<String> iduser,
                              @Field("id_ticket") String idticket,
                              @Field("dead_line") String date);

    @FormUrlEncoded
    @PATCH("admin/make-agreement/{id_ticket}")
    Call<ResponseBody> makeAgreement(@Header("Authorization") String adminToken,
                                     @Path("id_ticket") String id_ticket,
                                     @Field("price") String price,
                                     @Field("time_periodic") String time_periodic,
                                     @Field("status") String status);

    @GET("twkstaff/todo")
    Call<ResponseData> getJobs(@Header("Authorization") String staffToken,
                               @Query("id_user") String staffId);

    @GET("twkstaff/hasdone")
    Call<ResponseData> getStaffDoneData(@Header("Authorization") String staffToken,
                                        @Query("id_user") String staffId);



}
