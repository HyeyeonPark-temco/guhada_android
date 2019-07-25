package io.temco.guhada.view.custom.layout.mypage

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableInt
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.flag.RequestCode
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.mypage.MyPageAddressViewModel
import io.temco.guhada.databinding.CustomlayoutMypageAddressBinding
import io.temco.guhada.view.activity.AddShippingAddressActivity
import io.temco.guhada.view.activity.EditShippingAddressActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
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
) : BaseListLayout<CustomlayoutMypageAddressBinding, MyPageAddressViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener, OnShippingAddressListener {
    private lateinit var mShippingAddressListFragment: ShippingAddressListFragment
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_address

    @SuppressLint("CheckResult")
    override fun init() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPageAddressViewModel(context, this)
        setShippingAddressListFragment()

        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode) {
                RequestCode.EDIT_SHIPPING_ADDRESS.flag -> mViewModel.getUserShippingAddress()
                RequestCode.ADD_SHIPPING_ADDRESS.flag -> mViewModel.getUserShippingAddress()
            }
        }

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
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
        // 배송지 등록 완료 시, 여기로 들어어옴
        if (resultCode == RESULT_OK) {
            mViewModel.getUserShippingAddress()
        }
    }

    override fun notifyDeleteItem() {
        mShippingAddressListFragment.mListAdapter.deleteItem()
        mShippingAddressListFragment.mListAdapter.currentPos = -1

        if (mShippingAddressListFragment.mListAdapter.itemCount == 0) {
            mViewModel.emptyVisibility = ObservableInt(View.VISIBLE)
            mViewModel.notifyPropertyChanged(BR.emptyVisibility)
        }
    }

    override fun redirectEditShippingAddressActivity(shippingAddress: UserShipping) {
        Intent(context, EditShippingAddressActivity::class.java).let {
            it.putExtra("shippingAddress", shippingAddress)
            (context as AppCompatActivity).startActivityForResult(it, Flag.RequestCode.EDIT_SHIPPING_ADDRESS)
        }
    }

    override fun redirectSearchZipActivity() {
        // NONE
    }

    override fun redirectAddShippingAddressActivity() {
        Intent(context, AddShippingAddressActivity::class.java).let {
            (context as AppCompatActivity).startActivityForResult(it, Flag.RequestCode.ADD_SHIPPING_ADDRESS)
        }
    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }
}