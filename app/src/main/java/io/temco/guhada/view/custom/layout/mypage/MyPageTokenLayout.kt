package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageTokenViewModel
import io.temco.guhada.databinding.CustomlayoutMypageTokenBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.11.27
 * @author park jungho
 *
 * 마이페이지 - 구하다 토큰 화면
 *
 * @author Hyeyeon Park
 * @since 2019.11.27
 *
 */
class MyPageTokenLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageTokenBinding, MyPageTokenViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener {
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_token
    override fun init() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPageTokenViewModel()
        mBinding.viewModel = mViewModel

        mBinding.executePendingBindings()
    }

    override fun onRefresh() {

        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onFocusView() { }
    override fun onReleaseView() { }
    override fun onStart() { }
    override fun onResume() { }
    override fun onPause() { }
    override fun onStop() { }
    override fun onDestroy() { }

}