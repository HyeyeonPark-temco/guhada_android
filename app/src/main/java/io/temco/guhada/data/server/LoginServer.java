package io.temco.guhada.data.server;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.NaverResponse;
import io.temco.guhada.data.model.SnsUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.retrofit.manager.RetrofitManager;
import io.temco.guhada.data.retrofit.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginServer {

    private static final String NAVER_API_SUCCESS = "00";

    public static void getNaverProfile(OnServerListener listener, String accessToken) {
        RetrofitManager.createService(Type.Server.NAVER_PROFILE, LoginService.class)
                .getNaverProfile("Bearer " + accessToken)
                .enqueue(new Callback<NaverResponse>() {
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

    public static void googleLogin(OnServerListener listener, SnsUser user) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class)
                .googleLogin(user)
                .enqueue(new Callback<BaseModel<Token>>() {
                    @Override
                    public void onResponse(Call<BaseModel<Token>> call, Response<BaseModel<Token>> response) {
                        if (response.isSuccessful()) {
                            BaseModel result = response.body();
                            listener.onResult(true, result);
                        } else {
                            listener.onResult(false, response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel<Token>> call, Throwable t) {
                        listener.onResult(false, t.getMessage());
                    }
                });
    }

    public static void signUp(OnServerListener listener, User user) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class)
                .signUp(user)
                .enqueue(new Callback<BaseModel<Object>>() {
                    @Override
                    public void onResponse(Call<BaseModel<Object>> call, Response<BaseModel<Object>> response) {
                        if (response.isSuccessful()) {
                            listener.onResult(true, response.body());
                        } else {
                            listener.onResult(false, response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel<Object>> call, Throwable t) {
                        listener.onResult(false, t.getMessage());
                    }
                });
    }

    public static void signIn(OnServerListener listener, User user) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class).signIn(user).enqueue(new Callback<BaseModel<Token>>() {
            @Override
            public void onResponse(Call<BaseModel<Token>> call, Response<BaseModel<Token>> response) {
                if (response.isSuccessful()) {
                    listener.onResult(true, response.body());
                } else {
                    listener.onResult(false, response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseModel<Token>> call, Throwable t) {
                listener.onResult(false, t.getMessage());
            }
        });
    }

    public static void findUserId(OnServerListener listener, String name, String phoneNumber) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class)
                .findUserId(name, phoneNumber)
                .enqueue(new Callback<BaseModel<User>>() {
                    @Override
                    public void onResponse(Call<BaseModel<User>> call, Response<BaseModel<User>> response) {
                        listener.onResult(response.isSuccessful(), response.body());
                    }

                    @Override
                    public void onFailure(Call<BaseModel<User>> call, Throwable t) {
                        listener.onResult(false, t.getMessage());
                    }
                });
    }

    public static void verifyEmail(OnServerListener listener, User user) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class).verifyEmail(user).enqueue(new Callback<BaseModel<Object>>() {
            @Override
            public void onResponse(Call<BaseModel<Object>> call, Response<BaseModel<Object>> response) {
                listener.onResult(response.isSuccessful(), response.body());
            }

            @Override
            public void onFailure(Call<BaseModel<Object>> call, Throwable t) {
                listener.onResult(false, t.getMessage());
            }
        });
    }

    public static void verifyNumber(OnServerListener listener, Verification verification) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class).verifyNumber(verification).enqueue(new Callback<BaseModel<Object>>() {
            @Override
            public void onResponse(Call<BaseModel<Object>> call, Response<BaseModel<Object>> response) {
                listener.onResult(response.isSuccessful(), response.body());
            }

            @Override
            public void onFailure(Call<BaseModel<Object>> call, Throwable t) {
                listener.onResult(false, t.getMessage());
            }
        });
    }

    public static void changePassword(OnServerListener listener, Verification verification) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class).changePassword(verification).enqueue(new Callback<BaseModel<Object>>() {
            @Override
            public void onResponse(Call<BaseModel<Object>> call, Response<BaseModel<Object>> response) {
                listener.onResult(response.isSuccessful(), response.body());
            }

            @Override
            public void onFailure(Call<BaseModel<Object>> call, Throwable t) {
                listener.onResult(false, t.getMessage());
            }
        });
    }

    public static void getVerifyPhoneToken(OnServerListener listener) {
        RetrofitManager.createService(Type.Server.LOCAL, LoginService.class).getVerifyToken().enqueue(new Callback<BaseModel<String>>() {
            @Override
            public void onResponse(Call<BaseModel<String>> call, Response<BaseModel<String>> response) {
                listener.onResult(response.isSuccessful(), response.body());
            }

            @Override
            public void onFailure(Call<BaseModel<String>> call, Throwable t) {
                listener.onResult(false, t.getMessage());
            }
        });
    }

    public static void naverLogin(SnsUser user, OnServerListener listener) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class)
                .naverLogin(user)
                .enqueue(new Callback<BaseModel<Token>>() {
                    @Override
                    public void onResponse(Call<BaseModel<Token>> call, Response<BaseModel<Token>> response) {
                        if (response.isSuccessful()) {
                            BaseModel result = response.body();
                            listener.onResult(true, result);
                        } else {
                            listener.onResult(false, response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel<Token>> call, Throwable t) {
                        listener.onResult(false, t.getMessage());
                    }
                });
    }

    public static void kakaoLogin(SnsUser user, OnServerListener listener) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class)
                .kakaoLogin(user)
                .enqueue(new Callback<BaseModel<Token>>() {
                    @Override
                    public void onResponse(Call<BaseModel<Token>> call, Response<BaseModel<Token>> response) {
                        if (response.isSuccessful()) {
                            BaseModel result = response.body();
                            listener.onResult(true, result);
                        } else {
                            listener.onResult(false, response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel<Token>> call, Throwable t) {
                        listener.onResult(false, t.getMessage());
                    }
                });
    }

    public static void facebookLogin(SnsUser user, OnServerListener listener) {
        RetrofitManager.createService(Type.Server.USER, LoginService.class)
                .facebookLogin(user)
                .enqueue(new Callback<BaseModel<Token>>() {
                    @Override
                    public void onResponse(Call<BaseModel<Token>> call, Response<BaseModel<Token>> response) {
                        if (response.isSuccessful()) {
                            BaseModel result = response.body();
                            listener.onResult(true, result);
                        } else {
                            listener.onResult(false, response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel<Token>> call, Throwable t) {
                        listener.onResult(false, t.getMessage());
                    }
                });
    }
}
