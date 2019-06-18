package io.temco.guhada.view.activity

import android.view.View
import android.widget.Toast
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.sns.SnsLoginModule
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.databinding.ActivityTemplogoutBinding
import io.temco.guhada.view.activity.base.BindActivity

class TempLogoutActivity : BindActivity<ActivityTemplogoutBinding>() {
    private lateinit var loadingDialog: LoadingIndicatorUtil

    override fun getBaseTag(): String = TempLogoutActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_templogout

    override fun getViewType(): Type.View = Type.View.TEMP_LOGOUT

    override fun init() {
        val listener = OnServerListener { _, o ->
            loadingDialog.dismiss()
            showMessage(o as String)
        }
        loadingDialog = LoadingIndicatorUtil(this@TempLogoutActivity)

        mBinding.onClickClearAccessToken = View.OnClickListener {
            loadingDialog.execute {
                Preferences.clearToken()
                loadingDialog.dismiss()
            }
        }
        mBinding.onClickKakaoLogout = View.OnClickListener {
            loadingDialog.execute { SnsLoginModule.logoutForKakao(listener) }
        }
        mBinding.onClickGoogleLogout = View.OnClickListener {
            loadingDialog.execute { SnsLoginModule.logoutForGoogle(listener) }
        }
        mBinding.onClickFacebookLogout = View.OnClickListener {
            loadingDialog.execute { SnsLoginModule.logoutForFacebook(listener) }
        }
        mBinding.onClickKakaoUnlink = View.OnClickListener {
            loadingDialog.execute { SnsLoginModule.unlinkForKakao(listener) }
        }
        mBinding.onClickNaverLogout = View.OnClickListener {
            loadingDialog.execute {
                showMessage("네이버 정책상 미지원")
                loadingDialog.dismiss()
            }
        }
        mBinding.executePendingBindings()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@TempLogoutActivity, message, Toast.LENGTH_SHORT).show()
    }

}