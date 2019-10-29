package io.temco.guhada.data.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.model.community.CommunityCriteria
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.search.AutoComplete
import io.temco.guhada.data.model.search.Popular
import io.temco.guhada.data.model.search.ProductSearchFilterValue
import io.temco.guhada.data.model.seller.Criteria
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SearchServer {

    companion object {

        @JvmStatic
        fun <C , R>resultListener(listener: OnServerListener, call: Call<C>, response: Response<R>){
            if (response.code() in 200..400 && response.body() != null) {
                listener.onResult(true, response.body())
            } else {
                try {
                    var msg = Message()
                    var errorBody: String? = response.errorBody()?.string() ?: null
                    if (!errorBody.isNullOrEmpty()) {
                        var gson = Gson()
                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                    }
                    var error = BaseErrorModel(response.code(), response.raw().request().url().toString(), msg)
                    if (CustomLog.flag) CustomLog.L("saveReport", "onResponse body", error.toString())
                    listener.onResult(false, error)
                } catch (e: Exception) {
                    if (CustomLog.flag) CustomLog.E(e)
                    listener.onResult(false, null)
                }
            }
        }


        @JvmStatic
        fun getProductListByCategory(type: Type.ProductOrder, id: Int, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                val body = FilterBody()
                body.categoryIds = arrayListOf(id)
                body.searchResultOrder = Type.ProductOrder.get(type)
                if(CustomLog.flag)CustomLog.L("getProductListByCategory","body",body)
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
                var tmp = ProductSearchFilterValue()
                tmp.brandIds.add(id)
                tmp.searchResultOrder = Type.ProductOrder.get(type)
                val body = FilterBody(tmp)
                /*body.brandIds = intArrayOf(id)
                body.searchResultOrder = Type.ProductOrder.get(type)*/
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
        fun getProductListBySearch(type: Type.ProductOrder, text: String, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                val body = FilterBody()
                body.searchQueries = arrayListOf(text)
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
        fun getSearchPopularKeyword(itemCount: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Request
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true)
                        .getSearchPopularKeyword(itemCount)
                        .enqueue(object : Callback<BaseModel<Popular>> {
                            override fun onResponse(call: Call<BaseModel<Popular>>, response: Response<BaseModel<Popular>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body())
                                    } else {
                                        listener.onResult(false, response.body())
                                    }
                                } else {
                                    try {
                                        listener.onResult(false, response.errorBody()!!.string())
                                    } catch (e: IOException) {
                                        // e.printStackTrace();
                                    }

                                }
                            }

                            override fun onFailure(call: Call<BaseModel<Popular>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }


        @JvmStatic
        fun getSearchAutoComplete(keyword: String, listener: OnServerListener?) {
            if (listener != null) {
                // Request
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true)
                        .getSearchAutoComplete(keyword)
                        .enqueue(object : Callback<BaseModel<AutoComplete>> {
                            override fun onResponse(call: Call<BaseModel<AutoComplete>>, response: Response<BaseModel<AutoComplete>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body())
                                    } else {
                                        listener.onResult(false, response.body())
                                    }
                                } else {
                                    try {
                                        listener.onResult(false, response.errorBody()!!.string())
                                    } catch (e: IOException) {
                                        // e.printStackTrace();
                                    }

                                }
                            }

                            override fun onFailure(call: Call<BaseModel<AutoComplete>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }

        /**
         * 셀러 추천상품(인기상품) 목록 조회
         */
        @JvmStatic
        fun getSellerPopularProductList(listener: OnServerListener, criteria: Criteria, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java).getSellerPopularProductList(criteria = criteria, page = page, unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))


        /**
         * 셀러 연관상품 목록 조회
         */
        @JvmStatic
        fun getSellerRelatedProductList(listener: OnServerListener, criteria: Criteria, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true).getSellerRelatedProductList(criteria = criteria, page = page, unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 커뮤니티 게시판 검색
         */
        @JvmStatic
        fun getCommunityBoardList(listener: OnServerListener, criteria: CommunityCriteria, order: String, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true).getCommunityList(criteria = criteria, order = order, page = page, unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 커뮤니티 게시판 전체글 검색
         */
        @JvmStatic
        fun getCommunityBoardList(listener: OnServerListener, criteria: JsonObject, order: String, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true).getCommunityList(criteria = criteria, order = order, page = page, unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 셀러별 상품 목록 조회
         */
        @JvmStatic
        fun getSellerProductList(listener: OnServerListener, sellerId: Long, order: String, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true).getSellerProductList(sellerId = sellerId, order = order, page = page, unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))



        /**
         * @author park jungho
         * 19.07.18
         * 신상품 목록 조회
         */
        @JvmStatic
        fun getProductByBestItem(unitPerPage: Int, listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, false).getProductByBestItem(unitPerPage)
                RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<HomeDeal>> {
                    override fun onResponse(call: Call<BaseModel<HomeDeal>>, response: Response<BaseModel<HomeDeal>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<HomeDeal>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }






        @JvmStatic
        fun getProductListByCategoryFilter(type: Type.ProductOrder, body: FilterBody, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                body.searchResultOrder = Type.ProductOrder.get(type)
                if(CustomLog.flag)CustomLog.L("getProductListByCategory","body",body)
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
        fun getProductListByBrandFilter(type: Type.ProductOrder, body: FilterBody, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                body.searchResultOrder = Type.ProductOrder.get(type)
                if(CustomLog.flag)CustomLog.L("getProductListByBrandFilter","body",body)
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
        fun getProductListBySearchFilter(type: Type.ProductOrder, body: FilterBody, page: Int, listener: OnServerListener?) {
            if (listener != null) {
                // Body
                body.searchResultOrder = Type.ProductOrder.get(type)
                if(CustomLog.flag)CustomLog.L("getProductListBySearchFilter","body",body)
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


        /**
         * @author park jungho
         * 19.07.18
         * 플러스 아이템 목록 조회
         */
        @JvmStatic
        fun getProductByPlusItem(unitPerPage: Int, listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.SEARCH, SearchService::class.java, true).getProductByPlusItem(unitPerPage)
                RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<HomeDeal>> {
                    override fun onResponse(call: Call<BaseModel<HomeDeal>>, response: Response<BaseModel<HomeDeal>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<HomeDeal>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }



    }

}