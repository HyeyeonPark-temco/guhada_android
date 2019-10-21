package io.temco.guhada.view.fragment.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.databinding.FragmentMainHomeBinding
import io.temco.guhada.view.activity.CustomDialogActivity
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.custom.layout.main.*
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.viewpager.CustomViewPagerAdapter
import java.lang.Exception
import java.util.*


class HomeFragment : BaseFragment<FragmentMainHomeBinding>(), View.OnClickListener {

    // -------- LOCAL VALUE --------
    private var viewPagerAdapter : CustomViewPagerAdapter<String>? = null
    private var currentPagerIndex : Int = 0
    var customLayoutMap: WeakHashMap<Int, BaseListLayout<*, *>> = WeakHashMap()

    var recentProductCount = 0
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag() = HomeFragment::class.java.simpleName
    override fun getLayoutId() = R.layout.fragment_main_home
    override fun init() {
        initHeader()
        setEvenBus()
    }

    override fun onClick(v: View) {
        when (v.id) {
            // @TODO MENU
            R.id.image_side_menu ->{
                CommonUtil.startMenuActivity(context as MainActivity, Flag.RequestCode.SIDE_MENU)
            }
            R.id.image_search -> {
                CommonUtil.startSearchWordActivity(context as MainActivity,"", true)
            }
            R.id.image_shop_cart -> {
                CommonUtil.startCartActivity(context as MainActivity)
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    // 최근본 상품 Count 조회
    /*private fun setRecentProductCount() {
        try {
            CommonUtilKotlin.recentProductCount((context as MainActivity).getmDisposable(), (context as MainActivity).getmDb(), object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    try {
                        recentProductCount = value.toString().toInt()
                    } catch (e: Exception) {
                        recentProductCount = 0
                        if (CustomLog.flag) CustomLog.E(e)
                    }
                }
            })
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }*/

    private fun initHeader() {
        mBinding.layoutHeader.clickListener = this

        // Tab
        setTabLayout()
        setViewPager()
    }

    private fun setViewPager(){
        if(viewPagerAdapter == null){
            context?.let {
                var tabtitle = resources.getStringArray(R.array.main_titles)
                viewPagerAdapter = object : CustomViewPagerAdapter<String>(it,tabtitle, tabtitle){
                    override fun setViewLayout(container: ViewGroup, item: String, position: Int): View {
                        var vw : View
                        when(position){
                            0->{vw = HomeListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            1->{vw = WomenListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            2->{vw = MenListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            3->{vw = KidsListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            4->{vw = TimeDealListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            5->{vw = HomeListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            6->{vw = HomeListLayout(it).apply { mHomeFragment = this@HomeFragment }}
                            else->{vw = HomeListLayout(it)}
                            // WomenListLayout,KidsListLayout,MenListLayout
                        }
                        if(vw is BaseListLayout<*,*>) lifecycle.addObserver(vw)
                        customLayoutMap.put(position, vw as BaseListLayout<*, *>)
                        return vw
                    }
                }
            }
        }
        mBinding.viewpager.adapter = viewPagerAdapter
        mBinding.viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mBinding.layoutTab))
        mBinding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                currentPagerIndex = position
                if (customLayoutMap.containsKey(currentPagerIndex)) customLayoutMap.get(currentPagerIndex)!!.onFocusView()
            }
        })
        mBinding.viewpager.offscreenPageLimit = 3
        mBinding.viewpager.currentItem = currentPagerIndex
        mBinding.layoutTab.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(mBinding.viewpager){
            override fun onTabReselected(tab: TabLayout.Tab?) {
                currentPagerIndex = tab?.position ?: 0
                mBinding.viewpager.setCurrentItem(currentPagerIndex)
            }
        })
        val windowSize = Point()
        activity!!.windowManager.defaultDisplay.getSize(windowSize)
        /*mBinding.layoutTab.viewTreeObserver.addOnScrollChangedListener{
            val scrollX = mBinding.layoutTab.getScrollX() // Current x scrolling position
            if(scrollX >= 190) mBinding.imageviewLayoutTab.visibility = View.GONE
            else mBinding.imageviewLayoutTab.visibility = View.VISIBLE
            //if(CustomLog.flag)CustomLog.L("HomeFragment","scrollX",scrollX)
        }*/
        mBinding.imageviewLayoutTab.setOnClickListener {
            mBinding.viewpager.setCurrentItem(currentPagerIndex+1)
        }
    }


    private fun setTabLayout() {
        // Remove
        if (mBinding.layoutTab.childCount > 0) {
            mBinding.layoutTab.removeAllTabs()
        }
        // Tab
        // Dummy
        val titles = resources.getStringArray(R.array.main_titles)
        for (t in titles) {
            addCustomTabs(t, false)
        }
    }

    private fun addCustomTabs(title: String, isSelect: Boolean) {
        if (context != null) {
            val v = layoutInflater.inflate(R.layout.layout_tab_category, null)
            (v.findViewById(R.id.text_title) as TextView).text = title
            val tab = mBinding.layoutTab.newTab().setCustomView(v)
            mBinding.layoutTab.addTab(tab)
            if (isSelect) tab.select()
        }
    }


    @SuppressLint("CheckResult")
    private fun setEvenBus(){
        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
                Flag.RequestCode.HOME_MOVE -> {
                    try{
                        var index = requestCode.data as Int
                        currentPagerIndex = index
                        mBinding.viewpager.setCurrentItem(index)
                        (customLayoutMap.get(0) as HomeListLayout).listScrollTop()
                    }catch (e : Exception){
                        if(CustomLog.flag)CustomLog.E(e)
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onStart()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onResume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onPause()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mBinding.layoutAppbar.setExpanded(true,false)
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onStop()
            }
        }
    }

    ////////////////////////////////////////////////
}
