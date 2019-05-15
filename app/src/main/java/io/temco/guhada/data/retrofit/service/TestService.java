package io.temco.guhada.data.retrofit.service;

import java.util.HashMap;

import io.temco.guhada.data.model.TestData;
import io.temco.guhada.data.model.base.BaseModel;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TestService {

    @GET("{userId}")
    Call<ResponseBody> getData(
            @Path("userId") String userId,
            @Query("data") String data);

    @GET("info")
    Call<BaseModel<TestData>> getInfo(
            @Query("data") String data);

    @FormUrlEncoded
    @POST("")
    Call<ResponseBody> get(
            @FieldMap HashMap<String, Object> params);

    @Multipart
    @POST("")
    Call<ResponseBody> upload(
            // @Part("description") RequestBody description,
            @Part MultipartBody.Part file);
}
