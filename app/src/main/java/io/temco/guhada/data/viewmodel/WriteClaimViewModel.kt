package io.temco.guhada.data.viewmodel

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.backup.BackupAgent
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag.ResultCode.NEED_TO_LOGIN
import io.temco.guhada.common.Flag.ResultCode.SUCCESS
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.InquiryRequest
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.WriteClaimActivity

class WriteClaimViewModel(val listener: WriteClaimActivity.OnWriteClaimListener) : BaseObservableViewModel() {
    val toolBarTitle: String = "상품 문의하기"
    val inquiry: InquiryRequest = InquiryRequest()

    fun onClickBack() {
        listener.closeActivity(RESULT_CANCELED)
    }

    fun onCheckedPrivate(checked: Boolean) {
        inquiry.privateInquiry = checked
    }

    fun onClickSubmit() {
        if(inquiry.content.isEmpty()){
            listener.showMessage(BaseApplication.getInstance().getString(R.string.claim_write_message_empty))
        }else {
            ClaimServer.saveClaim(OnServerListener { success, o ->
                if (success) {
                    val model = o as BaseModel<ClaimResponse.Claim>
                    when (model.resultCode) {
                        SUCCESS -> {
                            // 문의 리스트 REFRESH
                            val claim = model.data as ClaimResponse.Claim
                            listener.showMessage("[${claim.id}] 문의가 등록되었습니다.")
                            listener.closeActivity(RESULT_OK)
                        }
                        NEED_TO_LOGIN -> {
                            listener.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                        }
                    }
                } else {
                    // 임시 메세지
                    listener.showMessage(o?.toString() ?: "오류")
                }
            }, inquiry)
        }
    }
}