package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.base.BaseListModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {

    // http://dev.product.guhada.com/categories
    @GET("categories")
    Call<BaseListModel<Category>> getCategories();

    // http://dev.product.guhada.com/brands/simple
    @GET("brands/simple")
    Call<BaseListModel<Brand>> getAllBrands();
}
