package io.temco.guhada.view.fragment.cart

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.cart.EmptyCartViewModel
import io.temco.guhada.databinding.FragmentEmptycartBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class EmptyCartFragment : BaseFragment<FragmentEmptycartBinding>() {
    override fun getBaseTag(): String = EmptyCartFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_emptycart

    override fun init() {
        mBinding.viewModel = EmptyCartViewModel()
        mBinding.executePendingBindings()
    }
}