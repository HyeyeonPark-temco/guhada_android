package io.temco.guhada.data.model.blockchain

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.TokenActionType
import io.temco.guhada.view.adapter.TokenHistoryAdapter.Companion.bindTextColor

/**
 * 마이페이지 > 토큰 > 상세 리스트 model
 * @author Hyeyeon Park
 * @since 2019.11.28
 */
class TokenHistory {
    var count = 0
    var page = 0
    var totalPage = 0

    var currentTokenBalance = 0
    var tokenImageUrl = ""
    var tokenName = ""
    var userTokenItemResponseList = mutableListOf<TokenItemResponse>()

    class TokenItemResponse {
        var changedBalance = 0
        var completeTimestamp = 0L
        var tokenActionType = ""
        var tokenActionTypeInfo = ""
        var tokenActionTypeMessage = ""
        var tokenActionTypeText = ""

        fun getBalanceText() : String =
                when (tokenActionType) {
                    TokenActionType.SAVE.type -> "+${String.format(BaseApplication.getInstance().getString(R.string.common_price_format), changedBalance)}"
                    TokenActionType.CHANGE_POINT.type -> "-${String.format(BaseApplication.getInstance().getString(R.string.common_price_format), changedBalance)}"
                    else -> "+${String.format(BaseApplication.getInstance().getString(R.string.common_price_format), changedBalance)}"
                }
    }
}