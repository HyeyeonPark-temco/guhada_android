package io.temco.guhada.view.fragment.main

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnDrawerLayoutListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.databinding.FragmentMainHomeBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.custom.layout.main.HomeListLayout
import io.temco.guhada.view.custom.layout.main.KidsListLayout
import io.temco.guhada.view.custom.layout.main.MenListLayout
import io.temco.guhada.view.custom.layout.main.WomenListLayout
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.viewpager.CustomViewPagerAdapter


class HomeFragment : BaseFragment<FragmentMainHomeBinding>(), View.OnClickListener {

    // -------- LOCAL VALUE --------
    private var mDrawerListener: OnDrawerLayoutListener? = null
    private var viewPagerAdapter : CustomViewPagerAdapter<String>? = null
    private var currentPagerIndex : Int = 0
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
                        return vw
                    }
                }
            }
        }
        mBinding.viewpager.adapter = viewPagerAdapter
        mBinding.viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mBinding.layoutTab))
        mBinding.viewpager.offscreenPageLimit = 4
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

    ////////////////////////////////////////////////
}
