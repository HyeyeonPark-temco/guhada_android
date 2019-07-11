package io.temco.guhada.view.fragment.shippingaddress

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.ShippingAddressViewModel
import io.temco.guhada.databinding.FragmentAddshippingaddressBinding
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
            mBinding.includeAddshippingaddress.setOnClickAddListListener { mViewModel.newItem.addList = !mViewModel.newItem.addList }
            mBinding.includeAddshippingaddress.setOnClickDefaultListener { mViewModel.newItem.defaultAddress = !mViewModel.newItem.defaultAddress }
            mBinding.executePendingBindings()
        }
    }
}