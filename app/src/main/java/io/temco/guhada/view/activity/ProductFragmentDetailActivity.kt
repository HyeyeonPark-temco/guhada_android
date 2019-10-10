package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.auth0.android.jwt.JWT
import com.google.android.gms.analytics.HitBuilders
import io.temco.guhada.BuildConfig
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnMainListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Brand
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.fragment.productdetail.ProductDetailFragment
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle



/**
 * @see io.temco.guhada.view.fragment.productdetail.ProductDetailFragment
 * @author park jungho
 * 19.08.05
 * 상품 상세 Activity
 */
class ProductFragmentDetailActivity : BindActivity<io.temco.guhada.databinding.ActivityProductdetailfragmentBinding>(), OnMainListener {

    //private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private var mProductDetailFragment: ProductDetailFragment? = null

    private var dealId: Long = 0
    private var initView = false

    override fun getBaseTag(): String = this@ProductFragmentDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_productdetailfragment
    override fun getViewType(): Type.View = Type.View.PRODUCT_DETAIL

    override fun init() {
        dealId = intent.getLongExtra("dealId", dealId)
        initView = false
    }

    override fun onStart() {
        super.onStart()
        if(!initView){
            initView = true
            setViewInit()
        }
    }

    private fun setViewInit() {
        addProductDetailView(dealId)
    }

    private fun addProductDetailView(dealId: Long) {
        mBinding.viewMainProductdetail.bringToFront()
        mBinding.viewMainProductdetail.visibility = View.VISIBLE

        if (mProductDetailFragment != null) {
            mProductDetailFragment = ProductDetailFragment()
            mProductDetailFragment!!.dealId = dealId
            mProductDetailFragment!!.mainListener = this
            supportFragmentManager.beginTransaction().replace(mBinding.viewMainProductdetail.getId(), mProductDetailFragment!!).addToBackStack(null).commitAllowingStateLoss()
        } else {
            mProductDetailFragment = ProductDetailFragment()
            mProductDetailFragment!!.dealId = dealId
            mProductDetailFragment!!.mainListener = this
            supportFragmentManager.beginTransaction().add(mBinding.viewMainProductdetail.getId(), mProductDetailFragment!!).addToBackStack(null).commitAllowingStateLoss()
        }
    }

    override fun removeProductFragment() {
        // setResult(Activity.RESULT_OK)
        BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true,true)
        setResult(ResultCode.GO_TO_MAIN.flag)
        finish()
        //  removeProductDetailFragment()
    }

    // [2019.06.26] 임시 브릿지
    override fun removeProductDetailFragment() {
        /*mBinding.viewMainProductdetail.visibility = View.GONE
        if (mProductDetailFragment != null && mProductDetailFragment!!.isAdded())
            supportFragmentManager.beginTransaction().remove(mProductDetailFragment!!).commitAllowingStateLoss()
        mLoadingIndicatorUtil.hide()*/
        BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true,true)
        setResult(ResultCode.GO_TO_MAIN.flag)
        finish()
    }

    override fun showSideMenu(isOpen: Boolean) {
        if (CustomLog.flag) CustomLog.L("ProductFragmentDetailActivity", "showSideMenu")
        CommonUtil.startMenuActivity(this, Flag.RequestCode.SIDE_MENU)

    }

    override fun setBrandProductList(brand: Brand) {
        if (CustomLog.flag) CustomLog.L("ProductFragmentDetailActivity", "setBrandProductList")
        var intent = Intent(this@ProductFragmentDetailActivity, ProductFilterListActivity::class.java)
        intent.putExtra("type", Type.ProductListViewType.BRAND)
        intent.putExtra("brand", brand)
        this@ProductFragmentDetailActivity.startActivityForResult(intent, Flag.RequestCode.BASE)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            /*RESULT_FIRST_USER -> { //  RESULT_FIRST_USER: SideMenu.kt에서 받아옴
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag, true)
                setResult(ResultCode.GO_TO_MAIN.flag)
                finish()
            }*/
            Activity.RESULT_OK -> when (requestCode) {
                Flag.RequestCode.WRITE_CLAIM -> if (mProductDetailFragment != null) mProductDetailFragment!!.refreshClaims()
                RequestCode.COUPON_DOWNLOAD.flag -> if (mProductDetailFragment != null) mProductDetailFragment!!.setSaveCouponDisabled()
                RequestCode.SIDE_MENU.flag -> if (mProductDetailFragment != null) mProductDetailFragment!!.refreshCouponDownloadView()
            }
            /**
             * @author park jungho
             * 판매자 문의하기에서 해당 상품을 주문한적이 없는 경우 나오는 팝업에서 확인을 눌렀을때 상품 문의하기로 보내는 부분
             */
            Activity.RESULT_FIRST_USER -> when (requestCode) {
                Flag.RequestCode.USER_CLAIM_SELLER -> {
                    if (mProductDetailFragment != null && mProductDetailFragment!!.getProductId() > 0) {
                        val intent = Intent(this@ProductFragmentDetailActivity, WriteClaimActivity::class.java)
                        intent.putExtra("productId", mProductDetailFragment!!.getProductId())
                        startActivityForResult(intent, Flag.RequestCode.WRITE_CLAIM)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
                when (requestCode) {
                    RequestCode.SIDE_MENU.flag -> if (mProductDetailFragment != null) mProductDetailFragment!!.refreshCouponDownloadView()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    override fun onPause() {
        super.onPause()
        checkLogin()
    }

    private fun checkLogin() {
        if (checkToken()) {
            changeLoginStatus()
        } else {
            changeLoginStatus()
        }
    }

    override fun onBackPressed() {
        if (mProductDetailFragment != null && mProductDetailFragment!!.isVisible) {
            if (mProductDetailFragment?.getMenuVisible()
                            ?: false) mProductDetailFragment?.closeMenuPopup() // menu gone
            else finish()
        } else {
            finish()
        }
    }

    /**
     * [사이드 메뉴] 상단 로그인/로그아웃
     *
     *
     * @TODO MENU
     */
    private fun changeLoginStatus() {
        if (mBinding != null) {
            // [상품 상세] 문의 리스트 리프레시
            try {
                if (mProductDetailFragment != null) {
                    mProductDetailFragment!!.refreshIsMyClaimsVisible()
                    mProductDetailFragment!!.refreshClaims()
                }
            } catch (e: Exception) {
                if (CustomLog.flag) CustomLog.E(e)
            }
        }
    }

    private fun checkToken(): Boolean {
        val token = Preferences.getToken() ?: return false
        val current = (System.currentTimeMillis() / 1000L).toInt()
        val exp = JWT(token.accessToken!!).getClaim("exp").asInt()!!
        if (exp > current) {
            return true
        } else {
            Preferences.clearToken(false)
            return false
        }
    }
}