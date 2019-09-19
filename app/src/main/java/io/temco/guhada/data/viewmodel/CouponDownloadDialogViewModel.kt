package io.temco.guhada.data.viewmodel

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.coupon.CouponSaveProcess
import io.temco.guhada.data.model.point.PointRequest
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CouponDownloadDialogViewModel : BaseObservableViewModel() {
    var mList: MutableList<Coupon> = mutableListOf()
    var mSellerId = 0L
    var mIsFollowCouponExist = false
    var mCouponSaveProcess = CouponSaveProcess()
    var mOnClickCloseTask: () -> Unit = {}
    var mOnSuccessSaveCouponTask: () -> Unit = {}

    fun onClickClose() = mOnClickCloseTask()
    fun onClickDownload() {
        ServerCallbackUtil.callWithToken(task = {
            if (mIsFollowCouponExist) saveFollow(accessToken = it, successTask = { saveCoupon(idx = 0, accessToken = it) })
            else saveCoupon(idx = 0, accessToken = it)
        })
    }

    // 셀러 팔로우 등록
    private fun saveFollow(accessToken: String, successTask: () -> Unit) {
        val bookMark = BookMarkResponse(target = BookMarkTarget.SELLER.target, targetId = mSellerId)
        UserServer.saveBookMark(OnServerListener { success, o ->
            if (success) {
                successTask()
            } else {
                if (o is BaseModel<*>) ToastUtil.showMessage(o.message)
                else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
            }
        }, accessToken = accessToken, response = bookMark.getProductBookMarkRespose())
    }

    // 쿠폰 발급
    private fun saveCoupon(idx: Int, accessToken: String) {
        if (idx < mList.size) {
            val coupon = mList[idx]
            CouponSaveProcess().apply {
                this.couponId = coupon.couponId ?: -1   // 확인용

                this.discountPrice = coupon.discountPrice
                this.discountRate = coupon.discountRate
                this.discountType = coupon.discountType
                        ?: if (coupon.discountPrice > 0) Coupon.DiscountType.PRICE.type else Coupon.DiscountType.RATE.type
                this.expirePeriod = coupon.expireDueDay.toLong()
                this.maximumDiscountPrice = coupon.maximumDiscountPrice
                this.minimumPrice = coupon.minimumPrice
                this.saveActionType = coupon.saveActionType
                this.serviceType = PointRequest.ServiceType.AOS.type
                this.userId = coupon.userId

                this.keyId = mCouponSaveProcess.keyId
                this.dcategoryId = mCouponSaveProcess.dcategoryId
                this.mcategoryId = mCouponSaveProcess.mcategoryId
                this.scategoryId = mCouponSaveProcess.scategoryId
                this.lcategoryId = mCouponSaveProcess.lcategoryId
                this.sellerId = mCouponSaveProcess.sellerId
            }.let {
                BenefitServer.saveCoupon(OnServerListener { success, o ->
                    if (success) {
                        saveCoupon(idx + 1, accessToken)
                    } else {
                        if (o is BaseModel<*>) ToastUtil.showMessage(o.message)
                        else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
                    }
                }, accessToken = accessToken, couponSaveProcess = it)
            }
        } else {
            mOnSuccessSaveCouponTask()
        }
    }
}