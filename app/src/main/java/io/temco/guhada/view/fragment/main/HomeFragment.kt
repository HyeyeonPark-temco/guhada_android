package io.temco.guhada.view.fragment.main

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.databinding.FragmentMainHomeBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.custom.layout.main.HomeListLayout
import io.temco.guhada.view.custom.layout.main.KidsListLayout
import io.temco.guhada.view.custom.layout.main.MenListLayout
import io.temco.guhada.view.custom.layout.main.WomenListLayout
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.viewpager.CustomViewPagerAdapter
import java.util.*


class HomeFragment : BaseFragment<FragmentMainHomeBinding>(), View.OnClickListener {

    // -------- LOCAL VALUE --------
    private var viewPagerAdapter : CustomViewPagerAdapter<String>? = null
    private var currentPagerIndex : Int = 0
    var customLayoutMap: WeakHashMap<Int, BaseListLayout<*, *>> = WeakHashMap()
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag() = HomeFragment::class.java.simpleName
    override fun getLayoutId() = R.layout.fragment_main_home
    override fun init() {
        initHeader()
        setLinkText()
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
                            0->{vw = HomeListLayout(it)}
                            1->{vw = HomeListLayout(it)}
                            2->{vw = HomeListLayout(it)}
                            3->{vw = HomeListLayout(it)}
                            4->{vw = HomeListLayout(it)}
                            5->{vw = HomeListLayout(it)}
                            6->{vw = HomeListLayout(it)}
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

    private fun setLinkText() {
        // Sales Number
        /*mBinding.layoutInformation.textInformationSalesNumber.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.layoutInformation.textInformationSalesNumber.setText(TextUtil.createTextWithLink(getContext(),
                R.string.information_company_sales_number_en, R.string.information_confirm_company_en,
                R.dimen.text_11, true,
                () -> {
                    if (true) {
                        CommonUtil.debug("Company Info!");
                    }
                }), TextView.BufferType.SPANNABLE);
        // Description
        mBinding.layoutInformation.textInformationDescription.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.layoutInformation.textInformationDescription.setText(TextUtil.createTextWithLink(getContext(),
                R.string.information_description_en, R.string.information_confirm_service_en,
                R.dimen.text_11, true,
                () -> {
                    if (true) {
                        CommonUtil.debug("Sevice Confirm!");
                    }
                }), TextView.BufferType.SPANNABLE);*/
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
