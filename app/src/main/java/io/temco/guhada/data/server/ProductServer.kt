package io.temco.guhada.data.server


import com.google.gson.Gson
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.model.LuckyDrawWinner
import io.temco.guhada.data.model.ProductByList
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.event.LuckyEvent
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.Keyword
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.ProductService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProductServer {

    companion object {

        @JvmStatic
        fun <C, R> resultListener(listener: OnServerListener, call: Call<C>, response: Response<R>) {
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
        fun getCategories(listener: OnServerListener?) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true)
                        .categories
                        .enqueue(object : Callback<BaseModel<Category>> {
                            override fun onResponse(call: Call<BaseModel<Category>>, response: Response<BaseModel<Category>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body()!!.list)
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

                            override fun onFailure(call: Call<BaseModel<Category>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }

        @JvmStatic
        fun getAllBrands(listener: OnServerListener?) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true)
                        .allBrands
                        .enqueue(object : Callback<BaseModel<Brand>> {
                            override fun onResponse(call: Call<BaseModel<Brand>>, response: Response<BaseModel<Brand>>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.resultCode == 200) {
                                        listener.onResult(true, response.body()!!.list)
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

                            override fun onFailure(call: Call<BaseModel<Brand>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }

        /**
         * 상품 상세 조회
         * @author Hyeyeon Park
         * @param listener OnServerListener
         * @param id       dealId
         */
        @JvmStatic
        fun getProductDetail(listener: OnServerListener, id: Long?) {
            RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true).getProductDetail(id).enqueue(object : Callback<BaseModel<Product>> {
                override fun onResponse(call: Call<BaseModel<Product>>, response: Response<BaseModel<Product>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Product>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        // 상품 상세 조회
        @JvmStatic
        fun getProductByList(id: String, listener: OnServerListener?) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false, false)
                        .getProductByList(id)
                        .enqueue(object : Callback<BaseModel<ProductByList>> {
                            override fun onResponse(call: Call<BaseModel<ProductByList>>, response: Response<BaseModel<ProductByList>>) {
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

                            override fun onFailure(call: Call<BaseModel<ProductByList>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }


        /**
         * @author park jungho
         * 19.07.18
         * 신상품 목록 조회
         */
        @JvmStatic
        fun getProductByNewArrivals(unitPerPage: Int, listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false).getProductByNewArrivals(unitPerPage)
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

        /**
         * 상품 목록 조회 API
         * @since 2019.09.18
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun getProductListByOnlyPage(listener: OnServerListener, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true, false).getProductListByOnlyPage(unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))


        /**
         * @author park jungho
         * 19.07.18
         * 플러스 아이템 목록 조회
         */
        @JvmStatic
        fun getProductByPlusItem(unitPerPage: Int, listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true).getProductByPlusItem(unitPerPage)
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

        /**
         * 상품 목록 조회
         * @author Hyeyeon Park
         * @since 2019.08.14
         */
        @JvmStatic
        fun getProductListBySellerId(listener: OnServerListener, sellerId: Long, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false, false).getProductListBySellerId(sellerId = sellerId, page = page, unitPerPage = unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) })
                )

        /**
         * 택배사 목록 조회
         * @author Hyeyeon Park
         * @since 2019.08.18
         */
        @JvmStatic
        fun getShippingCompanyList(listener: OnServerListener, type: String) =
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false, false).getShippingCompanies(type = type).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))


        /**
         * @author park jungho
         * 19.09.19
         * 핫키워드 목록 조회
         */
        @JvmStatic
        fun getProductByKeyword(listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false).getProductByKeyword()
                RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Keyword>> {
                    override fun onResponse(call: Call<BaseModel<Keyword>>, response: Response<BaseModel<Keyword>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<Keyword>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }


        /**
         * 타임딜 리스트 조회
         * @author Hyeyeon Park
         * @since 2019.10.23
         */
        @JvmStatic
        fun getTimeDeal(listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false, false).getTimeDeal().enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))


        /**
         * @author park jungho
         * 19.09.19
         * 럭키드로우 목록 조회
         */
        @JvmStatic
        fun getLuckyDraws(listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, false).getLuckyDraws()
                RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<LuckyEvent>> {
                    override fun onResponse(call: Call<BaseModel<LuckyEvent>>, response: Response<BaseModel<LuckyEvent>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }
                    override fun onFailure(call: Call<BaseModel<LuckyEvent>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }


        /**
         * @author park jungho
         *
         * 럭키드로우 응모하기
         */
        @JvmStatic
        fun getRequestLuckyDraw(listener: OnServerListener?,accessToken: String,id: String) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true,false)
                        .getRequestLuckyDraw(accessToken, id)
                        .enqueue(object : Callback<BaseModel<JSONObject>> {
                            override fun onResponse(call: Call<BaseModel<JSONObject>>, response: Response<BaseModel<JSONObject>>) {
                                listener.onResult(response.isSuccessful, response.body())
                            }
                            override fun onFailure(call: Call<BaseModel<JSONObject>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }


        /**
         * @author park jungho
         *
         * 럭키드로우 응모하기
         */
        @JvmStatic
        fun getRequestLuckyDrawWinner(listener: OnServerListener?,accessToken: String, luckyDrawWinner:LuckyDrawWinner) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true)
                        .getRequestLuckyDrawWinner(accessToken, luckyDrawWinner)
                        .enqueue(object : Callback<BaseModel<JSONObject>> {
                            override fun onResponse(call: Call<BaseModel<JSONObject>>, response: Response<BaseModel<JSONObject>>) {
                                listener.onResult(response.isSuccessful, response.body())
                            }
                            override fun onFailure(call: Call<BaseModel<JSONObject>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }




    }

}