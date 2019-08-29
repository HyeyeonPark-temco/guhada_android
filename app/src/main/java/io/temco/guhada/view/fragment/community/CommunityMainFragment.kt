package io.temco.guhada.view.fragment.community


import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.viewmodel.community.CommunityMainViewPagerViewModel
import io.temco.guhada.databinding.FragmentMainCommunityBinding
import io.temco.guhada.view.activity.CreateBbsActivity
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.CommunityPagerAdapter
import io.temco.guhada.view.fragment.base.BaseFragment
import java.util.ArrayList

/**
 * 19.08.19
 * @author park jungho
 *
 * 전체 탭 구성
 * 메인 - 인기글 - 공지사항 - 패션문답 - 패션토크 - 패션추천 - 회원패션 - 소장품 - 비디오 쇼핑 - 포토 쇼핑 - 진품가품 - 쇼핑후기 - 세일정보
 *
 *
 */

class CommunityMainFragment : BaseFragment<FragmentMainCommunityBinding>(), View.OnClickListener {
    private var viewPagerAdapter : CommunityPagerAdapter? = null
    private lateinit var mViewModel : CommunityMainViewPagerViewModel

    var currentPagerIndex : Int = 0
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag() = CommunityMainFragment::class.java.simpleName
    override fun getLayoutId() = R.layout.fragment_main_community
    override fun init() {
        mViewModel = CommunityMainViewPagerViewModel(context = context!!)
        mBinding.viewModel = mViewModel
        mViewModel.communityInfoList.observe(this, Observer {
            if(CustomLog.flag)CustomLog.L("CommunityMainFragment",it.toString())
            initHeader()
        })
        mViewModel.getCommunityInfo()
        mBinding.setOnClickCreateBbs {
            var intent = Intent((context as AppCompatActivity), CreateBbsActivity::class.java)
            intent.putExtra("currentIndex",(if(currentPagerIndex<3) 0 else currentPagerIndex-3))
            (context as AppCompatActivity).startActivityForResult(intent, 0)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_side_menu -> CommonUtil.startMenuActivity(context as MainActivity, Flag.RequestCode.SIDE_MENU)
            R.id.image_search -> CommonUtil.startSearchWordActivity(context as MainActivity,null, true)
            R.id.image_shop_cart -> CommonUtil.startCartActivity(context as MainActivity)
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    fun getViewModel() : CommunityMainViewPagerViewModel{
        return mViewModel
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun initHeader() {
        mBinding.layoutHeader.clickListener = this
        // Tab
        var tabtitle = mutableListOf<String>()
        for (tab in mViewModel.communityInfoList.value!!){
            tabtitle.add(tab.communityCategoryName)
        }
        setTabLayout(tabtitle.toTypedArray())
        setViewPager(tabtitle.toTypedArray())
    }

    private fun setViewPager(tabtitle : Array<String>) {
        if (viewPagerAdapter == null) {
            if(CustomLog.flag)CustomLog.L("CommunityMainFragment","tabtitle.size",tabtitle.size)
            viewPagerAdapter = CommunityPagerAdapter(childFragmentManager, mViewModel.communityInfoList.value!!)
        }
        mBinding.viewpager.adapter = viewPagerAdapter
        mBinding.viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mBinding.layoutTab))
        mBinding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                currentPagerIndex = position
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
    }


    private fun setTabLayout(tabtitle : Array<String>) {
        // Remove
        if (mBinding.layoutTab.childCount > 0) {
            mBinding.layoutTab.removeAllTabs()
        }
        // Tab
        for (t in tabtitle) {
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
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}