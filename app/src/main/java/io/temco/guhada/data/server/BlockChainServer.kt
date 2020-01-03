package io.temco.guhada.data.server

import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.BlockChain
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.BlockChainService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class BlockChainServer {
    companion object {
        @JvmStatic
        fun getTransactionData(id: Int, listener: OnServerListener?) {
            if (listener != null) {
                RetrofitManager.createService(Type.Server.BLOCKCHAIN, BlockChainService::class.java, false, false)
                        .getTransactionData(id)
                        .enqueue(object : Callback<List<BlockChain>> {
                            override fun onResponse(call: Call<List<BlockChain>>, response: Response<List<BlockChain>>) {
                                if (response.isSuccessful) {
                                    listener.onResult(true, response.body())
                                } else {
                                    try {
                                        listener.onResult(false, response.errorBody()!!.string())
                                    } catch (e: IOException) {
                                        // e.printStackTrace();
                                    }

                                }
                            }

                            override fun onFailure(call: Call<List<BlockChain>>, t: Throwable) {
                                listener.onResult(false, t.message)
                            }
                        })
            }
        }
    }

}