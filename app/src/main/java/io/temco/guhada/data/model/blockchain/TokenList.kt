package io.temco.guhada.data.model.blockchain

import java.io.Serializable

data class TokenList(var tokenName : String = "",
                     val tokenNameText : String = "",
                     val tokenImageUrl : String = "",
                     val balance: Int) : Serializable