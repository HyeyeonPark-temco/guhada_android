package io.temco.guhada.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.model.AppVersionCheck
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SettleServer
import io.temco.guhada.data.viewmodel.main.MainDataRepository
import io.temco.guhada.databinding.ActivitySplashBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.io.Serializable


class SplashActivity : BindActivity<ActivitySplashBinding>() {

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////
    private lateinit var mDisposable: CompositeDisposable
    private lateinit var db: GuhadaDB
    private lateinit var premiumData : HomeDeal

    private var isFinishedCategoryData = false
    private var isFinishedBrandData = false
    private var isFinishedPremiumData = false

    private var schemeData : ActivityMoveToMain? = null

    override fun getBaseTag(): String {
        return SplashActivity::class.java.simpleName
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getViewType(): Type.View {
        return Type.View.SPLASH
    }

    override fun init() {
        if (CustomLog.flag) CustomLog.L(this.javaClass.simpleName, "init")
        mDisposable = CompositeDisposable()
        db = GuhadaDB.getInstance(this)!!
        BaseApplication.getInstance().moveToMain = null
        var data : Serializable? = intent?.extras?.getSerializable("schemeData")
        if(data != null) schemeData = data as ActivityMoveToMain
        mHandler.postDelayed(timeOutDialog, 60*1000)
        getAppVersionData()

        this.windowManager.defaultDisplay.getMetrics((this@SplashActivity.applicationContext as BaseApplication).matrix)
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun getCategories() {
        ProductServer.getCategories(OnServerListener{ success, o ->
            if (success) {
                (this@SplashActivity.applicationContext as BaseApplication).categoryList = o as List<Category>
                Preferences.setCategories(o)
                mDisposable.add(Observable.fromCallable<Int> {
                    db.categoryDao().deleteAll()
                    for (category in o){
                        var data = CategoryEntity(category, category.label, 0,category.hierarchies[category.hierarchies.size-1])
                        db.categoryDao().insert(data)
                        setChildCategory(category, 1, category.label)
                    }
                    db.categoryDao().getAll().size
                }.subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                    if(it > 0){
                        if(CustomLog.flag)CustomLog.L("SplashActivity subscribe",it)
                        isFinishedCategoryData = true
                        check()
                    }
                })
            }
        })
    }

    private fun setChildCategory(category : Category, depth : Int, label : String){
        if(!category.children.isNullOrEmpty()) {
            for (category_child in category.children) {
                var data = CategoryEntity(category_child, label, depth, category_child.hierarchies[category_child.hierarchies.size - 1])
                db.categoryDao().insert(data)
                setChildCategory(category_child, depth + 1,label)
            }
        }
    }

    private fun getAllBrands() {
        ProductServer.getAllBrands(OnServerListener{ success, o ->
            if (success) {
                Preferences.setBrands(o as List<Brand>)
            }
            isFinishedBrandData = true
            check()
        })
    }


    private fun getPremiumData() {
        MainDataRepository().getPremiumItem(Info.MAIN_UNIT_PER_PAGE, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(resultFlag){
                    premiumData = value as HomeDeal
                    isFinishedPremiumData = true
                }
                check()
            }
        })
    }


    private fun check() {
        if(CustomLog.flag)CustomLog.L("SplashActivity",
                "check isFinishedCategoryData",isFinishedCategoryData,"isFinishedBrandData",isFinishedBrandData,"isFinishedPremiumData",isFinishedPremiumData)
        if(isFinishedCategoryData && isFinishedBrandData && isFinishedPremiumData){
            mHandler.removeCallbacks(timeOutDialog)
            mHandler.postDelayed({
                var intent = Intent(this, MainActivity::class.java)
                if(::premiumData.isInitialized)intent.putExtra("premiumData",premiumData)
                if(schemeData != null){
                    if(CustomLog.flag)CustomLog.L("SplashActivity","schemeData",schemeData.toString())
                    BaseApplication.getInstance().moveToMain = schemeData
                }
                startActivity(intent)
                finish()
            }, 300)
        }
    }

    private fun setPasswordConfirm() {
        Preferences.setPasswordConfirm(false)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(timeOutDialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(timeOutDialog)
        if (mDisposable != null) mDisposable!!.dispose()
        GuhadaDB.destroyInstance()
    }


    private fun getAppVersionData(){
        isFinishedCategoryData = false
        isFinishedBrandData = false
        isFinishedPremiumData = false
        SettleServer.checkAppVersion(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var check = (o as BaseModel<*>).list as List<AppVersionCheck>
                        if (CustomLog.flag) CustomLog.L("getAppVersionData", check)
                        try {
                            val pInfo = this@SplashActivity.getPackageManager().getPackageInfo(packageName, 0)
                            val version = pInfo.versionName
                            for(d in check){
                                if("AOS".equals(d.osType)){
                                    /**
                                     * 서버에서 내려오는 버전 보다 현재 앱의 버전이 낮은 경우 업데이트 이동
                                     */
                                    if(d.isUpdateApp(version)){
                                        CommonViewUtil.showDialog(this@SplashActivity, "최신 버전으로 업데이트 진행해주세요.",false, object : OnBaseDialogListener{
                                            override fun onClickOk() {
                                                val appPackageName = packageName
                                                try {
                                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                                                } catch (anfe: android.content.ActivityNotFoundException) {
                                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                                                }
                                                finish()
                                            }
                                        })
                                    }else{
                                        initData()
                                    }
                                }
                            }
                        } catch (e: PackageManager.NameNotFoundException) {
                            e.printStackTrace()
                        }

                    },
                    dataNotFoundTask = {initData()},
                    failedTask = {initData()},
                    userLikeNotFoundTask = {initData()},
                    serverRuntimeErrorTask = {initData()},
                    dataIsNull = {initData()}
            )
        })
    }

    private fun initData(){
        getCategories()
        getPremiumData()
        getAllBrands()
        setPasswordConfirm()
    }
    ////////////////////////////////////////////////
}