package io.temco.guhada.view.activity

import android.app.Activity
import android.text.TextUtils
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.GlideApp
import io.temco.guhada.databinding.ActivityMainbannerpopupBinding
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.view.activity.base.BindActivity
import java.util.*

class MainBannerPopupActivity : BindActivity<ActivityMainbannerpopupBinding>()  {

    private lateinit var state : PopupViewType
    private lateinit var imgPath : String

    ////////////////////////////////////////////////////////////////////////////////
    override fun getBaseTag(): String = this::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_mainbannerpopup
    override fun getViewType(): Type.View = Type.View.CUSTOM_WEBVIEW

    override fun init() {
        state = intent?.extras?.getSerializable("state") as PopupViewType
        imgPath = intent?.extras?.getString("imgPath") ?: ""
        if(TextUtils.isEmpty(imgPath)) finish()
        if(CustomLog.flag) CustomLog.L("FirstPurchaseDialogActivity","state",state.name)
        mBinding.setClickCloseListener { onBackPressed() }
        mBinding.setClickStopListener {
            Calendar.getInstance().apply {
                Preferences.setMainBannerViewDialog(imgPath, timeInMillis.toString())
                onBackPressed()
            }
        }

        when(state){
            PopupViewType.POPUP_VIEW_STOP -> {
                mBinding.layoutMainbannerStop.visibility = View.VISIBLE
                if(!TextUtils.isEmpty(imgPath)){
                    ImageUtil.loadImage(GlideApp.with(this), mBinding.imageviewMainbannerStop, imgPath)
                }else finish()
            }
            PopupViewType.POPUP_VIEW -> {
                mBinding.layoutMainbannerView.visibility = View.VISIBLE
                if(!TextUtils.isEmpty(imgPath)){
                    ImageUtil.loadImage(GlideApp.with(this), mBinding.imageviewMainbannerView, imgPath)
                }else finish()
            }
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


enum class PopupViewType{ POPUP_VIEW_STOP, POPUP_VIEW }