package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Flag.RequestCode.POINT_RESULT_DIALOG
import io.temco.guhada.common.enum.PointDialogFlag
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.CommonUtil
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
            val intent = Intent(this, ReviewPointDialogActivity::class.java)
            intent.putExtra("type", PointDialogFlag.CONFIRM_PURCHASE.flag)
            intent.putExtra("purchaseOrder", mViewModel.purchaseOrder)
            startActivityForResult(intent, Flag.RequestCode.POINT_RESULT_DIALOG)
        }
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) mViewModel.purchaseOrder = it as PurchaseOrder
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.POINT_RESULT_DIALOG.flag && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}