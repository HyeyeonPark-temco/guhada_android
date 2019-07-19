package io.temco.guhada.view.fragment.shippingaddress

import android.content.Intent
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.data.viewmodel.ShippingAddressViewModel
import io.temco.guhada.databinding.FragmentAddshippingaddressBinding
import io.temco.guhada.view.activity.SearchZipWebViewActivity
import io.temco.guhada.view.fragment.base.BaseFragment

class AddShippingAddressFragment : BaseFragment<FragmentAddshippingaddressBinding>() {
    lateinit var mViewModel: ShippingAddressViewModel
    override fun getBaseTag(): String = AddShippingAddressFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_addshippingaddress

    override fun init() {
        if (::mViewModel.isInitialized) {
            mBinding.viewModel = mViewModel
            mBinding.includeAddshippingaddress.addButtonVisible = true
            mBinding.includeAddshippingaddress.shippingAddress = mViewModel.newItem
            mBinding.includeAddshippingaddress.setOnClickZipListener { mViewModel.redirectSearchZipActivity() }
            mBinding.includeAddshippingaddress.setOnClickAddListListener { mViewModel.newItem.addList = !mViewModel.newItem.addList }
            mBinding.includeAddshippingaddress.setOnClickDefaultListener { mViewModel.newItem.defaultAddress = !mViewModel.newItem.defaultAddress }
            mBinding.executePendingBindings()
        }
    }

    private fun redirectSearchZipActivity() {
        startActivityForResult(Intent(context, SearchZipWebViewActivity::class.java), Flag.RequestCode.SEARCH_ZIP)
    }

    fun updateSearchZipResult(zip: String, address: String) {
        if (zip.isNotEmpty() && address.isNotEmpty()) {
            mViewModel.newItem.zip = zip
            mViewModel.newItem.address = address
            mViewModel.newItem.roadAddress = address
            mBinding.includeAddshippingaddress.shippingAddress = mViewModel.newItem
            mBinding.executePendingBindings()
        }
    }
}