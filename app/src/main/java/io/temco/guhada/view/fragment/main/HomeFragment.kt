package io.temco.guhada.view.fragment.main

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Info
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnMainCustomLayoutListener
import io.temco.guhada.common.listener.OnMainListListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.MainBanner
import io.temco.guhada.data.viewmodel.main.MainDataRepository
import io.temco.guhada.data.viewmodel.main.MainListPageViewModel
import io.temco.guhada.databinding.FragmentMainHomeBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.custom.layout.main.*
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.viewpager.CustomViewPagerAdapter
import java.util.*


class HomeFragment : BaseFragment<FragmentMainHomeBinding>(), View.OnClickListener, OnMainListListener {

    // -------- LOCAL VALUE --------
    private lateinit var mViewModel: MainListPageViewModel
    private var viewPagerAdapter: CustomViewPagerAdapter<String>? = null
    private var currentPagerIndex: Int = 0
    lateinit var customLayoutMap: WeakHashMap<Int, BaseListLayout<*, *>>
    lateinit var mHandler: Handler
    lateinit var mainCustomLayoutListenerMap : WeakHashMap<Int,OnMainCustomLayoutListener>
    lateinit var premiumData : HomeDeal
    lateinit var bestData: HomeDeal
    lateinit var newInData: HomeDeal

    lateinit var mainBanner: ArrayList<MainBanner>

    private var isFinishedBestData = false
    private var isFinishedBannerData = false
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag() = HomeFragment::class.java.simpleName
    override fun getLayoutId() = R.layout.fragment_main_home

    override fun init() {
        mViewModel = MainListPageViewModel(context!!)
        mBinding.viewModel = mViewModel

        arguments?.getSerializable("premiumData")?.let {
            premiumData = it as HomeDeal
        }

        customLayoutMap = WeakHashMap()
        mHandler = Handler(context?.mainLooper)

        getMainBannerList(true)
        getBestItemList(true)
        setEvenBus()
    }

