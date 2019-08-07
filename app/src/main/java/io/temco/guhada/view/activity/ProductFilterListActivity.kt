package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnBrandListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.Brand
import io.temco.guhada.databinding.ActivityProductfilterlistBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ProductFilterPagerAdapter
import io.temco.guhada.view.custom.dialog.BrandListDialog
import io.temco.guhada.view.custom.dialog.CategoryListDialog

/**
 * @author park jungho
 * 19.08.05
 * 카테고리, 브랜드 리스트 Activity
 *
 */
class ProductFilterListActivity : BindActivity<ActivityProductfilterlistBinding>(), View.OnClickListener {
    private val REQUEST_CODE_CATEGORY = 201
    private val REQUEST_CODE_BRAND = 202

    private var mPagerAdapter: ProductFilterPagerAdapter? = null

    private var type = Type.ProductListViewType.NONE
    private var categoryType: Type.Category? = null
    private var hierarchies: IntArray = intArrayOf()
    private var brand: Brand? = null
    private var searchWord: String? = null
    private var mCategoryListDialog: CategoryListDialog? = null
    private var mBrandListDialog: BrandListDialog? = null

    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    override fun getBaseTag(): String = this@ProductFilterListActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_productfilterlist
    override fun getViewType(): Type.View = Type.View.PRODUCT_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        this.overridePendingTransition(R.anim.fade, R.anim.fade)
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        mBinding.setClickListener(this)
        if (!intent.extras.isEmpty && intent.extras.containsKey("type")) {
            type = intent.extras.getSerializable("type") as Type.ProductListViewType
            when (type) {
                Type.ProductListViewType.CATEGORY -> {
                    hierarchies = intent.getIntArrayExtra("hierarchies")
                    categoryType = intent.extras.getSerializable("categoryType") as Type.Category
                    mBinding.imageviewMaintabIcon1.setBackgroundResource(R.drawable.tool_icon_category_on)
                    mBinding.textviewMaintabTitle1.setTextColor(Color.parseColor("#5d2ed1"))
                }
                Type.ProductListViewType.BRAND -> {
                    brand = intent.extras.getSerializable("brand") as Brand
                    mBinding.imageviewMaintabIcon2.setBackgroundResource(R.drawable.tool_icon_brand_on)
                    mBinding.textviewMaintabTitle2.setTextColor(Color.parseColor("#5d2ed1"))
                }
                Type.ProductListViewType.SEARCH -> {
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
            swipeLocked = true
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

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.layout_maintab_layout1 -> showCategoryListDialog()

            R.id.layout_maintab_layout2 -> showBrandListDialog()

            R.id.layout_maintab_layout3 -> {
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true)
                this@ProductFilterListActivity.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                this@ProductFilterListActivity.overridePendingTransition(R.anim.fade, R.anim.fade)
                this@ProductFilterListActivity.finish()
                this@ProductFilterListActivity.overridePendingTransition(R.anim.fade, R.anim.fade)
            }

            R.id.layout_maintab_layout4 -> {
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_COMUNITY.flag, true)
                this@ProductFilterListActivity.setResult(Flag.ResultCode.GO_TO_MAIN_COMUNITY)
                this@ProductFilterListActivity.overridePendingTransition(R.anim.fade, R.anim.fade)
                this@ProductFilterListActivity.finish()
                this@ProductFilterListActivity.overridePendingTransition(R.anim.fade, R.anim.fade)
            }

            R.id.layout_maintab_layout5 -> {
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_MYPAGE.flag, true)
                this@ProductFilterListActivity.setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE)
                this@ProductFilterListActivity.overridePendingTransition(R.anim.fade, R.anim.fade)
                this@ProductFilterListActivity.finish()
                this@ProductFilterListActivity.overridePendingTransition(R.anim.fade, R.anim.fade)
            }
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


    // Dialog
    private fun showCategoryListDialog() {
        if (supportFragmentManager != null) {
            if (mCategoryListDialog == null) {
                mCategoryListDialog = CategoryListDialog()
                mCategoryListDialog!!.setOnCategoryListener({ category -> CommonUtil.startCategoryScreen(this, category.type, category.hierarchies, true) })
            }
            mCategoryListDialog!!.show(supportFragmentManager, baseTag)
        }
    }

    private fun showBrandListDialog() {
        if (supportFragmentManager != null) {
            if (mBrandListDialog == null) {
                mBrandListDialog = BrandListDialog()
                mBrandListDialog!!.setOnBrandListener { brand -> CommonUtil.startBrandScreen(this, brand, true) }
            }
            mBrandListDialog!!.show(supportFragmentManager, baseTag)
        }
    }


}