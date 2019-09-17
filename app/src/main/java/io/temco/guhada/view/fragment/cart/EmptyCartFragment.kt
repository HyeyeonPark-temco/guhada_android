package io.temco.guhada.view.fragment.cart

import android.app.Activity
import io.temco.guhada.R
import io.temco.guhada.common.ActivityMoveToMain
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.data.viewmodel.cart.EmptyCartViewModel
import io.temco.guhada.databinding.FragmentEmptycartBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class EmptyCartFragment : BaseFragment<FragmentEmptycartBinding>() {
    override fun getBaseTag(): String = EmptyCartFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_emptycart

    override fun init() {
        mBinding.viewModel = EmptyCartViewModel()
        mBinding.executePendingBindings()

        mBinding.setOnClickContinue {
            BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true)
            (context as Activity).setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
            (context as Activity).onBackPressed()
        }

    }
}