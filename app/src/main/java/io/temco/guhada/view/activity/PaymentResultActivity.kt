package io.temco.guhada.view.activity

import android.app.Activity
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.order.PurchaseOrderResponse
import io.temco.guhada.data.model.point.ExpectedPointResponse
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.viewmodel.payment.PaymentResultViewModel
import io.temco.guhada.databinding.ActivityPaymentResultBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.payment.PaymentResultOrderAdapter

class PaymentResultActivity : BindActivity<ActivityPaymentResultBinding>() {
    private lateinit var mViewModel: PaymentResultViewModel

    override fun getBaseTag(): String = PaymentResultActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_payment_result

    override fun getViewType(): Type.View = Type.View.PAYMENT_RESULT

    override fun init() {
        initHeader()

        mViewModel = PaymentResultViewModel(object : OnPaymentResultListener {
            override fun redirectMainActivity() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            override fun closeActivity() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            override fun showMessage(message: String) {
                Toast.makeText(this@PaymentResultActivity, message, Toast.LENGTH_SHORT).show()
            }
        }).apply {
            val purchaseOrderResponse = intent.getSerializableExtra("purchaseOrderResponse")
            val shippingMemo = intent.getStringExtra("shippingMemo") ?: ""
            val userName = intent.getStringExtra("userName") ?: ""
            this.userName = userName
            this.shippingMemo = shippingMemo
            if (purchaseOrderResponse != null) {
                this.purchaseOrderResponse = purchaseOrderResponse as PurchaseOrderResponse
            }
        }

        // 할인 내역
        mBinding.textviewPaymentresultTotaldiscountprice.text = String.format(getString(R.string.common_priceunit_format), intent.getIntExtra("totalDiscountPrice", 0))
        mBinding.textviewPaymentresultCoupondiscountprice.text = String.format(getString(R.string.common_priceunit_format), intent.getIntExtra("couponDiscountPrice", 0))
        mBinding.textviewPaymentresultUsedpoint.text = String.format(getString(R.string.common_priceunit_format), intent.getIntExtra("usedPoint", 0))

        // 적립 예정 포인트
        intent.getSerializableExtra("expectedPoint").let {
            if (it != null) {
                val expectedPoint = it as ExpectedPointResponse
                var dusSaveTotalPoint = 0
                for (item in expectedPoint.dueSavePointList) {
                    dusSaveTotalPoint += item.freePoint
                    when (item.pointType) {
                        PointProcessParam.PointSave.BUY.type -> mBinding.textviewPaymentresultBuypoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
                        PointProcessParam.PointSave.TEXT_REVIEW.type -> mBinding.textviewPaymentresultTextreviewpoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
                        PointProcessParam.PointSave.IMG_REVIEW.type -> mBinding.textviewPaymentresultPhotoreviewpoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
                    }
                }

                mBinding.textviewPaymentresultExpectedtotalpoint.text = String.format(getString(R.string.common_price_format), dusSaveTotalPoint)
            }
        }

        mBinding.recyclerView.adapter = PaymentResultOrderAdapter()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initHeader() {
        mBinding.includePaymentresultHeader.title = "주문 완료"
        mBinding.includePaymentresultHeader.setOnClickBackButton {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    interface OnPaymentResultListener {
        fun closeActivity()
        fun showMessage(message: String)
        fun redirectMainActivity()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("purchaseOrders")
        fun RecyclerView.bindPurchaseOrders(list: MutableList<PurchaseOrder>) {
            (this.adapter as PaymentResultOrderAdapter).setItems(list)
        }
    }
}