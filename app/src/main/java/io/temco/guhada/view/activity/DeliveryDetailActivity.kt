package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
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
    var refundVisible = false

    override fun getBaseTag(): String = DeliveryDetailActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_deliverydetail

    override fun getViewType(): Type.View = Type.View.DELIVERY_DETAIL

    override fun init() {
        initHeader()
        initViewModel()

        if (::mViewModel.isInitialized) {
            initExpectedRefundPrice()

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
            mBinding.includeDeliverydetailPaymentinfo1.viewModel = mViewModel
            mBinding.includeDeliverydetailPaymentinfo2.viewModel = mViewModel
//            mBinding.includeDeliverydetailProductinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailUserinfo.viewModel = mViewModel

//            mBinding.includeDeliverydetailProductinfo.status = intent.getStringExtra("status")
            initRefundInfo()

            val receiptButtonVisible = intent.getBooleanExtra("receiptButtonVisible", true)
            mBinding.includeDeliverydetailOrderinfo.buttonDeliverydetailReceipt.visibility = if (receiptButtonVisible) View.VISIBLE else View.GONE
            mBinding.executePendingBindings()
        }
    }

    private fun initViewModel() {
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
    }

    private fun initExpectedRefundPrice() {
        intent.getBooleanExtra("refundVisible", false).let {
            refundVisible = it
            mBinding.includeDeliverydetailRefundinfo.constraintlayoutRequestcancelorderRefund.visibility = if (refundVisible) View.VISIBLE else View.GONE
        }

        intent.getLongExtra("orderClaimGroupId", 0).let { orderClaimGroupId ->
            if(CustomLog.flag)CustomLog.L("initExpectedRefundPrice","orderClaimGroupId",orderClaimGroupId)
            if (orderClaimGroupId > 0) {
                mViewModel.mOrderClaimGroupId = orderClaimGroupId
                mViewModel.getOrder()
                mViewModel.getExpectedRefundPrice()
                mBinding.recyclerviewDeliverydetailProductlist.visibility = View.GONE
                mBinding.includeDeliverydetailProductinfo.constraintlayoutDeliverydetailProductinfo.visibility = View.VISIBLE
                mBinding.includeDeliverydetailPaymentinfo1.constraintlayoutPaymentinfoContainer.visibility = View.GONE
            } else {
                mViewModel.getOrder()
                mBinding.recyclerviewDeliverydetailProductlist.visibility = View.VISIBLE
                mBinding.includeDeliverydetailProductinfo.constraintlayoutDeliverydetailProductinfo.visibility = View.GONE
                mBinding.includeDeliverydetailPaymentinfo2.constraintlayoutPaymentinfoContainer.visibility = View.GONE
            }
        }

        mViewModel.mExpectedRefundPrice.observe(this, Observer {
            mBinding.includeDeliverydetailRefundinfo.expectedRefundPrice = it
            mBinding.includeDeliverydetailPaymentinfo1.expectedRefundPrice = it
            mBinding.includeDeliverydetailPaymentinfo2.expectedRefundPrice = it
            if(CustomLog.flag)CustomLog.L("mExpectedRefundPrice",mViewModel.mExpectedRefundPrice.value)
            mBinding.executePendingBindings()
        })

        mViewModel.mExpectedRefundInfo.observe(this, Observer {
            mBinding.includeDeliverydetailRefundinfo.expectedRefundInfo = it
            mBinding.includeDeliverydetailPaymentinfo1.expectedRefundInfo = it
            mBinding.includeDeliverydetailPaymentinfo2.expectedRefundInfo = it
            mBinding.includeDeliverydetailProductinfo.info = it
            if(CustomLog.flag)CustomLog.L("mExpectedRefundInfo",mViewModel.mExpectedRefundInfo.value)
            mBinding.executePendingBindings()
        })
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