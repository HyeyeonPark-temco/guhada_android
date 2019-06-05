package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.NaverResponse;
import io.temco.guhada.data.model.SnsUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginService {

    @POST("/signUpUser")
    Call<BaseModel<Object>> signUp(@Body User user);

    @POST("/loginUser")
    Call<BaseModel<Token>> signIn(@Body User user);

    @GET("/findUserId")
    Call<BaseModel<User>> findUserId(
            @Query("name") String name,
            @Query("phoneNumber") String phoneNumber);

    // VERIFY
    @GET("/phoneCertification")
    Call<BaseModel<String>> getVerifyToken();

    /**
     * 인증번호 검증 API
     */
    @POST("/verify")
    Call<BaseModel<Object>> verifyNumber(@Body Verification verification);

    /**
     * 이메일로 인증번호 발송 API
     */
    @POST("/verify/sendEmail")
    Call<BaseModel<Object>> verifyEmail(@Body User user);

    /**
     * 휴대폰 번호로 인증번호 발송 API
     */
    @POST("/verify/sendMobile")
    Call<BaseModel<Object>> verifyPhone(@Body User user);

    /**
     * 인증번호로 비밀번호 바꾸기 API
     * @param verification [field] email, newPassword, verificationNumber
     */
    @POST("/verify/change-password")
    Call<BaseModel<Object>> changePassword(@Body Verification verification);

    /**
     * 본인인증으로 비밀번호 바꾸기 API
     * @param verification [field] diCode, newPassword, mobile
     */
    @POST("/verify/identity/change-password")
    Call<BaseModel<Object>> changePasswordByIdentifying(@Body Verification verification);


    // SNS LOGIN
    @POST("/facebookLogin")
    Call<BaseModel<Token>> facebookLogin(@Body SnsUser user);

    @POST("/googleLogin")
    Call<BaseModel<Token>> googleLogin(@Body SnsUser user);

    @POST("/kakaoLogin")
    Call<BaseModel<Token>> kakaoLogin(@Body SnsUser user);

    @POST("/naverLogin")
    Call<BaseModel<Token>> naverLogin(@Body SnsUser user);

    // NAVER
    @GET("v1/nid//me")
    Call<NaverResponse> getNaverProfile(@Header("Authorization") String auth);

    @GET("/isEmailExist/{email}")
    Call<BaseModel<Object>> checkEmail(@Path("email") String email);

    @GET("/isMobileExist/{mobile}")
    Call<BaseModel<Object>> checkPhone(@Path("mobile") String mobile);
}
