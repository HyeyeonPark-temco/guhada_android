package io.temco.guhada.data.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.model.order.RequestOrder
import io.temco.guhada.data.model.payment.CalculatePaymentInfo
import io.temco.guhada.data.server.OrderServer

class CouponSelectDialogViewModel : BaseObservable() {
    class CouponFlag {
        val NOT_SELECT_COUPON_ID: Long = -1
        val NOT_SELECT_COUPON_NUMBER = "NOT_SELECT"
    }

    // key: dealId      value: cartId
    var mCartIdMap = hashMapOf<Long, Long>()

    // key: dealId      value: BenefitOrderProductCouponResponse
    var mSelectedCouponMap = hashMapOf<Long, CouponInfo.BenefitOrderProductCouponResponse?>()

    /*
      - 쿠폰 선택      : mSelectedCouponInfo<couponNumber, dealId>
      - 적용 안함 선택 : mSelectedCouponInfo<dealId, "NOT_SELECT">
    */
    var mSelectedCouponInfo = ObservableField(hashMapOf<String?, Any?>())
        @Bindable
        get() = field

    var mSelectedDealId = 0L
    var mTotalDiscountPrice = ObservableInt(0)
        @Bindable
        get() = field
    var mTotalProductPrice = ObservableInt(0)
        @Bindable
        get() = field
    var mTotalProductCount = ObservableInt(0)
        @Bindable
        get() = field

    var mSelectedCoupon = ObservableField<CouponWallet>(CouponWallet().apply { this.couponId = -1 })
        @Bindable
        get() = field

    var mPointConsumption = 0

    var mCouponInfo = CouponInfo()
    var mCalculatePaymentInfo = MutableLiveData<CalculatePaymentInfo>()


    fun calculatePaymentInfo() {
        val jsonArray = JsonArray()
        for (dealId in mCartIdMap.keys) {
            if (mCartIdMap[dealId] != null) {
                val couponNumber = mSelectedCouponMap[dealId]
                RequestOrder.CartItemPayment().apply {
                    this.cartItemId = mCartIdMap[dealId]!!
                    this.couponNumber = couponNumber?.couponNumber ?: ""
                }.let {
                    val element = JsonParser().parse(Gson().toJson(it))
                    jsonArray.add(element)
                }
            }
        }


        val jsonObject = JsonObject()
        jsonObject.add("cartItemPayments", jsonArray)
        jsonObject.addProperty("consumptionPoint", mPointConsumption)

        ServerCallbackUtil.callWithToken(task = { accessToken ->
            OrderServer.getCalculatePaymentInfo(OnServerListener { success, o ->
                if (success && o is BaseModel<*>) {
                    if (o.resultCode == ResultCode.SUCCESS.flag)
                        mCalculatePaymentInfo.postValue(o.data as CalculatePaymentInfo)
                    else
                        ToastUtil.showMessage(o.message)
                } else
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
            }, accessToken = accessToken, jsonObject = jsonObject)
        })

    }
}