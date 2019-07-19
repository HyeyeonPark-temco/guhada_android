package io.temco.guhada.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnEditShippingAddressListener
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.EditShippingAddressViewModel
import io.temco.guhada.databinding.DialogEditshippingaddressBinding

class EditShippingAddressActivity : AppCompatActivity(), OnEditShippingAddressListener {
    private lateinit var mBinding: DialogEditshippingaddressBinding
    private lateinit var mViewModel: EditShippingAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this@EditShippingAddressActivity.requestWindowFeature(Window.FEATURE_NO_TITLE)

        mBinding = DataBindingUtil.setContentView(this, R.layout.dialog_editshippingaddress)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mViewModel = EditShippingAddressViewModel(this)
        mViewModel.shippingAddress = intent.getSerializableExtra("shippingAddress") as UserShipping

        mBinding.includeEditshippingaddress.addButtonVisible = false
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
//        if(withExtra)
//            intent.putExtra("shippingAddress", mViewModel.shippingAddress)

        setResult(resultCode, intent)
        finish()
    }

    private fun redirectSearchZipWebViewActivity() {
        val intent = Intent(this, SearchZipWebViewActivity::class.java)
        startActivityForResult(intent, Flag.RequestCode.SEARCH_ZIP)
    }
}