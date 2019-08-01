package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import com.google.gson.internal.LinkedTreeMap
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.view.activity.base.BindActivity
import kotlinx.android.synthetic.main.activity_searchzipwebview.*

class ReceiptActivity : BindActivity<io.temco.guhada.databinding.ActivityReceiptBinding>() {
    override fun getBaseTag(): String = ReceiptActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_receipt

    override fun getViewType(): Type.View = Type.View.RECEIPT

    override fun init() {
        mBinding.includeReceiptHeader.title = "영수증 조회"
        mBinding.includeReceiptHeader.setOnClickBackButton { finish() }

        val tId = intent.getStringExtra("tId")
        if (tId != null && tId.isNotEmpty()) getUrl(tId)
        else {
            ToastUtil.showMessage(getString(R.string.common_message_error))
            finish()
        }
    }

    private fun getUrl(tId: String) {
        OrderServer.getReceiptUrl(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val url = (it.data as LinkedTreeMap<String, String>)["data"]
                        if (url != null) initWebView(url)
                        else {
                            ToastUtil.showMessage(getString(R.string.common_message_error))
                            finish()
                        }
                    })
        }, tId)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String) {
        val settings = mBinding.webviewReceipt.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        mBinding.webviewReceipt.loadUrl(url)
    }
}