    override fun onClick(v: View) {
        when (v.id) {
            // @TODO MENU
            R.id.image_side_menu -> {
                CommonUtil.startMenuActivity(context as MainActivity, Flag.RequestCode.SIDE_MENU)
            }
            R.id.image_search -> {
                CommonUtil.startSearchWordActivity(context as MainActivity, "", true)
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


    private fun initHeader() {
        mBinding.layoutHeader.clickListener = this
        mViewModel.getPlusItem()
        if(!::newInData.isInitialized) {
            mHandler.postDelayed(Runnable {
                getNewInItemList()
            }, 2000)
        }
        // Tab
        setTabLayout()
        setViewPager()
    }

    private fun setViewPager() {
        if (viewPagerAdapter == null) {
            context?.let {
                var tabtitle = resources.getStringArray(R.array.main_titles)
                if(!::mainCustomLayoutListenerMap.isInitialized) mainCustomLayoutListenerMap = WeakHashMap()
                viewPagerAdapter = object : CustomViewPagerAdapter<String>(it, tabtitle, tabtitle) {
                    override fun setViewLayout(container: ViewGroup, item: String, position: Int): View {
                        var vw: View

                        when (position) {
                            0 -> {
                                vw = HomeListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                    mainCustomLayoutListenerMap[position] = this
                                }
                                vw.setData(premiumData, bestData, mainBanner)
                                if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                            }
                            1 -> {
                                vw = WomenListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                    mainCustomLayoutListenerMap[position] = this
                                }
                                if(!::newInData.isInitialized){
                                    mHandler.postDelayed(Runnable {
                                        (customLayoutMap[1] as WomenListLayout)?.setData(premiumData, bestData)
                                        if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                                    },1000)
                                }else{
                                    vw.setData(premiumData, bestData)
                                    if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                                }
                            }
                            2 -> {
                                vw = MenListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                    mainCustomLayoutListenerMap[position] = this
                                }
                                if(!::newInData.isInitialized){
                                    mHandler.postDelayed(Runnable {
                                        (customLayoutMap[2] as MenListLayout)?.setData(premiumData, bestData)
                                        if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                                    },1300)
                                }else{
                                    vw.setData(premiumData, bestData)
                                    if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                                }
                            }
                            3 -> {
                                vw = KidsListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                    mainCustomLayoutListenerMap[position] = this
                                }
                                if(!::newInData.isInitialized){
                                    mHandler.postDelayed(Runnable {
                                        (customLayoutMap[3] as KidsListLayout)?.setData(premiumData, bestData)
                                        if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                                    },1600)
                                }else{
                                    vw.setData(premiumData, bestData)
                                    if(::newInData.isInitialized) mainCustomLayoutListenerMap[position]?.loadNewInDataList(position, newInData)
                                }
                            }
                            4 -> {
                                vw = TimeDealListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                }
                            }
                            5 -> {
                                vw = LuckyDrawListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                }
                            }
                            6 -> {
                                vw = EventListLayout(it).apply {
                                    mHomeFragment = this@HomeFragment
                                    mainListListener = this@HomeFragment
                                }
                            }
                            else -> {
                                vw = EventListLayout(it)
                            }
                            // WomenListLayout,KidsListLayout,MenListLayout
                        }
                        if (vw is BaseListLayout<*, *>) lifecycle.addObserver(vw)
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
                when(position){
                    1,2,3-> mBinding.viewLine.visibility = View.GONE
                    else -> mBinding.viewLine.visibility = View.VISIBLE
                }
                if (customLayoutMap.containsKey(currentPagerIndex)) {
                    customLayoutMap.get(currentPagerIndex)!!.onFocusView()
                    if (customLayoutMap.isNotEmpty()) {
                        for (k in customLayoutMap.keys) {
                            if (k != currentPagerIndex)
                                customLayoutMap[k]!!.onReleaseView()
                        }
                    }
                }
            }
        })
        mBinding.viewpager.offscreenPageLimit = 3
        mBinding.viewpager.currentItem = currentPagerIndex
        mBinding.layoutTab.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(mBinding.viewpager) {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                currentPagerIndex = tab?.position ?: 0
                mBinding.viewpager.setCurrentItem(currentPagerIndex)
            }
        })
        val windowSize = Point()
        activity!!.windowManager.defaultDisplay.getSize(windowSize)
        mBinding.imageviewLayoutTab.setOnClickListener {
            mBinding.viewpager.setCurrentItem(currentPagerIndex + 1)
        }
    }


    private fun setTabLayout() {
        // Remove
        if (mBinding.layoutTab.childCount > 0) {
            mBinding.layoutTab.removeAllTabs()
        }
        // Tab
        // Dummy
        if(context != null){
            val titles = context!!.resources!!.getStringArray(R.array.main_titles)
            for (t in titles) {
                if(t == "타임딜" || t == "럭키드로우") addCustomTabsRed(t, false)
                else addCustomTabs(t, false)
            }
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


    private fun addCustomTabsRed(title: String, isSelect: Boolean) {
        if (context != null) {
            val v = layoutInflater.inflate(R.layout.layout_tab_category_red, null)
            (v.findViewById(R.id.text_title) as TextView).text = title
            val tab = mBinding.layoutTab.newTab().setCustomView(v)
            mBinding.layoutTab.addTab(tab)
            if (isSelect) tab.select()
        }
    }

    private fun getBestItemList(init : Boolean) {
        MainDataRepository().getBestItem(Info.MAIN_UNIT_PER_PAGE, object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(resultFlag){
                    bestData = value as HomeDeal
                    isFinishedBestData = true
                    if(init && isFinishedBestData && isFinishedBannerData) initHeader()
                }
            }
        })
    }

    private fun getNewInItemList() {
        MainDataRepository().getNewIn(Info.MAIN_UNIT_PER_PAGE, object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(resultFlag){
                    newInData = value as HomeDeal
                    for (i in 0..3) mainCustomLayoutListenerMap[i]?.loadNewInDataList(i, newInData!!)
                }
            }
        })
    }


    private fun getMainBannerList(init : Boolean) {
        MainDataRepository().getMainBanner(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag)CustomLog.L("getMainBannerList","MainBanner callBackListener",resultFlag)
                if(resultFlag){
                    isFinishedBannerData = true
                    mainBanner = value as ArrayList<MainBanner>
                    if(init && isFinishedBestData && isFinishedBannerData) initHeader()
                }
            }
        })
    }


    @SuppressLint("CheckResult")
    private fun setEvenBus() {
        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
                Flag.RequestCode.HOME_MOVE -> {
                    try {
                        var index = requestCode.data as Int
                        currentPagerIndex = index
                        mHandler.postDelayed(Runnable {
                            mBinding.viewpager.setCurrentItem(currentPagerIndex,true)
                            if (CustomLog.flag) CustomLog.L("GO_TO_MAIN_HOME setEvenBus -", "currentPagerIndex", currentPagerIndex)
                            if(customLayoutMap.containsKey(currentPagerIndex)){
                                when(currentPagerIndex){
                                    0-> (customLayoutMap[currentPagerIndex] as HomeListLayout).listScrollTop()
                                    1-> (customLayoutMap[currentPagerIndex] as WomenListLayout).listScrollTop()
                                    2-> (customLayoutMap[currentPagerIndex] as MenListLayout).listScrollTop()
                                    3-> (customLayoutMap[currentPagerIndex] as KidsListLayout).listScrollTop()
                                    4-> (customLayoutMap[currentPagerIndex] as TimeDealListLayout).listScrollTop()
                                    5-> (customLayoutMap[currentPagerIndex] as LuckyDrawListLayout).listScrollTop()
                                }

                            }
                        },150)
                    }catch (e : Exception){
                        if(CustomLog.flag)CustomLog.E(e)
                    }
                }
                RequestCode.CART_BADGE.flag -> {
                    val count = requestCode.data as Int
                    if(count > 0){
                        mBinding.layoutHeader.textviewBadge.visibility = View.VISIBLE
                        mBinding.layoutHeader.textviewBadge.text = count.toString()
                    }else{
                        mBinding.layoutHeader.textviewBadge.visibility = View.GONE
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
        mBinding.layoutAppbar.setExpanded(true, false)
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onStop()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding.layoutAppbar.setExpanded(true, false)
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onDestroy()
            }
        }
    }

    override fun requestDataList(tabIndex: Int, type: String) {
        if(CustomLog.flag)CustomLog.L("HomeFragment","requestDataList","tabIndex","type",type)
        if(::mainCustomLayoutListenerMap.isInitialized && mainCustomLayoutListenerMap.containsKey(tabIndex)){
            mainCustomLayoutListenerMap[tabIndex]?.updateDataList(tabIndex,type)
        }
    }

    ////////////////////////////////////////////////
}
