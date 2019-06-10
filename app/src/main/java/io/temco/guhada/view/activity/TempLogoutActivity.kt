package io.temco.guhada.view.activity

import android.view.View
import android.widget.Toast
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.sns.SnsLoginModule
import io.temco.guhada.databinding.ActivityTemplogoutBinding
import io.temco.guhada.view.activity.base.BindActivity

class TempLogoutActivity : BindActivity<ActivityTemplogoutBinding>() {
    override fun getBaseTag(): String = TempLogoutActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_templogout

    override fun getViewType(): Type.View = Type.View.TEMP_LOGOUT

    override fun init() {
        val listener = OnServerListener { _, o -> showMessage(o as String) }

        mBinding.onClickClearAccessToken = View.OnClickListener { Preferences.clearToken() }
        mBinding.onClickKakaoLogout = View.OnClickListener { SnsLoginModule.logoutForKakao(listener) }
        mBinding.onClickGoogleLogout = View.OnClickListener { SnsLoginModule.logoutForGoogle(listener) }
        mBinding.onClickFacebookLogout = View.OnClickListener { SnsLoginModule.logoutForFacebook(listener) }
        mBinding.onClickNaverLogout = View.OnClickListener { showMessage("네이버 정책상 미지원") }
        mBinding.executePendingBindings()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@TempLogoutActivity, message, Toast.LENGTH_SHORT).show()
    }
}