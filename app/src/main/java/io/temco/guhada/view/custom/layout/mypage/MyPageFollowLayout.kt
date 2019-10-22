package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageFollowViewModel
import io.temco.guhada.databinding.CustomlayoutMypageFollowBinding
import io.temco.guhada.view.adapter.mypage.MyPageStoreFollowAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 팔로우한 스토어 화면
 * @author Hyeyeon Park
 * @since 2019.08.26
 */
class MyPageFollowLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageFollowBinding, MyPageFollowViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener {
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_follow
    override fun init() {
        initViewModel()

        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = MyPageFollowViewModel()
        mViewModel.mFollowStore.observe(this, Observer {
            mBinding.textviewMypagefollowTotalcount.text = Html.fromHtml(resources.getString(R.string.mypagefollow_totalcount, it.size))
            mBinding.recyclerviewMypagefollowList.adapter = MyPageStoreFollowAdapter().apply {
                this.mList = it
                this.mViewModel = this@MyPageFollowLayout.mViewModel
            }
        })

        mViewModel.mNotifyDataChangedTask = {
            val adapter = (mBinding.recyclerviewMypagefollowList.adapter as MyPageStoreFollowAdapter)
            adapter.setItems(mViewModel.mFollowStore.value?: mutableListOf())
        }

        mViewModel.mNotifyItemInsertedTask = { startPos, endPos ->
            mBinding.recyclerviewMypagefollowList.recycledViewPool.clear()
            val adapter = (mBinding.recyclerviewMypagefollowList.adapter as MyPageStoreFollowAdapter)
            adapter.addAllItems(mViewModel.mFollowStore.value?: mutableListOf(), startPos, endPos)
            mViewModel.mTempSellerList = mutableListOf()
        }

        mViewModel.getFollowingStores()
    }

    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false
        mViewModel.getFollowingStores()
        mViewModel.mFollowStore.value?.clear()
    }


    override fun onFocusView() { }
    override fun onReleaseView() { }
    override fun onStart() { }
    override fun onResume() { }
    override fun onPause() { }
    override fun onStop() { }
    override fun onDestroy() { }

}