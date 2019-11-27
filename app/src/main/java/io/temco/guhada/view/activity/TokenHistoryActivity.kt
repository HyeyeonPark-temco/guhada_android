package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.databinding.ActivityTokenhistoryBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 마이페이지-토큰 히스토리 Activity
 * @author Hyeyeon Park
 * @since 2019.11.27
 */
class TokenHistoryActivity : BindActivity<ActivityTokenhistoryBinding>() {
    override fun getBaseTag(): String = this::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_tokenhistory

    override fun getViewType(): Type.View = Type.View.MYPAGE_TOKEN

    override fun init() {
        val tokenName = intent.getStringExtra("tokenName")
        if (tokenName.isNullOrEmpty())
            finish()
        else {
            if (CustomLog.flag) CustomLog.L(tokenName)
        }
    }
}