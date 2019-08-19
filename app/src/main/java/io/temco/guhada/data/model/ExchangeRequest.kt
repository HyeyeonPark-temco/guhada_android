package io.temco.guhada.data.model

/**
 * 교환 신청 클래스
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
class ExchangeRequest {
    var alreadySend = true
    var claimShippingPriceType = ""
    var exchangeReason = ""
    var exchangeReasonDetail = ""
    var shippingCompanyCode = ""
    var invoiceNo = 0L
    var orderProdGroupId = 0L
    var quantity = 1
    var exchangeShippingAddress : UserShipping = UserShipping()
}
