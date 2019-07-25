package io.temco.guhada.view.fragment.shippingaddress

import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.shippingaddress.ShippingAddressViewModel
import io.temco.guhada.databinding.FragmentShippingaddresslistBinding
import io.temco.guhada.view.adapter.ShippingAddressListAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class ShippingAddressListFragment : BaseFragment<FragmentShippingaddresslistBinding>() {
    lateinit var mViewModel: ShippingAddressViewModel
    lateinit var mListAdapter: ShippingAddressListAdapter
    private lateinit var mLoadingIndicator: LoadingIndicatorUtil

    override fun getBaseTag(): String = ShippingAddressListFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_shippingaddresslist

    override fun init() {
        if (::mViewModel.isInitialized) {
            if (context != null) mLoadingIndicator = LoadingIndicatorUtil(context!!)
            getShippingAddressList()

            mViewModel.shippingAddresses.observe(this, Observer {
                if (!::mListAdapter.isInitialized)
                    mListAdapter = ShippingAddressListAdapter(mViewModel)

                mListAdapter.setItems(it)
                if (mBinding.recyclerviewShippingaddress.adapter == null) {
                    mBinding.recyclerviewShippingaddress.adapter = mListAdapter
                } else {
                    (mBinding.recyclerviewShippingaddress.adapter as ShippingAddressListAdapter).setItems(it)
                }

                if (::mLoadingIndicator.isInitialized)
                    mLoadingIndicator.hide()
            })

            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mLoadingIndicator.isInitialized) mLoadingIndicator.hide()
    }

    override fun onDestroy() {
        if (::mLoadingIndicator.isInitialized)
            mLoadingIndicator.dismiss()
        if (::mViewModel.isInitialized)
            mViewModel.shippingAddresses.removeObservers(this)
        super.onDestroy()
    }

    fun getShippingAddressList() {
        if (::mLoadingIndicator.isInitialized)
            mLoadingIndicator.show()
        if (::mViewModel.isInitialized)
            mViewModel.getUserShippingAddress()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bindShippingAddress")
        fun RecyclerView.bindShippingAddresses(list: MutableList<UserShipping>?) {
            if (list != null) {
                (this.adapter as ShippingAddressListAdapter).setItems(list)
            }
        }
    }
}