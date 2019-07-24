package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.data.viewmodel.ShippingAddressViewModel
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 배송지관리
    - 주문에서 입력했던 화면과 비슷하게
    - 웹 프론트 작업 된 내용  공유
        ○ 주문자, 배송지, 결제 // /order/order-complete/{purchaseId} (한주문에 전체를 가져오기때문에 목록에서 필요한 값을 뽑아서 써야함?)
        ○ 배송지 관리는 주문, 장바구니에서 사용하는 API를 다시 사용하면됨
 *
 */
class MyPageAddressViewModel (val context : Context, val listener: OnShippingAddressListener) : ShippingAddressViewModel(listener){

}