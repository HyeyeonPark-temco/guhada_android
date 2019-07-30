package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 상품문의
    - 웹 프론트 작업 된 내용 공유
        ○ /users/my-page/inquiries
 *
 */
class MyPageClaimViewModel (val context : Context) : BaseObservableViewModel() {

    var claimMessages: MutableList<String> = mutableListOf("답변상태 : 전체","답변상태 : 미답변","답변상태 : 완료")
        @Bindable
        get() = field

    var selectedStatusMessage = ObservableField<String>("답변상태 : 전체") // 스피너 표시 메세지
        @Bindable
        get() = field

    fun onShippingMemoSelected(position: Int) {
        if (claimMessages.size > position) {
            val message = claimMessages[position]
            selectedStatusMessage.set(message)
            notifyPropertyChanged(BR.selectedStatusMessage)
        }
    }


}