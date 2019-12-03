package io.temco.guhada.data.model.blockchain

import io.temco.guhada.common.util.CustomLog

data class TokenAddress (var pointRatio : Int = 0,
                         var publicKey : String = "",
                         var qrImageUrl : String = "",
                         var tokenImageUrl : String = "",
                         var tokenName : String = "",
                         var tokenNameText : String = "",
                         var tokenRatio : Int = 0){
    override fun toString(): String {
        if(CustomLog.flag)return "TokenAddress(pointRatio=$pointRatio, publicKey='$publicKey', qrImageUrl='$qrImageUrl', tokenImageUrl='$tokenImageUrl', tokenName='$tokenName', tokenNameText='$tokenNameText', tokenRatio=$tokenRatio)"
        else return ""
    }
}