package io.temco.guhada.view.activity

import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.blockchain.TokenList
import io.temco.guhada.data.viewmodel.mypage.MyPageTokenViewModel
import io.temco.guhada.databinding.ActivityTokenhistoryBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.TokenHistoryAdapter

/**
 * 마이페이지-토큰 히스토리 Activity
 * @author Hyeyeon Park
 * @since 2019.11.27
 */
class TokenHistoryActivity : BindActivity<ActivityTokenhistoryBinding>() {
    private lateinit var mViewModel: MyPageTokenViewModel

    override fun getBaseTag(): String = this::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_tokenhistory

    override fun getViewType(): Type.View = Type.View.MYPAGE_TOKEN

    override fun init() {
        val token = intent.getSerializableExtra("token")
        if (token != null ) {
            mViewModel = MyPageTokenViewModel().apply {
                this.mToken = token as TokenList
                this.mTokenHistory.observe(this@TokenHistoryActivity, Observer {
                    mBinding.recyclerviewTokenHistory.adapter = TokenHistoryAdapter().apply { this.mList = it.userTokenItemResponseList }
                })
            }
            mViewModel.getTokenHistoryList(invalidTokenTask = {
                ToastUtil.showMessage(getString(R.string.login_message_requiredlogin))
                finish()
            })

            mBinding.includeTokenInfo.token =  token as TokenList
            mBinding.includeTokenHeader.title = token.tokenNameText
            mBinding.includeTokenHeader.setOnClickBackButton { finish() }
        } else {
            ToastUtil.showMessage(getString(R.string.common_message_error))
            finish()
        }
    }
}