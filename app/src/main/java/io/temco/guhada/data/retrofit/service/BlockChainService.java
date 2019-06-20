package io.temco.guhada.data.retrofit.service;

import java.util.List;

import io.temco.guhada.data.model.BlockChain;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BlockChainService {

    @GET("guhada/blockchain/transact-Data/{productId}")
    Call<List<BlockChain>> getTransactionData(@Path("productId") int id);
}
