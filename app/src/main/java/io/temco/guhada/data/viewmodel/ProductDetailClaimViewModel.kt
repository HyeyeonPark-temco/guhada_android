package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import io.temco.guhada.BR
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment

class ProductDetailClaimViewModel(private val productId: Int, val listener: ProductDetailClaimFragment.OnProductDetailClaimListener) : BaseObservableViewModel() {
    var claimPageNo = 0
    var claimPageSize = 5
    var claimStatus = ""
    var claimResponse: ClaimResponse = ClaimResponse()
        @Bindable
        get() = field

    fun getClaims() {
        if (!this.claimResponse.last) {
            ClaimServer.getClaimsForGuest(OnServerListener { success, o ->
                if (success) {
                    val model = o as BaseModel<*>
                    if (model.resultCode == Flag.ResultCode.DATA_NOT_FOUND) {
                        listener.showMessage("마지막 항목입니다.")
                    } else {
                        this.claimResponse = model.data as ClaimResponse
                        notifyPropertyChanged(BR.claimResponse)
                    }
                } else {
                    listener.showMessage(o as String)
                }
            }, productId = productId, status = claimStatus, size = claimPageSize, pageNo = ++claimPageNo)
        } else {
            listener.showMessage("마지막 항목입니다.")
        }
    }

    fun onClickMoreClaim(claimPageSize: Int) {
        this.claimPageSize = claimPageSize
        this.getClaims()
    }
}