package io.temco.guhada.view.activity

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.PGResponse
import io.temco.guhada.databinding.ActivityPaymentwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.net.URLEncoder

class PaymentWebViewActivity : BindActivity<ActivityPaymentwebviewBinding>() {
    val SCHEME = "ispmobile://"
    val INSTALL_URL = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
    val DEV_CARD_URL = "https://devmobpay.lpay.com:410/smart/wcard"
    val SELLER_ID = "P_MID"
    val ORDER_ID = "P_OID"
    val ORDER_PRICE = "P_AMT"
    val USER_NAME = "P_UNAME"
    val PRODUCT_NAME = "P_GOODS"
    val AUTH_NEXT_URL = "P_NEXT_URL"
    val FINISH_URL = "P_RETURN_URL"
    val TIMESTAMP = "P_TIMESTAMP"
    val SIGNATURE = "P_SIGNATURE"
    val HASH_SIGNKEY = "P_MKEY"

    override fun getBaseTag(): String = PaymentWebViewActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_paymentwebview
    override fun getViewType(): Type.View = Type.View.PAYMENT_WEBVIEW

    override fun init() {
        // 전 결제 방식 공통 필드
        val pgResponse = intent.getSerializableExtra("pgResponse") as PGResponse

        val params = "$SELLER_ID=${URLEncoder.encode(pgResponse.pgMid, "UTF-8")}&" +
                "$ORDER_ID=${URLEncoder.encode(pgResponse.pgOid, "UTF-8")}&" +
                "$ORDER_PRICE=${URLEncoder.encode(pgResponse.pgAmount, "UTF-8")}&" +
                "$USER_NAME=${URLEncoder.encode(pgResponse.purchaseUserName, "UTF-8")}&" +
                "$PRODUCT_NAME=${URLEncoder.encode(pgResponse.productName, "UTF-8")}&" +
                "$AUTH_NEXT_URL=${URLEncoder.encode(pgResponse.nextUrl, "UTF-8")}&" +
                "$FINISH_URL=${URLEncoder.encode(pgResponse.returnUrl, "UTF-8")}&" +
                "$TIMESTAMP=${URLEncoder.encode(pgResponse.timestamp, "UTF-8")}&" +
                "$SIGNATURE=${URLEncoder.encode(pgResponse.signature, "UTF-8")}&" +
                "$HASH_SIGNKEY=${URLEncoder.encode(pgResponse.key, "UTF-8")}"

        mBinding.webviewPayment.webViewClient = PaymentWebViewClient()
        mBinding.webviewPayment.postUrl(DEV_CARD_URL, params.toByteArray())
    }

    inner class PaymentWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val alertIsp = AlertDialog.Builder(this@PaymentWebViewActivity)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle("NOTIFICATION")
                    .setMessage("모바일 ISP 어플리케이션이 설치되어있지 않습니다.\n설치를 눌러 진행해 주십시요.\n취소를 누르면 결제가 취소됩니다.")
                    .setPositiveButton("설치", DialogInterface.OnClickListener { dialog, which ->
                        view?.loadUrl(INSTALL_URL)
                        finish()
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(this@PaymentWebViewActivity, "(-1)결제를 취소하셨습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }).create()

            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(intent)
                // 삼성카드 안심클릭
                if (url?.startsWith(SCHEME) == true) finish()
            } catch (e: ActivityNotFoundException) {
                // url prefix가 ispmobile인 경우, alert 띄움
                if (url?.startsWith(SCHEME) == true) {
                    view?.loadData("<html><body></body></html>", "text/html", "euc-kr")
                    alertIsp.show()
                    return true
                }
            }
            return true
        }
    }
}