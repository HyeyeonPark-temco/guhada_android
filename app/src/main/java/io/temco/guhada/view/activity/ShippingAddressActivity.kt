package io.temco.guhada.view.activity

import com.auth0.android.jwt.JWT
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.ShippingAddressViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.base.BaseFragmentPagerAdpter
import io.temco.guhada.view.fragment.shippingaddress.ShippingAddressListFragment

/**
 * 배송지 변경 Activity
 */
class ShippingAddressActivity : BindActivity<io.temco.guhada.databinding.ActivityShippingaddressBinding>(), OnShippingAddressListener {
    private lateinit var mFragmentPagerAdapter: BaseFragmentPagerAdpter
    private lateinit var mViewModel: ShippingAddressViewModel

    override fun getBaseTag(): String = ShippingAddressActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_shippingaddress

    override fun getViewType(): Type.View = Type.View.SHIPPING_ADDRESS

    override fun init() {
        mViewModel = ShippingAddressViewModel(this)
        mViewModel.userId = JWT(Preferences.getToken().accessToken).getClaim("userId").asInt() ?: 0
        mViewModel.selectedItem = intent.getSerializableExtra("shippingAddress") as UserShipping

        BaseFragmentPagerAdpter(supportFragmentManager).let { baseFragmentPagerAdapter ->
            mFragmentPagerAdapter = baseFragmentPagerAdapter
            mFragmentPagerAdapter.addFragment(ShippingAddressListFragment().apply { mViewModel = this@ShippingAddressActivity.mViewModel })
            mBinding.viewpagerShippingaddress.adapter = mFragmentPagerAdapter
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    override fun closeActivity(resultCode: Int, withExtra: Boolean) {
        if (withExtra) {
            intent.putExtra("shippingAddress", mViewModel.selectedItem)
            setResult(resultCode, intent)
        } else
            setResult(resultCode)
        finish()
    }

    override fun notifyDeleteItem() = (mFragmentPagerAdapter.getItem(0) as ShippingAddressListFragment).mListAdapter.deleteItem()
}