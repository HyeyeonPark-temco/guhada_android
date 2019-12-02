package io.temco.guhada.view.activity

import android.app.Activity
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.databinding.ActivityFirstpurchaseBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.util.*

class FirstPurchaseDialogActivity : BindActivity<ActivityFirstpurchaseBinding>()  {

    private lateinit var state : FirstPurchaseType

    ////////////////////////////////////////////////////////////////////////////////
    override fun getBaseTag(): String = ImageGetActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_firstpurchase
    override fun getViewType(): Type.View = Type.View.CUSTOM_WEBVIEW

    override fun init() {
        state = intent?.extras?.getSerializable("state") as FirstPurchaseType
        if(CustomLog.flag)CustomLog.L("FirstPurchaseDialogActivity","state",state.name)
        mBinding.setClickCloseListener { onBackPressed() }
        mBinding.setClickStopListener {
            Calendar.getInstance().apply {
                Preferences.setFirstPurchasViewDialog(timeInMillis.toString())
                onBackPressed()
            }
        }

        when(state){
            FirstPurchaseType.FIRST_VIEW -> mBinding.relativeFirstpurchaseMain.visibility = View.VISIBLE
            FirstPurchaseType.FIRST_PURCHASE -> mBinding.relativeFirstpurchasePurchase.visibility = View.VISIBLE
            else -> onBackPressed()
        }
    }


    override fun onBackPressed() {
        overridePendingTransition(0, 0)
        setResult(Activity.RESULT_CANCELED)
        finish()
        overridePendingTransition(0, 0)
    }

    ////////////////////////////////////////////////////////////////////////////////

}


enum class FirstPurchaseType{ FIRST_VIEW,FIRST_PURCHASE }