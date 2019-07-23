package io.temco.guhada.data.server


import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.model.ProductByList
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProductServer {

    companion object{

        @JvmStatic
        fun getCategories(listener: OnServerListener?) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java)
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
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java)
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
         *
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
                RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java)
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
        fun getProductByNewArrivals(unitPerPage : Int, listener: OnServerListener?) {
            if (listener != null) {
                val call = RetrofitManager.createService(Type.Server.PRODUCT, ProductService::class.java, true).getProductByNewArrivals(unitPerPage)
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
         * @author park jungho
         * 19.07.18
         * 플러스 아이템 목록 조회
         */
        @JvmStatic
        fun getProductByPlusItem(unitPerPage : Int, listener: OnServerListener?) {
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




    }

}