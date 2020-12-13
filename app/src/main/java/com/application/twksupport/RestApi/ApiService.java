package com.application.twksupport.RestApi;

import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.StaffResponse;
import com.application.twksupport.model.TokenResponse;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //allUsers
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

    @GET("notification")
    Call<ResponseData> getListNotification(@Query("page") int page, @Query("id_user") String id_user,
                                           @Query("priority") String priority,
                                           @Query("apps_name") String apps_name,
                                           @Query("dari") String fromDate,
                                           @Query("sampai") String untilDate);

    @PATCH("notification/readat/{id_notif}")
    Call<ResponseBody> markAsRead(@Path("id_notif") String id_notif, @Query("read_at") int read_at);

    @FormUrlEncoded
    @POST("user/upload-image-base64/{id}")
    Call<ResponseBody> uploadBase64Pict(@Path("id") String id_user, @Field("photo") String imageEncoded);

    @Multipart
    @POST("user/upload-image/{id}")
    Call<ResponseBody> uploadImageUser(@Path("id") String id_user, @Part MultipartBody.Part photo);


    //ForClientAct
    @GET("user/getapp")
    Call<ResponseData> getUserApps(@Query("id_perusahaan") int idCompany, @Header("Authorization") String authToken);

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
    Call<ResponseData> getUserBugData(@Query("id_perusahaan") int idCompany,
                                      @Query("page") int page,
                                      @Header("Authorization") String authToken,
                                      @Query("priority") String priority,
                                      @Query("apps_name") String apps_name);

    @GET("user/data-feature")
    Call<ResponseData> getUserFeatureData(@Query("id_perusahaan") int idCompany,
                                          @Query("page") int page,
                                          @Header("Authorization") String authToken,
                                          @Query("priority") String priority,
                                          @Query("apps_name") String apps_name);

    @GET("user/data-done")
    Call<ResponseData> getUserDoneData(@Query("id_perusahaan") int idCompany,
                                       @Query("page") int page,
                                       @Header("Authorization") String authToken,
                                       @Query("priority") String priority,
                                       @Query("apps_name") String apps_name);

    @PATCH("user/agreement-act/{id_ticket}")
    Call<JsonObject> clientAgreementAct(@Header("Authorization") String token,
                                        @Path("id_ticket") String id_ticket,
                                        @Query("nama_perusahaan") String nama_perusahaan,
                                        @Query("apps_name") String apps_name,
                                        @Query("aproval_stat") String aproval_stat,
                                        @Query("status") String status);


    //AdminAct
    @GET("admin/data-bug")
    Call<ResponseData> getAdminBugData(@Query("page") int page,
                                       @Header("Authorization") String authToken,
                                       @Query("priority") String priority,
                                       @Query("apps_name") String apps_name,
                                       @Query("assigned") String assigned,
                                       @Query("dari") String fromDate,
                                       @Query("sampai") String untilDate);

    @GET("admin/data-feature")
    Call<ResponseData> getAdminFeatureData(@Query("page") int page, @Header("Authorization") String authToken,
                                           @Query("priority") String priority,
                                           @Query("apps_name") String apps_name,
                                           @Query("assigned") String assigned,
                                           @Query("dari") String fromDate,
                                           @Query("sampai") String untilDate);

    @GET("admin/data-done")
    Call<ResponseData> getAdminDoneData(@Query("page") int page, @Header("Authorization") String authToken,
                                        @Query("priority") String priority,
                                        @Query("apps_name") String apps_name,
                                        @Query("dari") String fromDate,
                                        @Query("sampai") String untilDate);

    @GET("admin/getTicketApps")
    Call<ResponseData> getAllTicketApps(@Header("Authorization") String authToken);

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
                                     @Field("price") int price,
                                     @Field("time_periodic") String time_periodic,
                                     @Field("status") String status);


    //StaffAct
    @GET("twkstaff/todo")
    Call<ResponseData> getJobs(@Header("Authorization") String staffToken,
                               @Query("page") int page,
                               @Query("id_user") String staffId,
                               @Query("priority") String priority,
                               @Query("apps_name") String apps_name,
                               @Query("dari") String fromDate,
                               @Query("sampai") String untilDate);

    @GET("twkstaff/hasdone")
    Call<ResponseData> getStaffDoneData(@Query("page") int page,
                                        @Header("Authorization") String staffToken,
                                        @Query("id_user") String staffId,
                                        @Query("priority") String priority,
                                        @Query("apps_name") String apps_name,
                                        @Query("dari") String fromDate,
                                        @Query("sampai") String untilDate);

    @GET("twkstaff/listnotif")
    Call<ResponseData> listNotifStaff(@Header("Authorization") String token, @Query("id_user") String id_user, @Query("page") int page);

    @PATCH("twkstaff/markAsComplete/{id_ticket}")
    Call<ResponseBody> markAsComplete(@Header("Authorization") String token,@Path("id_ticket")String id_ticket);

    @GET("twkstaff/getapp")
    Call<ResponseData> getAssignedApps(@Header("Authorization") String token, @Query("id_user") String id_user);


}
