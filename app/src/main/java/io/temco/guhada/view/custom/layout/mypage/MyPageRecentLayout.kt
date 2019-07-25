package io.temco.guhada.view.custom.layout.mypage

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.mypage.MyPageRecentViewModel
import io.temco.guhada.databinding.CustomlayoutMypageRecentBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import kotlinx.android.synthetic.main.customlayout_mypage_recent.view.*

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 최근본상품 화면
 *
 */
class MyPageRecentLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageRecentBinding, MyPageRecentViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_recent
    override fun init() {
        mViewModel = MyPageRecentViewModel(context)
        mBinding.viewModel = mViewModel
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "MyPageRecentViewModel ", "init -----")

        recyclerview_mypagerecentlayout_list.layoutManager = WrapGridLayoutManager(context as Activity, 2, LinearLayoutManager.VERTICAL, false)
        recyclerview_mypagerecentlayout_list.setHasFixedSize(true)

        (recyclerview_mypagerecentlayout_list.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (recyclerview_mypagerecentlayout_list.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true

        mViewModel.listData.observe(this,
                androidx.lifecycle.Observer<ArrayList<Product>> {
                    if (CustomLog.flag) CustomLog.L("MyPageRecentLayout LIFECYCLE", "onViewCreated listData.size 1----------------", it.toString())
                    mViewModel.getListAdapter().notifyDataSetChanged()
                    if (CustomLog.flag) CustomLog.L("MyPageRecentLayout LIFECYCLE", "onViewCreated listData.size 1----------------", mViewModel.getListAdapter().items.size)
                }
        )
    }

    override fun onDestroy() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onDestroy ", "init -----")
        mBinding.viewModel?.destroyModel()
    }
}