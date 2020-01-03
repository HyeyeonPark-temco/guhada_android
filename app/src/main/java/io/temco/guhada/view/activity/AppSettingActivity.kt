package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.AppSettingModel
import io.temco.guhada.databinding.ActivityAppsettingBinding
import io.temco.guhada.view.activity.base.BindActivity

class AppSettingActivity : BindActivity<ActivityAppsettingBinding>(), View.OnClickListener {

    private lateinit var mViewModel : AppSettingModel

    override fun getBaseTag(): String = AppSettingActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_appsetting
    override fun getViewType(): Type.View = Type.View.APP_SETTING

    override fun init() {
        mViewModel = AppSettingModel(this)
        val pInfo = this@AppSettingActivity.getPackageManager().getPackageInfo(packageName, 0)
        mViewModel.version = pInfo.versionName
        mBinding.viewModel = mViewModel
        mBinding.clickListener = this
        setResult(Activity.RESULT_CANCELED)
        mBinding.setOnClickBackButton { finish() }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageview_appSetting_toggle_01 ->{
                mViewModel.appSettingFlag01.set(!mViewModel.appSettingFlag01.get())
            }
            R.id.imageview_appSetting_toggle_02 ->{
                mViewModel.appSettingFlag02.set(!mViewModel.appSettingFlag02.get())
            }
            R.id.imageview_appSetting_toggle_03 ->{
                mViewModel.appSettingFlag03.set(!mViewModel.appSettingFlag03.get())
            }
            R.id.imageview_appSetting_toggle_04 ->{
                mViewModel.appSettingFlag04.set(!mViewModel.appSettingFlag04.get())
            }
            R.id.imageview_appSetting_toggle_05 ->{
                mViewModel.appSettingFlag05.set(!mViewModel.appSettingFlag05.get())
            }
            R.id.imageview_appSetting_toggle_06 ->{
                mViewModel.appSettingFlag06.set(!mViewModel.appSettingFlag06.get())
            }
            R.id.imageview_appSetting_logout ->{
                setResult(Activity.RESULT_OK)
                finish()
            }
            R.id.textview_appSetting_update ->{
                val appPackageName = "io.temco.guhada"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
        }
    }
}