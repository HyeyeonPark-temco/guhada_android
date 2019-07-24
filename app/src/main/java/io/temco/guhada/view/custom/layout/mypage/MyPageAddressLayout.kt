package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.mypage.MyPageAddressViewModel
import io.temco.guhada.databinding.CustomlayoutMypageAddressBinding
import io.temco.guhada.view.activity.EditShippingAddressActivity
import io.temco.guhada.view.custom.layout.common.BaseConstraintLayout
import io.temco.guhada.view.fragment.shippingaddress.ShippingAddressListFragment

/**
 * created 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 배송지관리 화면
 * @author Hyeyeon Park
 * @since 2019.07.24
 */
class MyPageAddressLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseConstraintLayout<CustomlayoutMypageAddressBinding, MyPageAddressViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener, OnShippingAddressListener {
    private lateinit var mShippingAddressListFragment: ShippingAddressListFragment
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_address
    override fun init() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPageAddressViewModel(context, this)
        setShippingAddressListFragment()
    }

    override fun onRefresh() {
        mViewModel.getUserShippingAddress()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setShippingAddressListFragment() {
        mShippingAddressListFragment = ShippingAddressListFragment().apply { this.mViewModel = this@MyPageAddressLayout.mViewModel }
        (context as AppCompatActivity).supportFragmentManager.beginTransaction().add(mBinding.framelayoutMypageAddress.id, mShippingAddressListFragment).commitAllowingStateLoss()
    }

    // OnShippingAddressListener
    override fun closeActivity(resultCode: Int, shippingAddress: UserShipping?) {
        // NONE
    }

    override fun notifyDeleteItem() = mShippingAddressListFragment.mListAdapter.deleteItem()

    override fun redirectEditShippingAddressActivity(shippingAddress: UserShipping) {
        Intent(context, EditShippingAddressActivity::class.java).let {
            it.putExtra("shippingAddress", shippingAddress)
            (context as AppCompatActivity).startActivityForResult(it, Flag.RequestCode.EDIT_SHIPPING_ADDRESS)
        }
    }

    override fun redirectSearchZipActivity() {
        // NONE
    }


}