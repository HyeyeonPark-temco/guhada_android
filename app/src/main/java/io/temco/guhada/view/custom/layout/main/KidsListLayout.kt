package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
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
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.db.entity.CategoryLabelType
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.viewmodel.main.KidsListViewModel
import io.temco.guhada.databinding.CustomlayoutMainKidslistBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.main.SubTitleListAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.mypage.MyPageTabType

class KidsListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainKidslistBinding,KidsListViewModel>(context, attrs, defStyleAttr) {

    private val INTERPOLATOR = FastOutSlowInInterpolator() // Button Animation

    override fun getBaseTag() = KidsListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_kidslist
    override fun init() {
        mViewModel = KidsListViewModel(context)
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

        mViewModel.listData.observe(this,
                androidx.lifecycle.Observer<ArrayList<MainBaseModel>> {
                    //if (CustomLog.flag) CustomLog.L("HomeListLayout LIFECYCLE", "onViewCreated listData.size 1----------------",it.size)
                    mViewModel.getListAdapter().notifyDataSetChanged()
                    //if (CustomLog.flag) CustomLog.L("HomeListLayout LIFECYCLE", "onViewCreated listData.size 2----------------",mViewModel.getListAdapter().items.size)
                }
        )
        getCategory()
    }



    private fun scrollToTop(isSmooth: Boolean) {
        if (isSmooth) {
            mBinding.recyclerView.smoothScrollToPosition(0)
        } else {
            mBinding.recyclerView.scrollToPosition(0)
        }
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
            changeLastView(mBinding.buttonFloatingItem.root, isShow, animate)
        } else {
            mBinding.buttonFloatingItem.root.visibility = View.GONE
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
                v.setOnClickListener { view -> scrollToTop(false) }
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
                v.setOnClickListener { view ->
                    (context as MainActivity).mBinding.layoutContents.layoutPager.currentItem = 4
                    (context as MainActivity).selectTab(4, false)
                    EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.MYPAGE_MOVE, MyPageTabType.LAST_VIEW.ordinal))
                }
                v.visibility = View.VISIBLE
                if (animate) {
                    showScaleAnimation(v)
                }
                setRecentProductCount()
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

    private fun setRecentProductCount() {
        try {
            CommonUtilKotlin.recentProductCount((context as MainActivity).getmDisposable(), (context as MainActivity).getmDb(), object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    try {
                        val count = value.toString()
                        mBinding.buttonFloatingItem.textviewFloatingCount.text = count
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
            db.categoryDao().getDepthAll(CategoryLabelType.Kids.name,2)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(!it.isNullOrEmpty()){
                        mViewModel.categoryList = it.toMutableList()
                        if (mBinding.recyclerviewKidslist.adapter == null) {
                            mBinding.recyclerviewKidslist.adapter = SubTitleListAdapter().apply { mList = mViewModel.categoryList!! }
                            (mBinding.recyclerviewKidslist.adapter as SubTitleListAdapter).mClickSelectItemListener = object : OnClickSelectItemListener {
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
                            (mBinding.recyclerviewKidslist.adapter as SubTitleListAdapter).setItems(mViewModel.categoryList!!)
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