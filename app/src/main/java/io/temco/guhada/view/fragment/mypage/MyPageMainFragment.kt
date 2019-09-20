package io.temco.guhada.view.fragment.mypage

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.mypage.MyPageViewModel
import io.temco.guhada.databinding.FragmentMainMypagehomeBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.custom.layout.mypage.*
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.viewpager.CustomViewPagerAdapter
import java.util.*

enum class MyPageTabType{DELIVERY,DELIVERY_CANCEL_EX,POINT,COUPON,BOOKMARK,FOLLOW_SELLER,LAST_VIEW,REVIEW,CLAIM,ADDRESS}
/**
 * 19.07.22
 * @author park jungho
 *
 * 전체 탭 구성
메인 - 주문배송 - 취소교환환불 - 포인트 - 쿠폰 - 찜한상품 - 팔로우한 스토어 - 최근본상품 - 상품리뷰 - 상품문의 - 배송지관리 - 회원등급 - 회원정보수정
 *
 * 공통
- 마이페이지 상단 메뉴 스와이프로 작업
- 메뉴 목록은 로컬에서 처리
- 풀 리퀘스트 적용 (os 기본으로 적용) (android SwipeRefreshLayout)
 *
 */
class MyPageMainFragment : BaseFragment<FragmentMainMypagehomeBinding>(), View.OnClickListener {
    private lateinit var mViewModel: MyPageViewModel
    private val SHIPPING_ADDRESS_IDX = 10

    // -------- LOCAL VALUE --------
    private var viewPagerAdapter: CustomViewPagerAdapter<String>? = null
    private var currentPagerIndex: Int = 0

    val mDisposable: CompositeDisposable = CompositeDisposable()
    var customLayoutMap: WeakHashMap<Int, BaseListLayout<*, *>> = WeakHashMap()

    var initView = false
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag() = MyPageMainFragment::class.java.simpleName
    override fun getLayoutId() = R.layout.fragment_main_mypagehome
    override fun init() {
        mViewModel = MyPageViewModel(context ?: mBinding.root.context)
        initView = false
        viewPagerAdapter = null
        setEventBus()
    }


    override fun onClick(v: View) {
        when (v.id) {
            // @TODO MENU
            R.id.image_side_menu ->{
                CommonUtil.startMenuActivity(context as MainActivity, Flag.RequestCode.SIDE_MENU)
            }
            R.id.image_search -> {
                //CommonUtil.debug("image_search")
                CommonUtil.startSearchWordActivity(context as MainActivity,null, true)
            }
            R.id.image_shop_cart -> {
                //CommonUtil.debug("image_shop_cart")
                CommonUtil.startCartActivity(context as MainActivity)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (CustomLog.flag) CustomLog.L("MyPageMainFragment ", "onStart----------------")
        if((context?.applicationContext as BaseApplication).isInitUserMaypage){
            (context?.applicationContext as BaseApplication).isInitUserMaypage = false
            initView = false
            viewPagerAdapter = null
        }
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onStart()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (CustomLog.flag) CustomLog.L("MyPageMainFragment ", "onResume----------------")
        if(CommonUtil.checkToken() && !initView){
            initHeader()
        }
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
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onStop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.dispose()
        if (customLayoutMap.isNotEmpty()) {
            for (v in customLayoutMap) {
                v.value.onDestroy()
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    fun setPagerIndexMove(index : Int){
        currentPagerIndex = index
        mBinding.viewpager.setCurrentItem(index)
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun initHeader() {
        if (CustomLog.flag) CustomLog.L("MyPageMainFragment ", "initHeader----------------")
        mBinding.layoutHeader.clickListener = this
        initView = true
        // Tab
        setTabLayout()
        setViewPager()
    }

    private fun setViewPager() {
        if (viewPagerAdapter == null) {
            context?.let {
                var tabtitle = resources.getStringArray(R.array.mypage_titles)
                viewPagerAdapter = object : CustomViewPagerAdapter<String>(it, tabtitle, tabtitle) {
                    override fun setViewLayout(container: ViewGroup, item: String, position: Int): View {
                        var vw: View
                        when (position) {
                            /*0 -> {
                                vw = MyPageMainLayout(it)
                            }*/
                            0 -> {
                                vw = MyPageDeliveryLayout(it)
                            }
                            1 -> {
                                vw = MyPageDeliveryCerLayout(it)
                            }
                            2 -> {
                                vw = MyPagePointLayout(it)
                            }
                            3 -> {
                                vw = MyPageCouponLayout(it)
                            }
                            4 -> {
                                vw = MyPageBookMarkLayout(it, mDisposable)
                            }
                            5 -> {
                                vw = MyPageFollowLayout(it)
                            }
                            6 -> {
                                vw = MyPageRecentLayout(it, mDisposable)
                            }
                            7 -> {
                                vw = MyPageReviewLayout(it)
                            }
                            8 -> {
                                vw = MyPageClaimLayout(it)
                            }
                            9 -> {
                                vw = MyPageAddressLayout(it)
                            }
                            /*11 -> {
                                vw = MyPageGradeLayout(it)
                            }
                            10 -> {
                                vw = MyPageUserInfoLayout(it)
                            }*/
                            else -> {
                                vw = MyPageMainLayout(it)
                            }
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
                if (customLayoutMap.containsKey(currentPagerIndex)) customLayoutMap.get(currentPagerIndex)!!.onFocusView()
            }
        })
        mBinding.viewpager.offscreenPageLimit = 1
        mBinding.viewpager.currentItem = currentPagerIndex
        mBinding.layoutTab.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(mBinding.viewpager) {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                currentPagerIndex = tab?.position ?: 0
                mBinding.viewpager.setCurrentItem(currentPagerIndex)
            }
        })
    }


    private fun setTabLayout() {
        // Remove
        if (mBinding.layoutTab.childCount > 0) {
            mBinding.layoutTab.removeAllTabs()
        }
        // Tab
        // Dummy
        val titles = resources.getStringArray(R.array.mypage_titles)
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
    private fun setEventBus(){
        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
                Flag.RequestCode.MYPAGE_MOVE -> {
                    if (CustomLog.flag) CustomLog.L("MyPageMainFragment LIFECYCLE", "EventBusHelper----------------MYPAGE_MOVE")
                    setPagerIndexMove(requestCode.data as Int)
                }
            }
        }
    }


    ////////////////////////////////////////////////

}