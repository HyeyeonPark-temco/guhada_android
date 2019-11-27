package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.BuildConfig
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnCategoryListListener
import io.temco.guhada.common.sns.SnsLoginModule
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.viewmodel.SideMenuViewModel
import io.temco.guhada.databinding.ActivitySidemenuBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.category.SideMenuCategoryFirstListAdapter


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class SideMenuActivity : BindActivity<ActivitySidemenuBinding>() , View.OnClickListener {
    private val REQUEST_CODE_LOGIN = 101
    private val REQUEST_CODE_CATEGORY = 201
    private val REQUEST_CODE_BRAND = 202

    private lateinit var mViewModel: SideMenuViewModel


    private val mSideMenuCategoryAdapter: SideMenuCategoryFirstListAdapter by lazy  {
        SideMenuCategoryFirstListAdapter(this)
    }
    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    override fun getBaseTag(): String = this@SideMenuActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_sidemenu
    override fun getViewType(): Type.View = Type.View.SIDE_MENU

    /**
     * @exception [IllegalStateException] Only fullscreen opaque activities can request orientation
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        this.overridePendingTransition(R.anim.left_to_right, R.anim.fade)
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        mViewModel = SideMenuViewModel(this)
        mBinding.viewModel = mViewModel
        /*mViewModel.getEventList(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {

            }
        })*/
        setViewInit()
        if(CustomLog.flag)setAppInfo()
    }


    private fun setViewInit(){
        mBinding.listContents.layoutManager = LinearLayoutManager(this)
        mSideMenuCategoryAdapter.setOnCategoryListener(object : OnCategoryListListener{
            override fun onEvent(index: Int, category: Category) {
                startCategoryByHierarchy(category.type, category.hierarchies)
            }
        })
        mSideMenuCategoryAdapter.setItems(Preferences.getCategories())
        mBinding.listContents.setAdapter(mSideMenuCategoryAdapter)
        mBinding.clickListener = this
        mBinding.layoutHeader.clickListener = this
    }

    // Category
    private fun startCategoryByHierarchy(type: Type.Category, hierarchies: IntArray) {
        when (type) {
            Type.Category.ALL -> {
                startCategoryScreen(type, hierarchies)
            }
            else -> if (hierarchies != null && hierarchies.size >= 2) {
                CategorySubActivity.startActivityForResult(this, hierarchies[1], REQUEST_CODE_CATEGORY)
            }
        }
    }

    private fun startCategoryScreen(type: Type.Category, hierarchies: IntArray) {
        CommonUtil.startCategoryScreen(this, type, hierarchies, true)
    }


    override fun onClick(v: View) {
        when (v.id) {
            ////////////////////////////////////////////////
            // Side Menu
            R.id.image_home ->  gotoMain(true)
            R.id.image_setting -> {
                //CommonUtilKotlin.startActivityUserClaimGuhada(this@SideMenuActivity)
            }
            R.id.image_close -> {
                startActivityForResult(Intent(this, GuhadaTokenAddressCreateDialog::class.java),9)
                onBackPressed()
            }
            R.id.layout_brand -> BrandSubActivity.startActivityForResult(this, REQUEST_CODE_BRAND)

            // Sub Menu
            R.id.layout_sale -> CommonUtil.debug(baseTag, "layout_sale")

            R.id.layout_new_product -> CommonUtil.debug(baseTag, "layout_new_product")

            R.id.layout_power_deal -> CommonUtil.debug(baseTag, "layout_power_deal")

            R.id.layout_time_deal -> CommonUtil.debug(baseTag, "layout_time_deal")

            R.id.layout_limit_price -> CommonUtil.debug(baseTag, "layout_limit_price")

            R.id.layout_community -> CommonUtil.debug(baseTag, "layout_community")
        }////////////////////////////////////////////////
    }


    private fun checkLogin() {
        if (checkToken()) {
            if(CustomLog.flag)CustomLog.L("SideMenuActivity","checkLogin",true)
            changeLoginStatus(true)
        } else {
            // startLoginActivity();
            if(CustomLog.flag)CustomLog.L("SideMenuActivity","onClick",false)
            changeLoginStatus(false)
        }
    }

    /**
     * [사이드 메뉴] 상단 로그인/로그아웃
     *
     * @param isLogin
     */
    private fun changeLoginStatus(isLogin: Boolean) {
        if (mBinding != null) {
            if (isLogin) {
                mBinding.layoutHeader.textLogin.setText(getString(R.string.side_menu_login_out))
                mBinding.layoutHeader.layoutLogin.setOnClickListener{
                    Preferences.clearToken(true, (applicationContext as BaseApplication))
                    changeLoginStatus(false)
                    gotoMain(true)
                    
                    // sns 로그아웃
                    SnsLoginModule.logoutSNS()
                }
            } else {
                mBinding.layoutHeader.textLogin.setText(getString(R.string.side_menu_login_need))
                mBinding.layoutHeader.layoutLogin.setOnClickListener({ v -> startLoginActivity() })
            }
        }
    }

    private fun checkToken(): Boolean {
        val token = Preferences.getToken() ?: return false
        val current = (System.currentTimeMillis() / 1000L).toInt()
        val exp = JWT(token.accessToken!!).getClaim("exp").asInt()!!
        if(CustomLog.flag)CustomLog.L("SideMenuActivity","checkToken exp",exp)
        if(CustomLog.flag)CustomLog.L("SideMenuActivity","checkToken current",current)
        if (exp > current) {
            return true
        } else {
            Preferences.clearToken(true)
            return false
        }
    }

    private fun startLoginActivity() {
        startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_CODE_LOGIN)
    }


    ////////////////////////////////////////////////////////////////////////////////
    // override activity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_LOGIN -> {
                    changeLoginStatus(checkToken())
                    gotoMain(true)
                }
                REQUEST_CODE_CATEGORY ->{
                    // @TODO MENU
                    if (data != null) {
                        val type = data!!.getSerializableExtra(Info.INTENT_CATEGORY_TYPE) as Type.Category
                        val hierarchies = data!!.getIntArrayExtra(Info.INTENT_CATEGORY_HIERARCHIES)
                        startCategoryScreen(type, hierarchies)
                    }
                }
                REQUEST_CODE_BRAND ->{
                    // @TODO MENU
                    if (data != null) {
                        val brand = data!!.getSerializableExtra(Info.INTENT_BRAND_DATA) as Brand
                        CommonUtil.startBrandScreen(this@SideMenuActivity, brand,true)
                    }
                }

            }
        }
    }

    private fun gotoMain(initMain : Boolean){
        BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true,initMain)
        this@SideMenuActivity.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
        this@SideMenuActivity.onBackPressed()
    }

    private fun setAppInfo(){
        var info = ""
        if(CommonUtil.checkToken()){
            info += "userId="+CommonUtil.checkUserId()+", "
        }
        val pinfo = packageManager.getPackageInfo(packageName, 0)
        info += "appVer="+pinfo.versionName
        info += ", server="+ BuildConfig.BuildType.name
        mBinding.appInfo.text = info
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable?.dispose()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade, R.anim.right_to_left)
    }

    ////////////////////////////////////////////////////////////////////////////////
}