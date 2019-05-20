package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.NaverResponse;
import io.temco.guhada.data.model.SnsUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.base.BaseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/signUpUser")
    Call<BaseModel<String>> signUp(@Body User user);

    @POST("/loginUser")
    Call<BaseModel<Token>> signIn(@Body User user);

    // SNS LOGIN
    @POST("/facebookLogin")
    Call<BaseModel> facebookLogin(@Body SnsUser user);

    @POST("/googleLogin")
    Call<BaseModel> googleLogin(@Body SnsUser user);

    @POST("/kakaoLogin")
    Call<BaseModel> kakaoLogin(@Body SnsUser user);

    @POST("/naverLogin")
    Call<BaseModel> naverLogin(@Body SnsUser user);

    // NAVER
    @GET("v1/nid//me")
    Call<NaverResponse> getNaverProfile(@Header("Authorization") String auth);

}
