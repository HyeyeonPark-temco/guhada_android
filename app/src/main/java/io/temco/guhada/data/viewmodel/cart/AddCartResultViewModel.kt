package io.temco.guhada.data.viewmodel.cart

import io.temco.guhada.common.listener.OnAddCartResultListener
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 장바구니 담기 완료 페이지
 * @author Hyeyeon Park
 */
class AddCartResultViewModel(val mListener: OnAddCartResultListener) : BaseObservableViewModel() {

    fun onClickClose() {
        mListener.hide()
    }

    fun onClickRedirectCart() {
        mListener.redirectProductDetail()
    }
}