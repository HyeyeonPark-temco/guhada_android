package io.temco.guhada.data.viewmodel

import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class EmptyCartViewModel : BaseObservableViewModel() {
    fun onClickContinue(){
        ToastUtil.showMessage("메인으로 이동")
    }
}