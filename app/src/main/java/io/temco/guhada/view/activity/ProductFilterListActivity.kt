package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.Brand
import io.temco.guhada.databinding.ActivityProductfilterlistBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ProductFilterPagerAdapter

/**
 * @author park jungho
 * 19.08.05
 * 카테고리, 브랜드 리스트 Activity
 *
 */
class ProductFilterListActivity : BindActivity<ActivityProductfilterlistBinding>() {
    private val REQUEST_CODE_CATEGORY = 201
    private val REQUEST_CODE_BRAND = 202

    private var mPagerAdapter: ProductFilterPagerAdapter? = null

    private var categoryType: Type.Category? = null
    private var hierarchies: IntArray = intArrayOf()
    private var brand: Brand? = null
    private var searchWord: String? = null

    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    var type = Type.ProductListViewType.NONE

    override fun getBaseTag(): String = this@ProductFilterListActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_productfilterlist
    override fun getViewType(): Type.View = Type.View.PRODUCT_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        this.overridePendingTransition(R.anim.fade, R.anim.fade)
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        if (!intent.extras.isEmpty && intent.extras.containsKey("type")) {
            type = intent.extras.getSerializable("type") as Type.ProductListViewType
            when (type) {
                Type.ProductListViewType.CATEGORY -> {
                    hierarchies = intent.getIntArrayExtra("hierarchies")
                    categoryType = intent.extras.getSerializable("categoryType") as Type.Category
                }
                Type.ProductListViewType.BRAND -> {
                    brand = intent.extras.getSerializable("brand") as Brand
                }
                Type.ProductListViewType.SEARCH -> {
                    searchWord = intent.extras.getString("search_word")
                }
                Type.ProductListViewType.VIEW_MORE -> {
                    searchWord = intent.extras.getString("search_word")
                }
            }
        }
        setViewInit()
    }


    private fun setViewInit() {
        initMainPager()
    }

    private fun initMainPager() {
        if (mPagerAdapter == null) mPagerAdapter = ProductFilterPagerAdapter(getSupportFragmentManager())
        // Pager
        mBinding.layoutPager.apply {
            adapter = mPagerAdapter
            setSwipeEnabled(true)
            offscreenPageLimit = mPagerAdapter?.count ?: 0
        }
        addFragmentList()
    }

    private fun addFragmentList() {
        if (type == Type.ProductListViewType.CATEGORY) {
            mPagerAdapter?.addProductCategoryData(categoryType!!, hierarchies)
        } else if (type == Type.ProductListViewType.BRAND) {
            mPagerAdapter?.setProductBrandData(brand!!)
        } else if (type == Type.ProductListViewType.SEARCH) {
            mPagerAdapter?.setProductSearchData(searchWord!!)
        } else if (type == Type.ProductListViewType.VIEW_MORE) {
            if (CustomLog.flag) CustomLog.L("initFilterBody addFragmentList", "type", type.name, "searchWord", searchWord!!)
            mPagerAdapter?.setProductConditionData(searchWord!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_CATEGORY -> {
                    if (data != null) {
                        val type = data.getSerializableExtra(Info.INTENT_CATEGORY_TYPE) as Type.Category
                        val hierarchies = data.getIntArrayExtra(Info.INTENT_CATEGORY_HIERARCHIES)
                        CommonUtil.startCategoryScreen(this@ProductFilterListActivity, type, hierarchies, true)
                    }
                }
                REQUEST_CODE_BRAND -> {
                    if (data != null) {
                        val b = data.getSerializableExtra(Info.INTENT_BRAND_DATA) as Brand
                        CommonUtil.startBrandScreen(this, b, true)
                    }
                }
                Flag.RequestCode.SEARCH_WORD -> {
                    if(CustomLog.flag)CustomLog.L("ProductFilterListActivity","onActivityResult SEARCH_WORD RESULT_OK finish")
                    overridePendingTransition(R.anim.fade, R.anim.fade)
                    finish()
                }
            }
        }else  if (requestCode == Flag.RequestCode.SIDE_MENU_FROM_PRODUCT_FILTER && resultCode == Activity.RESULT_FIRST_USER) {
            overridePendingTransition(R.anim.fade, R.anim.fade)
            finish()
        }
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable?.dispose()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.fade, R.anim.fade)
        super.onBackPressed()
        overridePendingTransition(R.anim.fade, R.anim.fade)
    }



}