package io.temco.guhada.view.activity

import android.app.Activity
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.ProductBridge
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.PurchaseOrder
import io.temco.guhada.data.model.PurchaseOrderResponse
import io.temco.guhada.data.viewmodel.PaymentResultViewModel
import io.temco.guhada.databinding.ActivityPaymentResultBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.PaymentResultOrderAdapter

class PaymentResultActivity : BindActivity<ActivityPaymentResultBinding>() {
    private lateinit var mViewModel: PaymentResultViewModel

    override fun getBaseTag(): String = PaymentResultActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_payment_result

    override fun getViewType(): Type.View = Type.View.PAYMENT_RESULT

    override fun init() {
        initHeader()

        mViewModel = PaymentResultViewModel(object : OnPaymentResultListener {
            override fun redirectMainActivity() {
                ProductBridge.removeProductDetailFragment()
                ProductBridge.mainActivity.removeProductFragment()
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