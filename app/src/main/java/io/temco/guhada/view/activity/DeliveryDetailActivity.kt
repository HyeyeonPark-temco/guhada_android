package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.viewmodel.mypage.MyPageDeliveryDetailViewModel
import io.temco.guhada.databinding.ActivityDeliverydetailBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 주문 상세 내역
 * @author Hyeyeon Park
 */
class DeliveryDetailActivity : BindActivity<ActivityDeliverydetailBinding>() {
    private lateinit var mViewModel: MyPageDeliveryDetailViewModel

    override fun getBaseTag(): String = DeliveryDetailActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_deliverydetail

    override fun getViewType(): Type.View = Type.View.DELIVERY_DETAIL

    override fun init() {
        initHeader()
        mViewModel = MyPageDeliveryDetailViewModel().apply {
            intent.getLongExtra("purchaseId", 0).let { purchaseId ->
                if (purchaseId > 0) {
                    this.purchaseId = purchaseId
                } else {
                    ToastUtil.showMessage(getString(R.string.common_message_error))
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        }

        if (::mViewModel.isInitialized) {
//            intent.getLongExtra("orderProdGroupId", 0).let { orderProdGroupId ->
//                if (orderProdGroupId > 0) {
//                    mViewModel.mOrderProdGroupId = orderProdGroupId
//                    mViewModel.getClaimForm()
//                }
//            }

            mViewModel.getOrder()
            mViewModel.onClickClaimTask = { productId ->
                val intent = Intent(this, WriteClaimActivity::class.java)
                intent.putExtra("productId", productId)
                startActivityForResult(intent, RequestCode.WRITE_CLAIM.flag)
            }
            mViewModel.onClickReceiptTask = { tId ->
                val intent = Intent(this, ReceiptActivity::class.java)
                intent.putExtra("tId", tId)
                startActivityForResult(intent, RequestCode.WRITE_CLAIM.flag)
            }

            mBinding.viewModel = mViewModel
            mBinding.includeDeliverydetailOrderinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailPaymentinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailProductinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailUserinfo.viewModel = mViewModel

            mBinding.includeDeliverydetailProductinfo.status = intent.getStringExtra("status")
//            /**
//             * @author park jungho
//             * 시즌과 상품 이름
//             *
//             * @author Hyeyeon Park
//             * @since 2019.009.24
//             * databinding으로 변경
//             */
//            mBinding.includeDeliverydetailProductinfo.title = if(TextUtils.isEmpty(mViewModel.purchaseOrderResponse.orderList[0].season)) mViewModel.purchaseOrderResponse.orderList[0].productName
//                else mViewModel.purchaseOrderResponse.orderList[0].season+ " "+ mViewModel.purchaseOrderResponse.orderList[0].productName

            initRefundInfo()

            val receiptButtonVisible = intent.getBooleanExtra("receiptButtonVisible", true)
            mBinding.includeDeliverydetailOrderinfo.buttonDeliverydetailReceipt.visibility = if (receiptButtonVisible) View.VISIBLE else View.GONE
            mBinding.executePendingBindings()
        }
    }

    private fun initHeader() {
        mBinding.includeDeliverydetailHeader.title = "주문 내역 상세"
        mBinding.includeDeliverydetailHeader.setOnClickBackButton { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.WRITE_CLAIM.flag && resultCode == Activity.RESULT_OK) {
            ToastUtil.showMessage(getString(R.string.claim_message_add))
        }
    }

    private fun initRefundInfo() {
        val isDeliveryCer = intent.getBooleanExtra("isDeliveryCer", false)
        mViewModel.refundInfoVisible = isDeliveryCer
        mBinding.includeDeliverydetailRefundinfo.viewModel = mViewModel
    }

    companion object {

        @JvmStatic
        @BindingAdapter("timeStamp")
        fun TextView.bindTimeStamp(timeStamp: Long) {
            this.text = if (timeStamp > 0) CommonUtil.convertTimeStampToDate(timeStamp)
            else ""
        }
    }
}