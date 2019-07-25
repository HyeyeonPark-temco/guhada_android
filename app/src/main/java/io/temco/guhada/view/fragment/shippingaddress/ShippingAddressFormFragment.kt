package io.temco.guhada.view.fragment.shippingaddress

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.shippingaddress.ShippingAddressViewModel
import io.temco.guhada.databinding.LayoutShippingaddressBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class ShippingAddressFormFragment : BaseFragment<LayoutShippingaddressBinding>() {
    lateinit var mViewModel: ShippingAddressViewModel
    var addButtonVisible: Boolean = false

    override fun getBaseTag(): String = ShippingAddressFormFragment::class.java.simpleName
    //    override fun getLayoutId(): Int = R.layout.fragment_addshippingaddress
    override fun getLayoutId(): Int = R.layout.layout_shippingaddress

    override fun init() {
        if (::mViewModel.isInitialized) {
          //  mBinding.viewModel = mViewModel
            mBinding.addButtonVisible = addButtonVisible
            mBinding.shippingAddress = mViewModel.newItem
            mBinding.setOnClickZipListener { mViewModel.redirectSearchZipActivity() }
            mBinding.setOnClickAddListListener { mViewModel.newItem.addList = !mViewModel.newItem.addList }
            mBinding.setOnClickDefaultListener { mViewModel.newItem.defaultAddress = !mViewModel.newItem.defaultAddress }
//            mBinding.includeAddshippingaddress.addButtonVisible = addButtonVisible
//            mBinding.includeAddshippingaddress.shippingAddress = mViewModel.newItem
//            mBinding.includeAddshippingaddress.setOnClickZipListener { mViewModel.redirectSearchZipActivity() }
//            mBinding.includeAddshippingaddress.setOnClickAddListListener { mViewModel.newItem.addList = !mViewModel.newItem.addList }
//            mBinding.includeAddshippingaddress.setOnClickDefaultListener { mViewModel.newItem.defaultAddress = !mViewModel.newItem.defaultAddress }
            mBinding.executePendingBindings()
        }
    }

    fun updateSearchZipResult(zip: String, address: String) {
        if (zip.isNotEmpty() && address.isNotEmpty()) {
            mViewModel.newItem.zip = zip
            mViewModel.newItem.address = address
            mViewModel.newItem.roadAddress = address
//            mBinding.includeAddshippingaddress.shippingAddress = mViewModel.newItem
            mBinding.shippingAddress = mViewModel.newItem
            mBinding.executePendingBindings()
        }
    }
}