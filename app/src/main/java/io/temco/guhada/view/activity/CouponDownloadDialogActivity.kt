package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.CouponDownloadDialogViewModel
import io.temco.guhada.databinding.ActivityCoupondownloaddialogBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CouponDownloadListAdapter

class CouponDownloadDialogActivity : BindActivity<ActivityCoupondownloaddialogBinding>() {
    private lateinit var mViewModel: CouponDownloadDialogViewModel


    override fun getBaseTag(): String = CouponDownloadDialogActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_coupondownloaddialog

    override fun getViewType(): Type.View = Type.View.COUPON_DOWNLOAD

    override fun init() {
        mViewModel = CouponDownloadDialogViewModel().apply { mOnClickCloseTask = { finish() } }
        intent.getParcelableArrayListExtra<Coupon>("couponList").let {
            if (it != null) {
                mViewModel.mList = it as MutableList<Coupon>
                mBinding.recyclerviewCoupondownload.adapter = CouponDownloadListAdapter().apply { this.mList = it }
            } else {
                ToastUtil.showMessage(resources.getString(R.string.common_message_error))
                finish()
            }
        }
        mBinding.viewModel = mViewModel
    }
}