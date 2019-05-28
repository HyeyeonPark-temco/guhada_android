package io.temco.guhada.data.server;

import java.io.IOException;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.base.BaseListModel;
import io.temco.guhada.data.retrofit.manager.RetrofitManager;
import io.temco.guhada.data.retrofit.service.ProductService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductServer {

    public static void getCategories(OnServerListener listener) {
        if (listener != null) {
            RetrofitManager.createService(Type.Server.PRODUCT, ProductService.class)
                    .getCategories()
                    .enqueue(new Callback<BaseListModel<Category>>() {
                        @Override
                        public void onResponse(Call<BaseListModel<Category>> call, Response<BaseListModel<Category>> response) {
                            if (response.isSuccessful()) {
                                if (response.body().resultCode == 200) {
                                    listener.onResult(true, response.body().data);
                                } else {
                                    listener.onResult(false, response.body().message);
                                }
                            } else {
                                try {
                                    listener.onResult(false, response.errorBody().string());
                                } catch (IOException e) {
                                    // e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseListModel<Category>> call, Throwable t) {
                            listener.onResult(false, t.getMessage());
                        }
                    });
        }
    }

    public static void getAllBrands(OnServerListener listener) {
        if (listener != null) {
            RetrofitManager.createService(Type.Server.PRODUCT, ProductService.class)
                    .getAllBrands()
                    .enqueue(new Callback<BaseListModel<Brand>>() {
                        @Override
                        public void onResponse(Call<BaseListModel<Brand>> call, Response<BaseListModel<Brand>> response) {
                            if (response.isSuccessful()) {
                                if (response.body().resultCode == 200) {
                                    listener.onResult(true, response.body().data);
                                } else {
                                    listener.onResult(false, response.body().message);
                                }
                            } else {
                                try {
                                    listener.onResult(false, response.errorBody().string());
                                } catch (IOException e) {
                                    // e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseListModel<Brand>> call, Throwable t) {
                            listener.onResult(false, t.getMessage());
                        }
                    });
        }
    }
}