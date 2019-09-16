package io.temco.guhada.view.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.databinding.ActivitySplashBinding
import io.temco.guhada.view.activity.base.BindActivity


class SplashActivity : BindActivity<ActivitySplashBinding>() {

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////
    private lateinit var mDisposable: CompositeDisposable
    private lateinit var db: GuhadaDB

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
        getCategories()
        setPasswordConfirm()
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun getCategories() {
        ProductServer.getCategories(OnServerListener{ success, o ->
            if (success) {
                Preferences.setCategories(o as List<Category>)
                mDisposable.add(Observable.fromCallable<Int> {
                    db.categoryDao().deleteAll()
                    for (category in o){
                        var data = CategoryEntity(category, category.label, 0,category.hierarchies[category.hierarchies.size-1])
                        db.categoryDao().insert(data)

                        if(!category.children.isNullOrEmpty()){
                            for (category2 in category.children) {
                                var data2 = CategoryEntity(category2,category.label, 1, category2.hierarchies[category2.hierarchies.size - 1])
                                db.categoryDao().insert(data2)

                                if(!category2.children.isNullOrEmpty()){
                                    for (category3 in category2.children) {
                                        var data3 = CategoryEntity(category3,category.label, 2, category3.hierarchies[category3.hierarchies.size - 1])
                                        db.categoryDao().insert(data3)

                                        if(!category3.children.isNullOrEmpty()){
                                            for (category4 in category3.children) {
                                                var data4 = CategoryEntity(category4,category.label, 3, category4.hierarchies[category4.hierarchies.size - 1])
                                                db.categoryDao().insert(data4)
                                            }
                                        }

                                    }
                                }

                            }
                        }
                    }
                    db.categoryDao().getAll().size
                }.subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                    if(it > 0){
                        if(CustomLog.flag)CustomLog.L("SplashActivity subscribe",it)
                        getAllBrands()
                    }
                })
            }
        })
    }

    private fun getAllBrands() {
        ProductServer.getAllBrands(OnServerListener{ success, o ->
            if (success) {
                Preferences.setBrands(o as List<Brand>)
            }
            check()
        })
    }

    /* mDisposable.add(Observable.fromCallable<Boolean> {
            var isFlag = true
    try {
        adapter.items.clear()
        db.recentDealDao().deleteAll()
    }catch (e : java.lang.Exception){
        isFlag = false
    }
    isFlag
}.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
        if(result){
            totalItemSize.value = adapter.itemCount
            adapter.notifyDataSetChanged()
        }
    }
)*/

    private fun check() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }

    private fun setPasswordConfirm() {
        Preferences.setPasswordConfirm(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposable != null) {
            mDisposable!!.dispose()
        }
        GuhadaDB.destroyInstance()
    }

    ////////////////////////////////////////////////
}