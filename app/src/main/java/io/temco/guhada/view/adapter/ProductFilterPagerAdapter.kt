package io.temco.guhada.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnBackPressListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.view.fragment.product.ProductFragment
/**
 * @author park jungho
 * 19.08.05
 * 카테고리, 브랜드 상품 리스트 viewPage adapter
 *
 */
class ProductFilterPagerAdapter(private val mFragmentManager: FragmentManager) : FragmentPagerAdapter(mFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // -------- LOCAL VALUE --------
    private val TAG = MainPagerAdapter::class.java.simpleName
    private val TAG_PRODUCT = "product"
    var mProductFragment: ProductFragment? = null

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getCount(): Int {
        return 1
    }



    override fun getItem(position: Int): Fragment {
        when (position) {
            /*0 // Home
            -> {
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment()
                    // mHomeFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mHomeFragment
            }

            1 // Community
            -> {
                if (mCommunityFragment == null) {
                    mCommunityFragment = CommunityFragment()
                    //mCommunityFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mCommunityFragment
            }

            2 // My Page
            -> {
                if (mMyPageFragment == null) {
                    mMyPageFragment = MyPageMainFragment()
                    //mMyPageFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mMyPageFragment
            }*/
        }
        return Fragment()
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    fun addProductCategoryData(type: Type.Category, hierarchies: IntArray) {
        val c = CommonUtil.getCategory(hierarchies)
        if (c != null) {
            addProductFragment(c)
        }
    }

    fun setProductBrandData(brand: Brand) {
        addProductFragment(brand)
    }

    fun setProductSearchData(word: String) {
        addProductFragment(word)
    }

    fun setProductConditionData(word: String, viewMoreCategory: String?) {
        addProductFragmentCondition(word, viewMoreCategory)
    }


    fun removeProduct() {
        removeProductFragment()
    }

    fun removeAll() {
        if (mProductFragment != null) {
            mProductFragment!!.removeAll()
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun checkProductFragment() {
        if (mProductFragment == null) {
            mProductFragment = ProductFragment()
            mProductFragment!!.setOnBackPressListener{ this.removeProductFragment() }
        }
        // Exist
        if (!existProductFragment()) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.layout_container, mProductFragment!!, TAG_PRODUCT)
                    .commitAllowingStateLoss()
        }
    }

    private fun addProductFragment(c: Category) {
        checkProductFragment()
        mProductFragment!!.changeProduct(Type.ProductListViewType.CATEGORY)
        mProductFragment!!.setCategory(c)
    }

    private fun addProductFragment(b: Brand) {
        checkProductFragment()
        mProductFragment!!.changeProduct(Type.ProductListViewType.BRAND)
        mProductFragment!!.setBrand(b)
    }

    private fun addProductFragment(s: String) {
        checkProductFragment()
        mProductFragment!!.changeProduct(Type.ProductListViewType.SEARCH)
        mProductFragment!!.setSearch(s)
    }

    private fun addProductFragmentCondition(s: String, viewMoreCategory : String?) {
        if (CustomLog.flag) CustomLog.L("initFilterBody addProductFragmentCondition", "type VIEW_MORE", "searchWord", s)
        checkProductFragment()
        mProductFragment!!.changeProduct(Type.ProductListViewType.VIEW_MORE)
        mProductFragment!!.setCondition(s,viewMoreCategory)
    }


    private fun removeProductFragment() {
        if (mProductFragment != null) {
            mFragmentManager
                    .beginTransaction()
                    .remove(mProductFragment!!)
                    .commitAllowingStateLoss()
        }
    }

    private fun existProductFragment(): Boolean {
        return mProductFragment != null && mProductFragment === mFragmentManager.findFragmentByTag(TAG_PRODUCT)
    }

    ////////////////////////////////////////////////
}
