package io.temco.guhada.view.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.Type
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
class TokenHistoryActivity : BindActivity<ActivityTokenhistoryBinding>(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mViewModel: MyPageTokenViewModel

    override fun getBaseTag(): String = this::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_tokenhistory
    override fun getViewType(): Type.View = Type.View.MYPAGE_TOKEN

    override fun onRefresh() {
        mBinding.swipeRefreshLayout.isRefreshing = false
        (mBinding.recyclerviewTokenHistory.adapter as TokenHistoryAdapter).clear()
        mViewModel.mHistoryPage = 1
        mViewModel.getTokenHistoryList()
    }

    override fun init() {
        val token = intent.getSerializableExtra("token")
        if (token != null) {
            initHeader(token  as TokenList)
            initViewModel(token)

            mViewModel.getTokenHistoryList()
            mBinding.swipeRefreshLayout.setOnRefreshListener(this)
            mBinding.includeTokenInfo.buttonTokenDeposit.setOnClickListener {
                // 입금
                val tokenName = token.tokenName

            }
            mBinding.includeTokenInfo.token = mViewModel.mToken
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        } else {
            ToastUtil.showMessage(getString(R.string.common_message_error))
            finish()
        }
    }
    private fun initHeader(token: TokenList){
        mBinding.includeTokenHeader.title = token.tokenNameText
        mBinding.includeTokenHeader.setOnClickBackButton { finish() }
    }

    private fun initViewModel(token: TokenList) {
        mViewModel = MyPageTokenViewModel().apply {
            this.mToken = token
            this.mInvalidTokenTask = {
                ToastUtil.showMessage(getString(R.string.login_message_requiredlogin))
                finish()
            }
            this.mTokenHistory.observe(this@TokenHistoryActivity, Observer {
                if (it.userTokenItemResponseList.isEmpty() && mBinding.recyclerviewTokenHistory.adapter?.itemCount == 0) {
                    mBinding.constraintlayoutTokenEmpty.visibility = View.VISIBLE
                } else {
                    if (mBinding.recyclerviewTokenHistory.adapter == null)
                        mBinding.recyclerviewTokenHistory.adapter = TokenHistoryAdapter()
                    (mBinding.recyclerviewTokenHistory.adapter as TokenHistoryAdapter).addItems(it.userTokenItemResponseList)

                    mBinding.constraintlayoutTokenEmpty.visibility = View.GONE
                    mBinding.linearlayoutTokenMore.visibility = if (it.page < it.totalPage) View.VISIBLE else View.GONE
                }

                this.mToken.balance = it.currentTokenBalance
            })
        }
    }
}