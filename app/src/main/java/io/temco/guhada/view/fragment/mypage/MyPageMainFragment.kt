package io.temco.guhada.view.fragment.mypage

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.R
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnDrawerLayoutListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.viewmodel.mypage.MyPageViewModel
import io.temco.guhada.databinding.FragmentMainMypagehomeBinding
import io.temco.guhada.view.activity.AddShippingAddressActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.custom.layout.mypage.*
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.viewpager.CustomViewPagerAdapter
import java.util.*

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
    private var mDrawerListener: OnDrawerLayoutListener? = null
    private var viewPagerAdapter: CustomViewPagerAdapter<String>? = null
    private var currentPagerIndex: Int = 0

    val mDisposable: CompositeDisposable = CompositeDisposable()
    var customLayoutMap: WeakHashMap<Int, BaseListLayout<*, *>> = WeakHashMap()
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag() = MyPageMainFragment::class.java.simpleName
    override fun getLayoutId() = R.layout.fragment_main_mypagehome
    override fun init() {
        mViewModel = MyPageViewModel(context ?: mBinding.root.context)
        initHeader()
        initShippingAddressButtons()
    }

    private fun initShippingAddressButtons() {
        mBinding.buttonMypagehomeAddaddress.setOnClickListener {
            val intent = Intent(context, AddShippingAddressActivity::class.java)
            startActivityForResult(intent, RequestCode.ADD_SHIPPING_ADDRESS.flag)
        }
        mBinding.buttonMypagehomeSetdefaultaddress.setOnClickListener {
            val isExist = customLayoutMap.contains(SHIPPING_ADDRESS_IDX)
            if (isExist) {
                val shippingAddressLayout = customLayoutMap[SHIPPING_ADDRESS_IDX] as MyPageAddressLayout
                val selectedItem = shippingAddressLayout.getSelectedItem()
                if (selectedItem != null) {
                    mViewModel.onClickDefault(shippingAddressLayout.getSelectedPos(), selectedItem) { shippingAddressLayout.onRefresh() }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_side_menu -> if (mDrawerListener != null) {
                mDrawerListener!!.onDrawerEvnet(true)
            }
            R.id.image_search -> CommonUtil.debug("image_search")
            R.id.image_shop_cart -> CommonUtil.debug("image_shop_cart")
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    fun setOnDrawerLayoutListener(listener: OnDrawerLayoutListener) {
        mDrawerListener = listener
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun initHeader() {
        mBinding.layoutHeader.clickListener = this

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
                            0 -> {
                                vw = MyPageMainLayout(it)
                            }
                            1 -> {
                                vw = MyPageDeliveryLayout(it)
                            }
                            2 -> {
                                vw = MyPageDeliveryCerLayout(it)
                            }
                            3 -> {
                                vw = MyPagePointLayout(it)
                            }
                            4 -> {
                                vw = MyPageCouponLayout(it)
                            }
                            5 -> {
                                vw = MyPageBookMarkLayout(it, mDisposable)
                            }
                            6 -> {
                                vw = MyPageFollowLayout(it)
                            }
                            7 -> {
                                vw = MyPageRecentLayout(it, mDisposable)
                            }
                            8 -> {
                                vw = MyPageReviewLayout(it)
                            }
                            9 -> {
                                vw = MyPageClaimLayout(it)
                            }
                            10 -> {
                                vw = MyPageAddressLayout(it)
                            }
                            11 -> {
                                vw = MyPageGradeLayout(it)
                            }
                            12 -> {
                                vw = MyPageUserInfoLayout(it)
                            }
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
                if (currentPagerIndex == 10) {
                    mBinding.testLayout.visibility = View.VISIBLE
                } else {
                    mBinding.testLayout.visibility = View.GONE
                }
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


    ////////////////////////////////////////////////

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

}