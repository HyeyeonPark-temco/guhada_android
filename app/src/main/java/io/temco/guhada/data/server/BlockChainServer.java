package io.temco.guhada.data.server;

import java.io.IOException;
import java.util.List;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.data.model.BlockChain;
import io.temco.guhada.data.retrofit.manager.RetrofitManager;
import io.temco.guhada.data.retrofit.service.BlockChainService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockChainServer {

    public static void getTransactionData(int id, OnServerListener listener) {
        if (listener != null) {
            RetrofitManager.createService(Type.Server.BLOCKCHAIN, BlockChainService.class, false ,false)
                    .getTransactionData(id)
                    .enqueue(new Callback<List<BlockChain>>() {
                        @Override
                        public void onResponse(Call<List<BlockChain>> call, Response<List<BlockChain>> response) {
                            if (response.isSuccessful()) {
                                listener.onResult(true, response.body());
                            } else {
                                try {
                                    listener.onResult(false, response.errorBody().string());
                                } catch (IOException e) {
                                    // e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<BlockChain>> call, Throwable t) {
                            listener.onResult(false, t.getMessage());
                        }
                    });
        }
    }
}
