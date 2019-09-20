package io.temco.guhada.data.viewmodel

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Inquiry
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class WriteClaimViewModel : BaseObservableViewModel() {
    val toolBarTitle: String = "상품 문의하기"
    var inquiry: Inquiry = Inquiry()
    lateinit var closeActivity: (resultCode: Int, claim: Claim?) -> Unit

    fun onClickBack() {
        if (::closeActivity.isInitialized) closeActivity(RESULT_CANCELED, null)
    }

    fun onCheckedPrivate(checked: Boolean) {
        inquiry.privateInquiry = checked
    }

    fun onClickSubmit() {
        if (inquiry.content.isEmpty()) {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.claim_write_message_empty))
        } else {
            if (inquiry.inquiryId == null) createInquiry()
            else editInquiry()
        }
    }

    private fun createInquiry() = ClaimServer.saveClaim(OnServerListener { success, o -> claimCallback(success, o) }, inquiry)
    private fun editInquiry() = ClaimServer.editClaim(OnServerListener { success, o -> claimCallback(success, o) }, inquiry)
    private fun claimCallback(success: Boolean, o: Any) =
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val model = o as BaseModel<Claim>
                        when (model.resultCode) {
                            ResultCode.SUCCESS.flag -> {
                                val claim = model.data as Claim
                                if (inquiry.inquiryId == null) ToastUtil.showMessage("문의가 등록되었습니다.")
                                else ToastUtil.showMessage("문의가 수정되었습니다.")
                                if (::closeActivity.isInitialized) closeActivity(RESULT_OK, claim)
                            }

                            ResultCode.NEED_TO_LOGIN.flag -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                        }
                    })

}