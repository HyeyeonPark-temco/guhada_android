package io.temco.guhada.data.server;

import java.io.IOException;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Product;
import io.temco.guhada.data.model.ProductByList;
import io.temco.guhada.data.model.base.BaseListModel;
import io.temco.guhada.data.model.base.BaseModel;
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

    /**
     * 상품 상세 조회
     *
     * @param listener OnServerListener
     * @param dealId       dealId
     */
    public static void getProductDetail(OnServerListener listener, Long dealId) {
        RetrofitManager.createService(Type.Server.PRODUCT, ProductService.class, true).getProductDetail(dealId).enqueue(new Callback<BaseModel<Product>>() {
            @Override
            public void onResponse(Call<BaseModel<Product>> call, Response<BaseModel<Product>> response) {
                listener.onResult(response.isSuccessful(), response.body());
            }

            @Override
            public void onFailure(Call<BaseModel<Product>> call, Throwable t) {
                listener.onResult(false, t.getMessage());
            }
        });
    }

    // 상품 상세 조회
    public static void getProductByList(String id, OnServerListener listener) {
        if (listener != null) {
            RetrofitManager.createService(Type.Server.PRODUCT, ProductService.class)
                    .getProductByList(id)
                    .enqueue(new Callback<BaseModel<ProductByList>>() {
                        @Override
                        public void onResponse(Call<BaseModel<ProductByList>> call, Response<BaseModel<ProductByList>> response) {
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
                        public void onFailure(Call<BaseModel<ProductByList>> call, Throwable t) {
                            listener.onResult(false, t.getMessage());
                        }
                    });
        }
    }
}