package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
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

class DeliveryDetailActivity : BindActivity<ActivityDeliverydetailBinding>() {
    private lateinit var mViewModel: MyPageDeliveryDetailViewModel

    override fun getBaseTag(): String = DeliveryDetailActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_deliverydetail

    override fun getViewType(): Type.View = Type.View.DELIVERY_DETAIL

    override fun init() {
        initHeader()



        mViewModel = MyPageDeliveryDetailViewModel().apply {
            val data = intent.getLongExtra("purchaseId", -1)
            if (data > 0) {
                this.purchaseId = data
            } else {
                ToastUtil.showMessage(getString(R.string.common_message_error))
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

        if (::mViewModel.isInitialized) {
            mViewModel.getOrder()
            mViewModel.getUser()
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
//            mBinding.includeDeliverydetailRefundinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailUserinfo.viewModel = mViewModel

            val receiptButtonVisible = intent.getBooleanExtra("receiptButtonVisible", true)
            mBinding.includeDeliverydetailOrderinfo.buttonDeliverydetailReceipt.visibility = if(receiptButtonVisible) View.VISIBLE else View.GONE

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

    companion object {

        @JvmStatic
        @BindingAdapter("timeStamp")
        fun TextView.bindTimeStamp(timeStamp: Long) {
            this.text = if (timeStamp > 0) CommonUtil.convertTimeStamp(timeStamp)
            else ""
        }
    }
}