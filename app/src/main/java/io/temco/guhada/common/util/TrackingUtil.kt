package io.temco.guhada.common.util

import android.text.TextUtils
import com.kakao.ad.common.json.CompleteRegistration
import com.kakao.ad.common.json.ViewCart
import com.kakao.ad.tracker.send
import com.kochava.base.Tracker
import io.temco.guhada.BuildConfig
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.order.PurchaseOrder

/**
 * Tracking 관련 Util
 * @see io.temco.guhada.common.enum.TrackingEvent
 * @author Hyeyeon Park
 */
object TrackingUtil {
    private var mFlag = BuildConfig.BUILD_TYPE.contentEquals("release")

    @JvmStatic
    fun sendKochavaEvent(eventName: String, value: String = "") {
        if (mFlag) {
            Tracker.sendEvent(eventName, value)

            val event = CompleteRegistration()
            event.tag = eventName
            event.a().put("type",value)
            event.send()
        }
    }

    @JvmStatic
    fun sendKochavaEvent(event: Tracker.Event){
        if (mFlag) Tracker.sendEvent(event)
    }


    @JvmStatic
    fun sendSignupEvent(eventName: String, value: String = "") {
        if (mFlag) {
            Tracker.sendEvent(eventName, value)
            val event = CompleteRegistration()
            event.tag = eventName
            event.a().put("type",value)
            event.send()
        }
    }


    @JvmStatic
    fun sendAddToCart(cart : Cart){
        Tracker.Event(TrackingEvent.Cart.Add_To_Cart.eventName).let {
            it.addCustom("dealId", cart.dealId)
            it.addCustom("sellerId", cart.sellerId)
            it.addCustom("brandName", cart.brandName)
            it.addCustom("dealName", cart.dealName)
            it.addCustom("sellPrice", cart.sellPrice.toString())
            it.addCustom("discountPrice", cart.discountPrice.toString())
            if (!TextUtils.isEmpty(cart.season)) it.addCustom("season", cart.season)
            //  it.addCustom("dealId", product.value?.dealId.toString())
            //  it.addCustom("productId", product.value?.productId.toString())
            //  it.addCustom("brandId", product.value?.brandId.toString())
            //  it.addCustom("sellerId", product.value?.sellerId.toString())
            //  it.addCustom("season", product.value?.season ?: cart.season)
            //  it.addCustom("name", product.value?.name ?: cart.dealName)
            //  it.addCustom("sellPrice", product.value?.sellPrice.toString())
            //  it.addCustom("discountPrice", product.value?.discountPrice.toString())
            sendKochavaEvent(it)
        }
    }

    @JvmStatic
    fun sendViewCart(){
        val event = ViewCart()
        event.tag = TrackingEvent.Cart.Add_To_Cart.eventName
        event.send()

    }

}