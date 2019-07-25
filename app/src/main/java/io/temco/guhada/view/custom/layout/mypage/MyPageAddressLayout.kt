package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageAddressViewModel
import io.temco.guhada.databinding.CustomlayoutMypageAddressBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 배송지관리 화면
 *
 */

class MyPageAddressLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageAddressBinding, MyPageAddressViewModel>(context, attrs, defStyleAttr),SwipeRefreshLayout.OnRefreshListener {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_address
    override fun init() {

    }


    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroy() {

    }
}