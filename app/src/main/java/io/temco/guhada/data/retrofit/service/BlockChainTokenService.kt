package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.blockchain.TokenList
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface BlockChainTokenService {

    /**
     * 구하다의 전체 토큰리스트와 내가 가진 토큰 보유량
     */
    @GET("/blockchain-wallet/token-list")
    fun getTokenList(@Header("Authorization") accessToken: String): Call<BaseModel<TokenList>>

    /**
     * 해당하는 토큰의 입금 계좌관련된 정보
     */
    @GET("/blockchain-wallet/token-address")
    fun getTokenAddress(@Header("Authorization") accessToken: String, @Query("tokenName") tokenName: String): Call<BaseModel<JSONObject>>

}