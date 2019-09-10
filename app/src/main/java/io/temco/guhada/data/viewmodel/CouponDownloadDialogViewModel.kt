package io.temco.guhada.data.viewmodel

import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.coupon.CouponConsumption
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
            if (mIsFollowCouponExist) saveFollow(accessToken = it, successTask = { saveCoupon(accessToken = it) })
            else saveCoupon(accessToken = it)
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
    private fun saveCoupon(accessToken: String) {
        Observable.fromIterable(mList)
                .map {
                    //                    mCouponSaveProcess.dealId = it.dealId
//                                        mCouponSaveProcess.keyId = it.couponId?:0
                    //                    mCouponSaveProcess.paymentPrice = it.paymentPrice
                    //                    mCouponSaveProcess.totalDiscountPrice = it.totalDiscountPrice

                    mCouponSaveProcess.discountPrice = it.discountPrice
                    mCouponSaveProcess.discountRate = it.discountRate
                    mCouponSaveProcess.discountType = it.discountType
                            ?: if (it.discountPrice > 0) Coupon.DiscountType.PRICE.type else Coupon.DiscountType.RATE.type
                    mCouponSaveProcess.expirePeriod = it.expireDueDay.toLong()
                    mCouponSaveProcess.maximumDiscountPrice = it.maximumDiscountPrice
                    mCouponSaveProcess.minimumPrice = it.minimumPrice
                    mCouponSaveProcess.saveActionType = it.saveTargetType
                    mCouponSaveProcess.sellerId = it.sellerId ?: 0
                    mCouponSaveProcess.serviceType = PointRequest.ServiceType.AOS.type
                    mCouponSaveProcess.userId = it.userId
                }.subscribe {
                    BenefitServer.saveCoupon(OnServerListener { success, o ->
                        if (success) {
                            ToastUtil.showMessage("쿠폰이 발급되었습니다.")
                            mOnSuccessSaveCouponTask()
                        } else {
                            if (o is BaseModel<*>) ToastUtil.showMessage(o.message)
                            else ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
                        }
                    }, accessToken = accessToken, couponSaveProcess = mCouponSaveProcess)
                }.dispose()
    }
}