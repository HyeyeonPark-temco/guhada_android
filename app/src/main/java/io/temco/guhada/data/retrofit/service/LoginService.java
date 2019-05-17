package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.NaverResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface LoginService {

    @GET("v1/nid//me")
    public Call<NaverResponse> getNaverProfile(@Header("Authorization") String auth);
}
