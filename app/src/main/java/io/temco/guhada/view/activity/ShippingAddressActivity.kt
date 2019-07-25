package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Flag.RequestCode.EDIT_SHIPPING_ADDRESS
import io.temco.guhada.common.Flag.RequestCode.SEARCH_ZIP
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.shippingaddress.ShippingAddressViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.base.BaseFragmentPagerAdapter
import io.temco.guhada.view.fragment.shippingaddress.ShippingAddressFormFragment
import io.temco.guhada.view.fragment.shippingaddress.ShippingAddressListFragment

/**
 * 배송지 변경 Activity
 * @author Hyeyeon Park
 */
class ShippingAddressActivity : BindActivity<io.temco.guhada.databinding.ActivityShippingaddressBinding>(), OnShippingAddressListener {
    private lateinit var mFragmentPagerAdapter: BaseFragmentPagerAdapter
    private lateinit var mViewModel: ShippingAddressViewModel
    private lateinit var mShippingAddressListFragment: ShippingAddressListFragment
    private lateinit var mShippingAddressFormFragment: ShippingAddressFormFragment

    override fun getBaseTag(): String = ShippingAddressActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_shippingaddress
    override fun getViewType(): Type.View = Type.View.SHIPPING_ADDRESS

    override fun init() {
        initHeader()
        initViewModel()
        initFragmentPager()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initHeader() {
        mBinding.includeShippingaddressHeader.title = getString(R.string.shippingaddress_title)
        mBinding.includeShippingaddressHeader.setOnClickBackButton {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun initViewModel() {
        mViewModel = ShippingAddressViewModel(this)

        // 배송지 있는 경우 Type Casting
        val shippingAddress = intent.getSerializableExtra("shippingAddress")
        if (shippingAddress != null) {
            mViewModel.prevSelectedItem = shippingAddress as UserShipping
            mViewModel.selectedItem = shippingAddress
        }
    }

    private fun initFragmentPager() {
        mFragmentPagerAdapter = BaseFragmentPagerAdapter(supportFragmentManager)
        mShippingAddressListFragment = ShippingAddressListFragment().apply { mViewModel = this@ShippingAddressActivity.mViewModel }
        mShippingAddressFormFragment = ShippingAddressFormFragment().apply { mViewModel = this@ShippingAddressActivity.mViewModel }
        mFragmentPagerAdapter.addFragment(mShippingAddressListFragment)
        mFragmentPagerAdapter.addFragment(mShippingAddressFormFragment)

        mBinding.viewpagerShippingaddress.adapter = mFragmentPagerAdapter
        mBinding.viewpagerShippingaddress.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mBinding.tablayoutShippingaddress))
        mBinding.tablayoutShippingaddress.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos = tab?.position ?: 0
                mViewModel.viewpagerPos = pos
                mBinding.viewpagerShippingaddress.currentItem = pos
            }
        })
    }

    override fun closeActivity(resultCode: Int, shippingAddress: UserShipping?) {
        if (resultCode == Activity.RESULT_OK) {
            intent.putExtra("shippingAddress", shippingAddress)
            setResult(resultCode, intent)
        } else
            setResult(resultCode)
        finish()
    }

    override fun notifyDeleteItem() = (mFragmentPagerAdapter.getItem(0) as ShippingAddressListFragment).mListAdapter.deleteItem()

    override fun redirectEditShippingAddressActivity(shippingAddress: UserShipping) {
        Intent(this@ShippingAddressActivity, EditShippingAddressActivity::class.java).let {
            it.putExtra("shippingAddress", shippingAddress)
            startActivityForResult(it, Flag.RequestCode.EDIT_SHIPPING_ADDRESS)
        }
    }

    override fun redirectSearchZipActivity() {
        startActivityForResult(Intent(this@ShippingAddressActivity, SearchZipWebViewActivity::class.java), Flag.RequestCode.SEARCH_ZIP)
    }

    override fun redirectAddShippingAddressActivity() {
        // NONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            var editedShippingAddress = data?.getSerializableExtra("shippingAddress")
//            if (editedShippingAddress != null) editedShippingAddress = editedShippingAddress as UserShipping

            when (requestCode) {
                EDIT_SHIPPING_ADDRESS -> {
                    val shippingAddress = data?.getSerializableExtra("shippingAddress")
                    if(shippingAddress != null){
                        mViewModel.selectedItem = shippingAddress as UserShipping
                    }
                    mShippingAddressListFragment.getShippingAddressList() // REFRESH
                }
                SEARCH_ZIP -> mShippingAddressFormFragment.updateSearchZipResult(data?.getStringExtra("zip")
                        ?: "", data?.getStringExtra("address") ?: "")
            }
        } else {

        }
    }
}