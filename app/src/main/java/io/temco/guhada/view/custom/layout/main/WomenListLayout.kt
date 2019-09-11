package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.db.entity.CategoryLabelType
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.viewmodel.main.WomenListViewModel
import io.temco.guhada.databinding.CustomlayoutMainWomenlistBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.main.SubTitleListAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout

class WomenListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainWomenlistBinding, WomenListViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = WomenListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_womenlist
    override fun init() {
        mViewModel = WomenListViewModel(context)
        mBinding.viewModel = mViewModel
        if(CustomLog.flag) CustomLog.L("HomeListRepository","HomeListLayout ", "init -----")

        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = WrapGridLayoutManager(context as Activity, 2, LinearLayoutManager.VERTICAL, false)

        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true
        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return mViewModel.listData.value!![position].gridSpanCount
            }
        }

        mViewModel.listData.observe(this,
                androidx.lifecycle.Observer<ArrayList<MainBaseModel>> {
                    //if (CustomLog.flag) CustomLog.L("HomeListLayout LIFECYCLE", "onViewCreated listData.size 1----------------",it.size)
                    mViewModel.getListAdapter().notifyDataSetChanged()
                    //if (CustomLog.flag) CustomLog.L("HomeListLayout LIFECYCLE", "onViewCreated listData.size 2----------------",mViewModel.getListAdapter().items.size)
                }
        )
        getCategory()
    }

    private fun getCategory(){
        var db = GuhadaDB.getInstance(context = context)!!
        (context as MainActivity).getmDisposable().add(Observable.fromCallable<List<CategoryEntity>> {
            db.categoryDao().getDepthAll(CategoryLabelType.Women.name,2)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(!it.isNullOrEmpty()){
                        mViewModel.categoryList = it.toMutableList()
                        if (mBinding.recyclerviewWomenlist.adapter == null) {
                            mBinding.recyclerviewWomenlist.adapter = SubTitleListAdapter().apply { mList = mViewModel.categoryList!! }
                            (mBinding.recyclerviewWomenlist.adapter as SubTitleListAdapter).mClickSelectItemListener = object : OnClickSelectItemListener{
                                override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
                                    if(CustomLog.flag)CustomLog.L("WomenListLayout","value", value as CategoryEntity)
                                    var hierarchy = (value as CategoryEntity).hierarchy.split(",")
                                    var array = arrayListOf<Int>()
                                    for (i in hierarchy) array.add(i.toInt())
                                    CommonUtil.startCategoryScreen(context as MainActivity, Type.Category.NORMAL, array.toIntArray(), false)
                                }
                            }
                            mBinding.executePendingBindings()
                        } else {
                            (mBinding.recyclerviewWomenlist.adapter as SubTitleListAdapter).setItems(mViewModel.categoryList!!)
                        }
                        for (i in it){
                            if(CustomLog.flag)CustomLog.L("WomenListLayout",i.toString())
                        }
                    }
                }
        )
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