package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.viewmodel.mypage.MyPageFollowViewModel
import io.temco.guhada.databinding.CustomlayoutMypageFollowBinding
import io.temco.guhada.view.adapter.mypage.MyPageFollowAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 팔로우한 스토어 화면
 *
 */
class MyPageFollowLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageFollowBinding, MyPageFollowViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener {
    private val mAdapter = MyPageFollowAdapter()

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_follow
    override fun init() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPageFollowViewModel(context)
        mViewModel.getFollowingSellerIds()

        mViewModel.mSeller.observe(this, Observer {
            (mBinding.recyclerviewMypagefollowList.adapter as MyPageFollowAdapter).addItem(it)
            mBinding.textviewMypagefollowTotalcount.text = "총 ${mViewModel.mSellerList.value?.size}개"
        })

        mBinding.recyclerviewMypagefollowList.adapter = mAdapter
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false

        mViewModel.page = 1
    }


    override fun onFocusView() {

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

    companion object {

        @BindingAdapter("followingSeller")
        @JvmStatic
        fun RecyclerView.bindFollowingSeller(list: MutableList<Seller>?) {
            if (list != null) {
                if (this.adapter != null) (this.adapter as MyPageFollowAdapter).setItems(list)
                else this.adapter = MyPageFollowAdapter().apply { mList = list }
            }
        }

    }
}