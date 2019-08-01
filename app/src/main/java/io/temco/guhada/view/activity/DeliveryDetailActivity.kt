package io.temco.guhada.view.activity

import android.app.Activity
import android.widget.TextView
import androidx.databinding.BindingAdapter
import io.temco.guhada.R
import io.temco.guhada.common.Type
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
            mBinding.viewModel = mViewModel
            mBinding.includeDeliverydetailOrderinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailPaymentinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailProductinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailRefundinfo.viewModel = mViewModel
            mBinding.includeDeliverydetailUserinfo.viewModel = mViewModel
            mBinding.executePendingBindings()
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