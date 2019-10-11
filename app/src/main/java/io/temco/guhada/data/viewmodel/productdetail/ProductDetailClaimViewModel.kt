package io.temco.guhada.data.viewmodel.productdetail

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment

class ProductDetailClaimViewModel(private val productId: Long, val listener: ProductDetailClaimFragment.OnProductDetailClaimListener) : BaseObservableViewModel() {
    var emptyVisible = ObservableBoolean(false)
        @Bindable
        get() = field
    var mMoreButtonVisible = ObservableBoolean(false)
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
    var claimResponse: MutableLiveData<ClaimResponse> = MutableLiveData()


    private val getClaimListener = OnServerListener { success, o ->
        if (success) {
            val model = o as BaseModel<*>
            if (model.resultCode == Flag.ResultCode.DATA_NOT_FOUND) {
                mMoreButtonVisible = ObservableBoolean(false)
                notifyPropertyChanged(BR.mMoreButtonVisible)

                emptyVisible = ObservableBoolean(true)
                notifyPropertyChanged(BR.emptyVisible)
            } else {
                this.claimResponse.postValue(model.data as ClaimResponse)

                if (claimStatus == "" && !isMineChecked) {
                    this.totalClaimCount = this.claimResponse.value?.totalElements ?: 0
                    notifyPropertyChanged(BR.totalClaimCount)
                }

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
        ServerCallbackUtil.callWithToken(
                task = { accessToken ->
                    ClaimServer.getClaims(getClaimListener, accessToken = accessToken, productId = productId, isMyInquiry = isMineChecked, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
                },
                invalidTokenTask = {
                    ClaimServer.getClaimsForGuest(getClaimListener, productId = productId, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
                })
    }

    fun onClickMoreClaim() {
        if (claimResponse.value?.last != true) this.getClaims()
        else listener.showMessage(BaseApplication.getInstance().getString(R.string.claim_message_lastitem))
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
        claimPageNo = 0

        listener.clearClaims()
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ClaimServer.getClaims(getClaimListener, accessToken = accessToken, productId = productId, isMyInquiry = checked, status = claimStatus, size = claimPageSize, pageNo = claimPageNo++)
        }, invalidTokenTask = {})
    }
}