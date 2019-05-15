package io.temco.guhada.data.server;

import android.text.TextUtils;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.TestData;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.retrofit.manager.RetrofitManager;
import io.temco.guhada.data.retrofit.service.TestService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestServer {

    public static void getData(OnServerListener listener) {
        if (listener != null) {
            RetrofitManager.createService(Type.Server.COMMON, TestService.class)
                    .getData("", "")
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            CommonUtil.debug("onResponse");
                            if (response.isSuccessful()) {
                                CommonUtil.debug(response.toString());
                            } else {
                                CommonUtil.debug(response.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            CommonUtil.debug("onFailure");
                        }
                    });
        }
    }

    public static void getInfo(OnServerListener listener) {
        if (listener != null) {
            RetrofitManager.createService(Type.Server.COMMON, TestService.class)
                    .getInfo("")
                    .enqueue(new Callback<BaseModel<TestData>>() {
                        @Override
                        public void onResponse(Call<BaseModel<TestData>> call, Response<BaseModel<TestData>> response) {
                            if (response.isSuccessful()) {
                                listener.onResult(true, response.body());
                            } else {
                                listener.onResult(false, response.message());
                            }

                            //
                            if (response.body() != null) {
                                String name = response.body().data.name;
                                if (!TextUtils.isEmpty(name)) {
                                    CommonUtil.debug(name);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseModel<TestData>> call, Throwable t) {
                            listener.onResult(false, t.getMessage());
                        }
                    });
        }
    }
}
