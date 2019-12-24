package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import com.auth0.android.jwt.JWT
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.shippingaddress.ShippingAddressViewModel

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
class MyPageAddressViewModel(val context: Context, val listener: OnShippingAddressListener) : ShippingAddressViewModel(listener) {

    fun onClickAddNew() {
        if (Preferences.getToken() != null)
            listener.redirectAddShippingAddressActivity()
        else
            ToastUtil.showMessage(context.getString(R.string.login_message_requiredlogin))
    }

}