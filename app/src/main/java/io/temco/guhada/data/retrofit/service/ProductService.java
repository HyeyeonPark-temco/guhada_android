package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.data.model.base.BaseListModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {

    // http://dev.product.guhada.com/categories
    @GET("categories")
    Call<BaseListModel<CategoryData>> getCategories();
}
