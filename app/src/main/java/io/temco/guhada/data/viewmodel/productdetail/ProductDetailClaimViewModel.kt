package io.temco.guhada.data.viewmodel.productdetail

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment

class ProductDetailClaimViewModel(private val productId: Long, val listener: ProductDetailClaimFragment.OnProductDetailClaimListener) : BaseObservableViewModel() {
    var emptyVisible = ObservableBoolean(false)
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
    var claimResponse: ObservableField<ClaimResponse> = ObservableField(ClaimResponse())
        @Bindable
        get() = field

    private val getClaimListener = OnServerListener { success, o ->
        if (success) {
            val model = o as BaseModel<*>
            if (model.resultCode == Flag.ResultCode.DATA_NOT_FOUND) {
                if (this.claimResponse.get()?.content?.isNotEmpty()?:false && this.claimResponse.get()?.last?:false) {
                    listener.showMessage(BaseApplication.getInstance().getString(R.string.claim_message_lastitem))
                }

                notifyPropertyChanged(BR.claimResponse)
                emptyVisible = ObservableBoolean(true)
                notifyPropertyChanged(BR.emptyVisible)
            } else {
                this.claimResponse = ObservableField(model.data as ClaimResponse)

                if (claimStatus == "" && !isMineChecked) {
                    this.totalClaimCount = this.claimResponse.get()?.totalElements?:0
                    notifyPropertyChanged(BR.totalClaimCount)
                }

                notifyPropertyChanged(BR.claimResponse)
                emptyVisible = ObservableBoolean(false)
                notifyPropertyChanged(BR.emptyVisible)
            }
        } else {
            emptyVisible = ObservableBoolean(true)
            notifyPropertyChanged(BR.emptyVisible)
        }

        if (Preferences.getToken() == null) {
            mineVisibility = ObservableInt(View.GONE)
            notifyPropertyChanged(BR.mineVisibility)
        }
    }

    fun getClaims() {
        if (!(this.claimResponse.get()?.last?:false)) {
            ServerCallbackUtil.callWithToken(
                    task = { accessToken ->
                        ClaimServer.getClaims(getClaimListener, accessToken = accessToken, productId = productId, isMyInquiry = isMineChecked, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
                    },
                    invalidTokenTask = {
                        ClaimServer.getClaimsForGuest(getClaimListener, productId = productId, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
                    })
        } else {
            listener.showMessage(BaseApplication.getInstance().getString(R.string.claim_message_lastitem))
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

    fun onClickUserClaimSeller() {
        if (Preferences.getToken() != null) {
            listener.redirectUserClaimSellerActivity()
        } else {
            listener.redirectLoginActivity()
        }
    }


    fun onCheckedMine(checked: Boolean) {
        this.isMineChecked = checked
        claimPageSize = 5
        claimPageNo = 0

        listener.clearClaims()
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.getClaims(getClaimListener, accessToken = accessToken, productId = productId, isMyInquiry = checked, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
        })
    }
}