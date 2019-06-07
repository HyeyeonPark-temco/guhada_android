package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment

class ProductDetailClaimViewModel(private val productId: Int, val listener: ProductDetailClaimFragment.OnProductDetailClaimListener) : BaseObservableViewModel() {
    var emptyVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var totalClaimCount = 0
        @Bindable
        get() = field
    var mineVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var isMineChecked = false
    var claimPageNo = 0
    var claimPageSize = 5
    var claimStatus = ""
    var claimResponse: ClaimResponse = ClaimResponse()
        @Bindable
        get() = field
    private val getClaimListener = OnServerListener { success, o ->
        if (success) {
            val model = o as BaseModel<*>
            if (model.resultCode == Flag.ResultCode.DATA_NOT_FOUND) {
                listener.showMessage("마지막 항목입니다.")
                emptyVisibility = ObservableInt(View.VISIBLE)
                notifyPropertyChanged(BR.emptyVisibility)
            } else {
                this.claimResponse = model.data as ClaimResponse

                if (claimStatus == "") {
                    this.totalClaimCount = this.claimResponse.totalElements
                    notifyPropertyChanged(BR.totalClaimCount)
                }
                notifyPropertyChanged(BR.claimResponse)
                emptyVisibility = ObservableInt(View.GONE)
                notifyPropertyChanged(BR.emptyVisibility)
            }
        } else {
            if (o != null) listener.showMessage(o as String)
            else listener.showMessage("오류") // 임시 메세지

            // 임시 처리
            emptyVisibility = ObservableInt(View.GONE)
            notifyPropertyChanged(BR.emptyVisibility)
        }
    }

    fun getClaims() {
        if (!this.claimResponse.last) {
            if (Preferences.getToken() != null) {
                ClaimServer.getClaims(getClaimListener, productId = productId, isMyInquiry = isMineChecked, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
            } else {
                ClaimServer.getClaimsForGuest(getClaimListener, productId = productId, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
            }
        } else {
            listener.showMessage("마지막 항목입니다.")
        }
    }

    fun onClickMoreClaim(claimPageSize: Int) {
        this.claimPageSize = claimPageSize
        this.getClaims()
    }

    // 상품 문의하기
    fun onClickWriteClaimForProduct() {
        if (Preferences.getToken() != null) {
            listener.redirectWriteClaimActivity()
        } else {
            listener.redirectLoginActivity()
        }
    }

    fun onCheckedMine(checked: Boolean) {
        this.isMineChecked = checked
        claimPageSize = 5
        claimPageNo = 0

        listener.clearClaims()
        ClaimServer.getClaims(getClaimListener, productId = productId, isMyInquiry = checked, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
    }
}