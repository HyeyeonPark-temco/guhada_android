package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.util.Log
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.Toast
import androidx.core.net.toUri
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.PGResponse
import io.temco.guhada.databinding.ActivityPaymentwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.net.URISyntaxException
import java.net.URLEncoder

class PaymentWebViewActivity : BindActivity<ActivityPaymentwebviewBinding>() {
    val SCHEME = "ispmobile://"
    val INSTALL_URL = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
    var URL = ""
    val DEV_CARD_URL = "https://devmobpay.lpay.com:410/smart/wcard/"
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
    val OFFER_PERIOD = "P_OFFER_PERIOD"
    val PURCHASE_EMAIL = "P_EMAIL"
    val PURCHASE_MOBILE = "P_MOBILE"
    val STORE_NAME = "P_MNAME"


    // 신용카드
    val CARD_REQUIRED_OPTION = "P_RESERVED"

    override fun getBaseTag(): String = PaymentWebViewActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_paymentwebview
    override fun getViewType(): Type.View = Type.View.PAYMENT_WEBVIEW

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        // 전 결제 방식 공통 필드
        when (intent.getStringExtra("payMethod")) {
            "CARD" -> {
                URL = "https://devmobpay.lpay.com:410/smart/wcard/"
            }
            "VBank" -> {
                // 무통장 입금
                URL = "https://devmobpay.lpay.com:410/smart/vbank/"
            }
            "DirectBank" -> {
                // 실시간 계좌
                URL = "https://devmobpay.lpay.com:410/smart/bank/"
            }
            "TOKEN" -> {
            }
        }

        val pgResponse = intent.getSerializableExtra("pgResponse") as PGResponse
        var params = "$SELLER_ID=${URLEncoder.encode(pgResponse.pgMid, "EUC-KR")}&" +
                "$ORDER_ID=${URLEncoder.encode(pgResponse.pgOid, "EUC-KR")}&" +
                "$ORDER_PRICE=${URLEncoder.encode(pgResponse.pgAmount, "EUC-KR")}&" +
                "$USER_NAME=${URLEncoder.encode(pgResponse.purchaseUserName, "EUC-KR")}&" +
                "$PRODUCT_NAME=${URLEncoder.encode(pgResponse.productName, "EUC-KR")}&" +
                "$AUTH_NEXT_URL=${URLEncoder.encode(pgResponse.nextUrl, "EUC-KR")}&" +
                "$FINISH_URL=${URLEncoder.encode(pgResponse.returnUrl, "EUC-KR")}&" +
                "$TIMESTAMP=${URLEncoder.encode(pgResponse.timestamp, "EUC-KR")}&" +
                "$SIGNATURE=${URLEncoder.encode(pgResponse.signature)}&" +
                "$HASH_SIGNKEY=${URLEncoder.encode(pgResponse.key)}&" +
                "$OFFER_PERIOD=${URLEncoder.encode(pgResponse.offerPeriod, "EUC-KR")}&" +
                "$PURCHASE_EMAIL=${URLEncoder.encode(pgResponse.purchaseEmail, "EUC-KR")}&" +
                "$PURCHASE_MOBILE=${URLEncoder.encode(pgResponse.purchasePhone, "EUC-KR")}&" +
                "$STORE_NAME=${URLEncoder.encode(pgResponse.mallName, "EUC-KR")}"

        params = "$params&$CARD_REQUIRED_OPTION=${URLEncoder.encode("twotrs_isp=Y& block_isp=Y& twotrs_isp_noti=N&apprun_checked=Y", "EUC-KR")}"

        mBinding.webviewPayment.webViewClient = PaymentWebViewClient()
        mBinding.webviewPayment.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@PaymentWebViewActivity)
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                                result?.confirm()
                        }
                        .setCancelable(false)
                        .create()
                        .show()

                return true
            }
        }
        mBinding.webviewPayment.settings.javaScriptEnabled = true
        mBinding.webviewPayment.settings.javaScriptCanOpenWindowsAutomatically = true
        mBinding.webviewPayment.settings.setSupportMultipleWindows(true)

        // Insecurity 페이지 허용
        mBinding.webviewPayment.settings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW

        // ThirdPartyCookies false 설정 시, 안심클릭 카드 결제 오류 발생
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mBinding.webviewPayment, true)
        } else {
            cookieManager.setAcceptCookie(true)
        }

        mBinding.webviewPayment.loadUrl("$URL?$params")
    }

    inner class PaymentWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.e("<LPAY>", url.toString())

            if (url.toString() == "http://13.209.10.68/lotte/mobile/privyCertifyResult") {
                Log.e("ㅇㅇㅇ", "결과")
            }

            if (!url?.startsWith("http://")!! && !url.startsWith("https://") && !url.startsWith("javascript")) {
                val intent: Intent
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                } catch (e: URISyntaxException) {
                    return false
                }

                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // ISP 앱이 없다면 알림을 통해 처리

                    if (url.startsWith(SCHEME)) {
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
                        alertIsp.show()
                        // SHOW ISP
                        return false
                    } else if (url.startsWith("intent://")) {
                        try {
                            var excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            val pNm = excepIntent.`package`
                            excepIntent = Intent(Intent.ACTION_VIEW)
                            excepIntent.data = "market://search?q=$pNm".toUri()
                            startActivity(excepIntent)
                        } catch (e: URISyntaxException) {
                            Log.e("<LPAY>", "INTENT:// 인입될 시 예외 처리 오류: $e")
                        }
                    }
                }
            } else {
                view?.loadUrl(url)
                return false
            }
            return true

        }
    }
}