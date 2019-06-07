package io.temco.guhada.view.activity

import android.widget.Toast
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.base.OnBaseActivityListener
import io.temco.guhada.data.viewmodel.WriteClaimViewModel
import io.temco.guhada.databinding.ActivityWriteclaimBinding
import io.temco.guhada.view.activity.base.BindActivity

class WriteClaimActivity : BindActivity<ActivityWriteclaimBinding>() {
    private lateinit var mViewModel: WriteClaimViewModel

    override fun getBaseTag(): String = WriteClaimActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_writeclaim
    override fun getViewType(): Type.View = Type.View.CLAIM

    override fun init() {
        mViewModel = WriteClaimViewModel(object : OnWriteClaimListener {
            override fun showMessage(message: String) {
                Toast.makeText(this@WriteClaimActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun closeActivity(resultCode: Int) {
                setResult(resultCode)
                this@WriteClaimActivity.finish()
            }
        })

        //// 임시 productID 12492
        mViewModel.inquiry.productId = intent.getIntExtra("productId", resources.getString(R.string.temp_productId).toInt())
        mBinding.includeWriteclaimHeader.viewModel = mViewModel
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    interface OnWriteClaimListener : OnBaseActivityListener
}