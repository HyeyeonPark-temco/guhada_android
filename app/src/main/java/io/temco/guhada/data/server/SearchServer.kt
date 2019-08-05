package io.temco.guhada.data.server

import java.io.IOException

import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchServer {

    companion object {

        @JvmStatic
        fun getProductListByCategory(type: Type.ProductOrder, id: Int, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                val body = FilterBody()
                body.categoryIds = intArrayOf(id)
                body.searchResultOrder = Type.ProductOrder.get(type)
                // Request
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true)
                        .getFilterProductListData(body, page, Info.LIST_PAGE_UNIT)
                        .enqueue(object : Callback<BaseModel<ProductList>> {
                            override fun onResponse(call: Call<BaseModel<ProductList>>, response: Response<BaseModel<ProductList>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body()!!.data)
                                    } else {
                                        listener.onResult(false, response.body()!!.message)
                                    }
                                } else {
                                    try {
                                        listener.onResult(false, response.errorBody()!!.string())
                                    } catch (e: IOException) {
                                        // e.printStackTrace();
                                    }

                                }
                            }

                            override fun onFailure(call: Call<BaseModel<ProductList>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }

        @JvmStatic
        fun getProductListByBrand(type: Type.ProductOrder, id: Int, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                val body = FilterBody()
                body.brandIds = intArrayOf(id)
                body.searchResultOrder = Type.ProductOrder.get(type)
                // Request
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true)
                        .getFilterProductListData(body, page, Info.LIST_PAGE_UNIT)
                        .enqueue(object : Callback<BaseModel<ProductList>> {
                            override fun onResponse(call: Call<BaseModel<ProductList>>, response: Response<BaseModel<ProductList>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body()!!.data)
                                    } else {
                                        listener.onResult(false, response.body()!!.message)
                                    }
                                } else {
                                    try {
                                        listener.onResult(false, response.errorBody()!!.string())
                                    } catch (e: IOException) {
                                        // e.printStackTrace();
                                    }

                                }
                            }

                            override fun onFailure(call: Call<BaseModel<ProductList>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }


        @JvmStatic
        fun getProductListBySearch(type: Type.ProductOrder,text: String, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Request
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true)
                        .getSearchProductListData(Type.ProductOrder.get(type), page, text,Info.LIST_PAGE_UNIT)
                        .enqueue(object : Callback<BaseModel<ProductList>> {
                            override fun onResponse(call: Call<BaseModel<ProductList>>, response: Response<BaseModel<ProductList>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body()!!.data)
                                    } else {
                                        listener.onResult(false, response.body()!!.message)
                                    }
                                } else {
                                    try {
                                        listener.onResult(false, response.errorBody()!!.string())
                                    } catch (e: IOException) {
                                        // e.printStackTrace();
                                    }

                                }
                            }

                            override fun onFailure(call: Call<BaseModel<ProductList>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }
    }

}