package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
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
        mViewModel = MyPageFollowViewModel(context)
        mViewModel.mFollowList.observe(this, Observer {
            Observable.fromIterable(it)
                    .map {
                        Seller().apply { id = it.targetId }
                    }.subscribe { seller ->
                        mViewModel.mSellerList.add(seller)
                    }

            mBinding.textviewMypagefollowTotalcount.text = Html.fromHtml(resources.getString(R.string.mypagefollow_totalcount, it.size))
            mBinding.recyclerviewMypagefollowList.adapter = MyPageFollowAdapter().apply {
                this.mList = this@MyPageFollowLayout.mViewModel.mSellerList
                this.mViewModel = this@MyPageFollowLayout.mViewModel
            }
        })

        mViewModel.mNotifyDataChangedTask = {
            val adapter = (mBinding.recyclerviewMypagefollowList.adapter as MyPageFollowAdapter)
            adapter.setItems(mViewModel.mSellerList)
        }

        mViewModel.mNotifyItemInsertedTask = { startPos, endPos ->
            mBinding.recyclerviewMypagefollowList.recycledViewPool.clear()
            val adapter = (mBinding.recyclerviewMypagefollowList.adapter as MyPageFollowAdapter)
            adapter.addAllItems(mViewModel.mTempSellerList, startPos, endPos)
            mViewModel.mTempSellerList = mutableListOf()
        }

        mViewModel.getFollowingSellerIds()
    }

    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false
        mViewModel.mSellerList.clear()
        mViewModel.getFollowingSellerIds()
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

}