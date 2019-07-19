package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.viewmodel.HomeListViewModel
import io.temco.guhada.databinding.CustomlayoutMainHomelistBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import kotlinx.android.synthetic.main.customlayout_main_homelist.view.*



class HomeListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainHomelistBinding, HomeListViewModel>(context, attrs, defStyleAttr){

    override fun getBaseTag() = HomeListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_homelist
    override fun init() {
        mViewModel = HomeListViewModel(context)
        mBinding.viewModel = mViewModel
        if(CustomLog.flag) CustomLog.L("HomeListRepository","HomeListLayout ", "init -----")

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = WrapGridLayoutManager(context as Activity, 2,LinearLayoutManager.VERTICAL, false)

        (recyclerView.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (recyclerView.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true
        (recyclerView.layoutManager as WrapGridLayoutManager).setSpanSizeLookup(
                object : GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return mViewModel.listData.value!![position].gridSpanCount
                    }
                }
        )

        mViewModel.listData.observe(this,
                androidx.lifecycle.Observer<ArrayList<MainBaseModel>> {
                    if (CustomLog.flag) CustomLog.L("HomeListLayout LIFECYCLE", "onViewCreated listData.size 1----------------",it.size)
                    mViewModel.getListAdapter().notifyDataSetChanged()
                }
        )
    }


}