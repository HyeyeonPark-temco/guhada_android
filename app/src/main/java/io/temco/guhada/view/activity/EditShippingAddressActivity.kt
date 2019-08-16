package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.Flag.RequestCode.SEARCH_ZIP
import io.temco.guhada.common.listener.OnEditShippingAddressListener
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.shippingaddress.EditShippingAddressViewModel
import io.temco.guhada.databinding.DialogEditshippingaddressBinding

/**
 * 배송지 수정 액티비티
 * @author Hyeyeon Park
 */
class EditShippingAddressActivity : AppCompatActivity(), OnEditShippingAddressListener {
    private lateinit var mBinding: DialogEditshippingaddressBinding
    private lateinit var mViewModel: EditShippingAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.dialog_editshippingaddress)
        mViewModel = EditShippingAddressViewModel(this)

        val orderShippingAddress = intent.getSerializableExtra("orderShippingAddress")
        if (orderShippingAddress != null) {
            mViewModel.shippingAddress = orderShippingAddress as UserShipping
            mViewModel.shippingAddress.pId = intent.getLongExtra("purchaseId", -1)
            mViewModel.submitTask = { mViewModel.checkEmptyField { mViewModel.updateOrderShippingAddress() } }
        } else {
            mViewModel.shippingAddress = intent.getSerializableExtra("shippingAddress") as UserShipping
            mViewModel.submitTask = { mViewModel.checkEmptyField { mViewModel.updateShippingAddress() } }
        }

        mBinding.includeEditshippingaddress.addButtonVisible = intent.getBooleanExtra("addButtonVisible", false)
        mBinding.includeEditshippingaddress.shippingAddress = mViewModel.shippingAddress
        mBinding.includeEditshippingaddress.setOnClickZipListener {
            redirectSearchZipWebViewActivity()
        }

        mBinding.includeEditshippingaddress.setOnClickDefaultListener {
            mViewModel.shippingAddress.defaultAddress = !mViewModel.shippingAddress.defaultAddress
            mBinding.includeEditshippingaddress.shippingAddress = mViewModel.shippingAddress
            mBinding.executePendingBindings()
        }

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    override fun closeActivity(resultCode: Int, withExtra: Boolean) {
        if (withExtra)
            intent.putExtra("shippingAddress", mViewModel.shippingAddress)

        setResult(resultCode, intent)
        finish()
    }

    private fun redirectSearchZipWebViewActivity() {
        val intent = Intent(this, SearchZipWebViewActivity::class.java)
        startActivityForResult(intent, SEARCH_ZIP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_ZIP) {
            if (resultCode == Activity.RESULT_OK) {
                val address = data?.getStringExtra("address")
                val zip = data?.getStringExtra("zip")

                if (address == null || address.isEmpty() || zip == null || zip.isEmpty()) {
                    ToastUtil.showMessage("다시 시도해주세요.")
                } else {
                    mViewModel.shippingAddress.zip = zip
                    mViewModel.shippingAddress.address = address
                    mViewModel.shippingAddress.roadAddress = address
                    mBinding.includeEditshippingaddress.shippingAddress = mViewModel.shippingAddress
                    mBinding.includeEditshippingaddress.executePendingBindings()
                }
            }
        }
    }
}