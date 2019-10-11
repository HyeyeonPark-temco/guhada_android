package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.HitBuilders
import com.google.firebase.analytics.FirebaseAnalytics
import io.temco.guhada.BuildConfig
import io.temco.guhada.R
import io.temco.guhada.common.ActivityMoveToMain
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.order.PurchaseOrderResponse
import io.temco.guhada.data.model.payment.CalculatePaymentInfo
import io.temco.guhada.data.model.point.ExpectedPointResponse
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.viewmodel.payment.PaymentResultViewModel
import io.temco.guhada.databinding.ActivityPaymentResultBinding
import io.temco.guhada.view.activity.base.BaseActivity
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.payment.PaymentResultOrderAdapter
import io.temco.guhada.view.fragment.mypage.MyPageTabType
import java.lang.Exception

/**
 * 주문 완료 Activity
 * @author Hyeyeon Park
 */
class PaymentResultActivity : BindActivity<ActivityPaymentResultBinding>() {
    private lateinit var mViewModel: PaymentResultViewModel

    override fun getBaseTag(): String = PaymentResultActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_payment_result

    override fun getViewType(): Type.View = Type.View.PAYMENT_RESULT

    override fun init() {
        initHeader()
        initViewModel()
        initExpectedPoint()
        initDiscount()

        // 주문 내역 보기
        mBinding.linearlayoutPaymentresultHistory.setOnClickListener {
            BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, MyPageTabType.DELIVERY.ordinal, true)
            setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE)
            finish()
        }

        mBinding.recyclerView.adapter = PaymentResultOrderAdapter()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = PaymentResultViewModel(object : OnPaymentResultListener {
            override fun redirectMainActivity() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            override fun redirectCartActivity() {
                Intent(this@PaymentResultActivity, CartActivity::class.java).let {
                    this@PaymentResultActivity.startActivity(it)
                    finish()
                }
            }

            override fun closeActivity() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            override fun showMessage(message: String) {
                ToastUtil.showMessage(message)
            }
        }).apply {
            val purchaseOrderResponse = intent.getSerializableExtra("purchaseOrderResponse")
            val shippingMemo = intent.getStringExtra("shippingMemo") ?: ""
            val userName = intent.getStringExtra("userName") ?: ""
            this.userName = userName
            this.shippingMemo = shippingMemo
            if (purchaseOrderResponse != null) {
                this.purchaseOrderResponse = purchaseOrderResponse as PurchaseOrderResponse
                sendAnalyticEvent(this.purchaseOrderResponse)
            }
        }
    }

    private fun sendAnalyticEvent(product : PurchaseOrderResponse){
        try{
            if (getmFirebaseAnalytics() != null) {
                val bundle = Bundle()
                var sizeStr = if((product.orderList.size -1) > 0) "외 $product.orderList.size 건" else ""
                bundle.putString(FirebaseAnalytics.Param.PRICE, product.totalAmount.toString())
                bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, product.orderNumber.toString())
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, product.orderList[0].productName+sizeStr)
                bundle.putString(FirebaseAnalytics.Param.ORIGIN, BuildConfig.BuildType.name)
                getmFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle)
            }
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }
    private fun initHeader() {
        mBinding.includePaymentresultHeader.title = "주문 완료"
        mBinding.includePaymentresultHeader.setOnClickBackButton {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun initExpectedPoint() {
        // 적립 예정 포인트
//        intent.getSerializableExtra("expectedPoint").let {
//            if (it != null) {
//                val expectedPoint = it as ExpectedPointResponse
//                var dusSaveTotalPoint = 0
//                for (item in expectedPoint.dueSavePointList) {
//                    dusSaveTotalPoint += item.freePoint
//                    when (item.pointType) {
//                        PointProcessParam.PointSave.BUY.type -> mBinding.textviewPaymentresultBuypoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
//                        PointProcessParam.PointSave.TEXT_REVIEW.type -> mBinding.textviewPaymentresultTextreviewpoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
//                        PointProcessParam.PointSave.IMG_REVIEW.type -> mBinding.textviewPaymentresultPhotoreviewpoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
//                    }
//                }
//
//                mBinding.textviewPaymentresultExpectedtotalpoint.text = String.format(getString(R.string.common_price_format), dusSaveTotalPoint)
//            }
//        }

        // 적립 내역
        intent.getSerializableExtra("calculatePaymentInfo").let {
            if (it != null) setDiscountPriceAndDueSavePoint(it as CalculatePaymentInfo)
        }
    }

    private fun initDiscount() {
        // 할인 내역
        mBinding.textviewPaymentresultTotaldiscountprice.text = String.format(getString(R.string.common_priceunit_format), intent.getIntExtra("totalDiscountPrice", 0))
        mBinding.textviewPaymentresultCoupondiscountprice.text = String.format(getString(R.string.common_priceunit_format), intent.getIntExtra("couponDiscountPrice", 0))
        mBinding.textviewPaymentresultUsedpoint.text = String.format(getString(R.string.common_priceunit_format), intent.getIntExtra("usedPoint", 0))


    }

    private fun setDiscountPriceAndDueSavePoint(info: CalculatePaymentInfo) {
        var reviewPoint = 0
        var buyPoint = 0

        for (item in info.totalDueSavePointResponseList) {
            when (item.dueSaveType) {
                PointProcessParam.PointSave.REVIEW.type -> reviewPoint = item.totalPoint
                PointProcessParam.PointSave.BUY.type -> buyPoint = item.totalPoint
            }
        }

        mBinding.buyPoint = buyPoint
        mBinding.reviewPoint = reviewPoint
        mBinding.totalDuePoint = info.totalDueSavePoint

        mBinding.textviewPaymentresultBuypoint1.visibility = if (buyPoint > 0) View.VISIBLE else View.GONE
        mBinding.textviewPaymentresultBuypoint2.visibility = if (buyPoint > 0) View.VISIBLE else View.GONE
        mBinding.textviewPaymentresultBuypoint3.visibility = if (buyPoint > 0) View.VISIBLE else View.GONE
        mBinding.textviewPaymentresultReviewpoint1.visibility = if (reviewPoint > 0) View.VISIBLE else View.GONE
        mBinding.textviewPaymentresultReviewpoint2.visibility = if (reviewPoint > 0) View.VISIBLE else View.GONE
        mBinding.textviewPaymentresultReviewpoint3.visibility = if (reviewPoint > 0) View.VISIBLE else View.GONE

     //   mBinding.executePendingBindings()
    }

    interface OnPaymentResultListener {
        fun closeActivity()
        fun showMessage(message: String)
        fun redirectMainActivity()
        fun redirectCartActivity()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("purchaseOrders")
        fun RecyclerView.bindPurchaseOrders(list: MutableList<PurchaseOrder>) {
            if(this.adapter == null)
                this.adapter = PaymentResultOrderAdapter()
            (this.adapter as PaymentResultOrderAdapter).setItems(list)
        }
    }
}