package io.temco.guhada.data.server;

import java.io.IOException;

import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.body.FilterBody;
import io.temco.guhada.data.retrofit.manager.RetrofitManager;
import io.temco.guhada.data.retrofit.service.SearchService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchServer {

    public static void getProductListByCategory(Type.ProductOrder type, int id, int page, OnServerListener listener) {
        if (listener != null) {
            // Body
            FilterBody body = new FilterBody();
            body.categoryIds = new int[]{id};
            body.searchResultOrder = Type.ProductOrder.get(type);
            // Request
            RetrofitManager.createService(Type.Server.SEARCH, SearchService.class)
                    .getFilterProductListData(body, page, Info.LIST_PAGE_UNIT)
                    .enqueue(new Callback<BaseModel<ProductList>>() {
                        @Override
                        public void onResponse(Call<BaseModel<ProductList>> call, Response<BaseModel<ProductList>> response) {
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
                        public void onFailure(Call<BaseModel<ProductList>> call, Throwable t) {
                            listener.onResult(false, t.getMessage());
                        }
                    });
        }
    }
}