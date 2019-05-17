package io.temco.guhada.data.server;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.NaverResponse;
import io.temco.guhada.data.retrofit.manager.RetrofitManager;
import io.temco.guhada.data.retrofit.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginServer {
    private static final String NAVER_API_SUCCESS = "00";

    public static void getNaverProfile(OnServerListener listener, String accessToken) {
        RetrofitManager.createService(Type.Server.NAVER_PROFILE, LoginService.class).getNaverProfile("Bearer " + accessToken).enqueue(new Callback<NaverResponse>() {
            @Override
            public void onResponse(Call<NaverResponse> call, Response<NaverResponse> response) {
                if (response.isSuccessful()) {
                    NaverResponse naverResponse = response.body();
                    if (naverResponse != null && naverResponse.getResultCode() != null) {
                        if (naverResponse.getResultCode().equals(NAVER_API_SUCCESS)) {
                            listener.onResult(true, naverResponse.getUser());
                        }
                    } else {
                        listener.onResult(false, response.message());
                    }
                } else {
                    listener.onResult(false, response.message());
                }
            }

            @Override
            public void onFailure(Call<NaverResponse> call, Throwable t) {
                CommonUtil.debug("[NAVER] FAILED: " + t.getMessage());
            }
        });
    }
}
