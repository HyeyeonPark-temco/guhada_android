package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.auth0.android.jwt.JWT
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnMainListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Brand
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.fragment.productdetail.ProductDetailFragment

/**
 * @author park jungho
 * 19.08.05
 * 상품 상세 Activity
 *
 */
class ProductFragmentDetailActivity : BindActivity<io.temco.guhada.databinding.ActivityProductdetailfragmentBinding>(), OnMainListener{

    //private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private var mProductDetailFragment: ProductDetailFragment? = null

    private var dealId: Long = 0

    override fun getBaseTag(): String = this@ProductFragmentDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_productdetailfragment
    override fun getViewType(): Type.View = Type.View.PRODUCT_DETAIL

    override fun init() {
        dealId = intent.getLongExtra("dealId",dealId)
        setViewInit()
    }


    private fun setViewInit(){
        addProductDetailView(dealId)
    }

    fun addProductDetailView(dealId: Long) {
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
        removeProductDetailFragment()
    }


    // [2019.06.26] 임시 브릿지
    override fun removeProductDetailFragment() {
        /*mBinding.viewMainProductdetail.visibility = View.GONE
        if (mProductDetailFragment != null && mProductDetailFragment!!.isAdded())
            supportFragmentManager.beginTransaction().remove(mProductDetailFragment!!).commitAllowingStateLoss()
        mLoadingIndicatorUtil.hide()*/
        finish()
    }
    override fun showSideMenu(isOpen: Boolean) {
        if(CustomLog.flag)CustomLog.L("ProductFragmentDetailActivity","showSideMenu")
        CommonUtil.startMenuActivity(this, Flag.RequestCode.SIDE_MENU)

    }

    override fun setBrandProductList(brand: Brand) {
        if(CustomLog.flag)CustomLog.L("ProductFragmentDetailActivity","setBrandProductList")
        var intent = Intent(this@ProductFragmentDetailActivity, ProductFilterListActivity::class.java)
        intent.putExtra("type", Type.ProductListViewType.BRAND)
        intent.putExtra("brand", brand)
        this@ProductFragmentDetailActivity.startActivityForResult(intent, Flag.RequestCode.BASE)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Flag.RequestCode.WRITE_CLAIM -> mProductDetailFragment!!.refreshClaims()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
            when (requestCode) {
                Flag.RequestCode.WRITE_CLAIM -> {
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
            finish()
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
            try{
                if (mProductDetailFragment != null) {
                    mProductDetailFragment!!.refreshIsMyClaimsVisible()
                    mProductDetailFragment!!.refreshClaims()
                }
            }catch (e: Exception){
                if(CustomLog.flag)CustomLog.E(e)
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
            Preferences.clearToken()
            return false
        }
    }
}