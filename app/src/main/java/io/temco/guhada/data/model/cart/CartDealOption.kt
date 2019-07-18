package io.temco.guhada.data.model.cart

/**
 * 장바구니 상품 dealOptionId 조회 용 클래스
 * @author Hyeyeon Park
 */
class CartDealOption {
    var cartItemId: Long = 0
    var dealOptionList: MutableList<DealOption> = mutableListOf()
}

