package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Product;
import io.temco.guhada.data.model.ProductByList;
import io.temco.guhada.data.model.base.BaseListModel;
import io.temco.guhada.data.model.base.BaseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductService {

    // http://dev.product.guhada.com/categories
    @GET("categories")
    Call<BaseListModel<Category>> getCategories();

    // http://dev.product.guhada.com/brands/simple
    @GET("brands/simple")
    Call<BaseListModel<Brand>> getAllBrands();

    @GET("/deals/{id}")
    Call<BaseModel<Product>> getProductDetail(@Path("id") Long id);

    // http://dev.product.guhada.com/products/{productId}?viewType=LIST
    @GET("products/{productId}?viewType=LIST")
    Call<BaseModel<ProductByList>> getProductByList(@Path("productId") String id);
}
