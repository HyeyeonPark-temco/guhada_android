package io.temco.guhada.view.activity

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.ConfirmPurchaseViewModel
import io.temco.guhada.databinding.ActivityConfirmpurchaseBinding

class ConfirmPurchaseActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityConfirmpurchaseBinding
    private lateinit var mViewModel: ConfirmPurchaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirmpurchase)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setMargin()
        initViewModel()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun setMargin() {
        val attr = window.attributes
        attr.y = -40
        window.attributes = attr
    }

    private fun initViewModel() {
        mViewModel = ConfirmPurchaseViewModel()
        mViewModel.closeActivityTask = { finish() }
        mViewModel.successConfirmPurchaseTask = {

        }
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) mViewModel.purchaseOrder = it as PurchaseOrder
        }
    }
}