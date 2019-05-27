package io.temco.guhada.data.retrofit.service;

import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.body.FilterBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SearchService {

    // http://dev.search.guhada.com/ps/search/filter?page=1&unitPerPage=20
    @POST("ps/search/filter")
    Call<BaseModel<ProductList>> getFilterProductListData(
            @Body FilterBody filter,
            @Query("page") int page,
            @Query("unitPerPage") int unit);
}