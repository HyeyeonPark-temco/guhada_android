package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.*
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.db.entity.CategoryLabelType
import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.main.KidsListViewModel
import io.temco.guhada.databinding.CustomlayoutMainKidslistBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.main.SubTitleListAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.main.HomeFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType


class KidsListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainKidslistBinding,KidsListViewModel>(context, attrs, defStyleAttr) , OnMainCustomLayoutListener {
    var mHomeFragment: HomeFragment? = null

    private val INTERPOLATOR = FastOutSlowInInterpolator() // Button Animation
    private var recentViewCount = -1

    lateinit var mainListListener : OnMainListListener

    override fun getBaseTag() = KidsListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_kidslist
    override fun init() {
        mViewModel = KidsListViewModel(context)
        mBinding.viewModel = mViewModel
        if(CustomLog.flag) CustomLog.L("KidsListLayout","HomeListLayout", "init -----")

        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = WrapGridLayoutManager(context as Activity, 2, LinearLayoutManager.VERTICAL, false)

        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true
        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return mViewModel.listData[position].gridSpanCount
            }
        }

        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Top
                    changeFloatingButtonLayout(false)
                } else if (!recyclerView.canScrollVertically(1)) {
                    // Bottom
                } else {
                    // Idle
                    changeFloatingButtonLayout(true)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) { }
        })
        mBinding.buttonFloatingItem.layoutFloatingButtonBadge.setOnClickListener { view ->
            (context as MainActivity).mBinding.layoutContents.layoutPager.currentItem = 4
            (context as MainActivity).selectTab(4, false,true)
            EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.MYPAGE_MOVE,MyPageTabType.LAST_VIEW.ordinal))
        }

        getRecentProductCount()
        getCategoryFilter() // 서버에서 카테고리 목록 가져옴
        //getCategory() // 디비에서 카테고리 목록 가져옴
    }



    private fun scrollToTop(isSmooth: Boolean) {
        if (isSmooth) {
            mBinding.recyclerView.smoothScrollToPosition(0)
        } else {
            mBinding.recyclerView.scrollToPosition(0)
        }
        changeTopFloatingButton(false)
        mainListListener.requestDataList(3,"scrollToTop")
    }

    // Floating Button
    private fun changeFloatingButtonLayout(isShow: Boolean) {
        changeTopFloatingButton(isShow)
        changeItemFloatingButton(isShow)
    }

    private fun changeItemFloatingButton(isShow: Boolean) {
        changeItemFloatingButton(isShow, false)
    }

    private fun changeTopFloatingButton(isShow: Boolean) {
        changeTopFloatingButton(isShow, false)
    }

    private fun changeItemFloatingButton(isShow: Boolean, animate: Boolean) {
        if (CommonUtil.checkToken()) {
            if(recentViewCount > 0){
                changeLastView(mBinding.buttonFloatingItem.root, isShow, animate)
            }
        }else{
            changeLastView(mBinding.buttonFloatingItem.root, false, false)
        }
    }

    private fun changeTopFloatingButton(isShow: Boolean, animate: Boolean) {
        changeScaleView(mBinding.buttonFloatingTop.root, isShow, animate)
    }

    /**
     * @editor park jungho
     * 19.08.01
     * scrollToTop Value -> false 로 변경
     */
    private fun changeScaleView(v: View, isShow: Boolean, animate: Boolean) {
        if (isShow) {
            if (v.visibility != View.VISIBLE) {
                v.setOnClickListener{
                    try{  mHomeFragment?.getmBinding()?.layoutAppbar?.setExpanded(true) }catch (e : Exception){
                        if(CustomLog.flag)CustomLog.E(e)
                    }
                    scrollToTop(false)
                }
                v.visibility = View.VISIBLE
                if (animate) {
                    showScaleAnimation(v)
                }
            }
        } else {
            if (v.visibility == View.VISIBLE) {
                v.setOnClickListener(null)
                if (animate) {
                    hideScaleAnimation(v)
                } else {
                    v.visibility = View.GONE
                }
            }
        }
    }


    /**
     * @editor park jungho
     * 19.08.01
     * scrollToTop Value -> false 로 변경
     */
    private fun changeLastView(v: View, isShow: Boolean, animate: Boolean) {
        if (isShow) {
            if (v.visibility != View.VISIBLE) {
                v.visibility = View.VISIBLE
                if (animate) {
                    showScaleAnimation(v)
                }
            }
        } else {
            if (v.visibility == View.VISIBLE) {
                if (animate) {
                    hideScaleAnimation(v)
                } else {
                    v.visibility = View.GONE
                }
            }
        }
    }

    private fun setRecentProductCount() {
        try {
            if (!CommonUtil.checkToken()) return
            CommonUtilKotlin.recentProductCount((context as MainActivity).getmDisposable(), (context as MainActivity).getmDb(), object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    try {
                        val count = value.toString()
                        mBinding.buttonFloatingItem.textviewFloatingCount.text = count
                        if(mBinding.buttonFloatingItem.textviewFloatingCount.text.toString().toInt() == 0){
                            changeLastView(mBinding.buttonFloatingItem.root, false, false)
                        }else{
                            changeLastView(mBinding.buttonFloatingItem.root, true, false)
                        }
                    } catch (e: Exception) {
                        changeLastView(mBinding.buttonFloatingItem.root, false, false)
                        if (CustomLog.flag) CustomLog.E(e)
                    }
                }
            })
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }

    private fun getRecentProductCount() {
        try {
            if (!CommonUtil.checkToken()) return
            CommonUtilKotlin.recentProductCount((context as MainActivity).getmDisposable(), (context as MainActivity).getmDb(), object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    try {
                        recentViewCount = value.toString().toInt()
                        mBinding.buttonFloatingItem.textviewFloatingCount.text = value.toString()
                    } catch (e: Exception) {
                        if (CustomLog.flag) CustomLog.E(e)
                    }
                }
            })
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }

    private fun showScaleAnimation(v: View) {
        ViewCompat.animate(v)
                .scaleX(1.0f).scaleY(1.0f).alpha(1.0f)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(null)
                .start()
    }

    private fun hideScaleAnimation(v: View) {
        ViewCompat.animate(v)
                .scaleX(0.0f).scaleY(0.0f).alpha(0.0f)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {}
                    override fun onAnimationCancel(view: View) {}
                    override fun onAnimationEnd(view: View) {
                        view.visibility = View.GONE
                    }
                })
                .start()
    }

    private fun getCategory(){
        var db = GuhadaDB.getInstance(context = context)!!
        (context as MainActivity).getmDisposable().add(Observable.fromCallable<List<CategoryEntity>> {
            db.categoryDao().getDepthUnder(CategoryLabelType.Kids.name,1)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(!it.isNullOrEmpty()){
                        mViewModel.categoryList = it.toMutableList()
                        mViewModel.categoryList!![0].title = "전체보기"
                        if (mBinding.recyclerviewKidslist.adapter == null) {
                            mBinding.recyclerviewKidslist.adapter = SubTitleListAdapter().apply { mList = mViewModel.categoryList!! }
                            (mBinding.recyclerviewKidslist.adapter as SubTitleListAdapter).mClickSelectItemListener = object : OnClickSelectItemListener {
                                override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
                                    var hierarchy = (value as CategoryEntity).hierarchy.split(",")
                                    var array = arrayListOf<Int>()
                                    for (i in hierarchy) array.add(i.toInt())
                                    CommonUtil.startCategoryScreen(context as MainActivity, Type.Category.NORMAL, array.toIntArray(), false)
                                }
                            }
                            mBinding.executePendingBindings()
                        } else {
                            (mBinding.recyclerviewKidslist.adapter as SubTitleListAdapter).setItems(mViewModel.categoryList!!)
                        }
                    }
                }
        )
    }

    private fun getCategoryFilter(){
        var filterBody = FilterBody()
        filterBody.categoryIds.add(3)
        SearchServer.getProductListByCategoryFilterMain(Type.ProductOrder.NEW_PRODUCT, filterBody, 1, OnServerListener{ success,o ->
            try{
                if(success){
                    var list = o as ProductList
                    if(!list.categories[0].children.isNullOrEmpty()){
                        var cTabList = mutableListOf<CategoryEntity>()
                        cTabList.add(CategoryEntity(list.categories[0], "", 0,list.categories[0].hierarchies[list.categories[0].hierarchies.size-1]))
                        for (category in list.categories[0].children){
                            cTabList.add(CategoryEntity(category, "", 0,category.hierarchies[category.hierarchies.size-1]))
                        }
                        mViewModel.categoryList = cTabList
                        mViewModel.categoryList!![0].title = "전체보기"
                        if (mBinding.recyclerviewKidslist.adapter == null) {
                            mBinding.recyclerviewKidslist.adapter = SubTitleListAdapter().apply { mList = mViewModel.categoryList!! }
                            (mBinding.recyclerviewKidslist.adapter as SubTitleListAdapter).mClickSelectItemListener = object : OnClickSelectItemListener {
                                override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
                                    var hierarchy = (value as CategoryEntity).hierarchy.split(",")
                                    var array = arrayListOf<Int>()
                                    for (i in hierarchy) array.add(i.toInt())
                                    CommonUtil.startCategoryScreen(context as MainActivity, Type.Category.NORMAL, array.toIntArray(), false)
                                }
                            }
                            mBinding.executePendingBindings()
                        } else {
                            (mBinding.recyclerviewKidslist.adapter as SubTitleListAdapter).setItems(mViewModel.categoryList!!)
                        }
                    }
                }else{
                    getCategory()
                }
            }catch (e : Exception){
                if(CustomLog.flag)CustomLog.E(e)
                getCategory()
            }
        })
    }


    fun setData(premiumData : HomeDeal, bestData : HomeDeal){
        mViewModel.premiumData = premiumData
        mViewModel.bestData = bestData
        mViewModel.setListData()
    }

    fun listScrollTop() {
        try{  mHomeFragment?.getmBinding()?.layoutAppbar?.setExpanded(true) }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        scrollToTop(false)
    }

    override fun updateDataList(tabIndex: Int, type: String) {
        if(CustomLog.flag)CustomLog.L("KidsListLayout","updateDataList","tabIndex","type",type)
    }

    override fun loadNewInDataList(tabIndex: Int, value: HomeDeal) {
        if(CustomLog.flag)CustomLog.L("KidsListLayout","loadNewInDataList","tabIndex")
        mViewModel.newInData = value
        mViewModel.setNewInData()
        if (CustomLog.flag) CustomLog.L("KidsListLayout", "newInData kidsList size", mViewModel.newInData?.kidsList!!.size)
        if (CustomLog.flag) CustomLog.L("KidsListLayout", "newInData menList size", mViewModel.newInData?.menList!!.size)
        if (CustomLog.flag) CustomLog.L("KidsListLayout", "newInData womenList size", mViewModel.newInData?.womenList!!.size)
        if (CustomLog.flag) CustomLog.L("KidsListLayout", "newInData allList size", mViewModel.newInData?.allList!!.size)
    }

    override fun onFocusView() {  }
    override fun onReleaseView() { }
    override fun onStart() { }
    override fun onResume() { setRecentProductCount() }
    override fun onPause() { }
    override fun onStop() { }
    override fun onDestroy() { }

    fun getViewModel() = mViewModel

}