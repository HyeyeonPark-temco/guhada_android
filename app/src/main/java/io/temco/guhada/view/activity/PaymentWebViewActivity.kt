package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import androidx.core.net.toUri
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.PGAuth
import io.temco.guhada.data.model.PGResponse
import io.temco.guhada.data.viewmodel.PaymentWebViewViewModel
import io.temco.guhada.databinding.ActivityPaymentwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import java.net.URISyntaxException
import java.net.URLEncoder


class PaymentWebViewActivity : BindActivity<ActivityPaymentwebviewBinding>() {
    private val RESULT_URL = "http://13.209.10.68/lotte/mobile/privyCertifyResult"
    private var mViewModel: PaymentWebViewViewModel = PaymentWebViewViewModel()
    val RESULT_CODE_SUCCESS = "00"
    val SCHEME = "ispmobile://"
    val INSTALL_URL = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
    var URL = ""
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
    val CHARSET = "EUC-KR"

    // 신용카드 전용 필드
    val COMPLEX_FIELD = "P_RESERVED"

    override fun getBaseTag(): String = PaymentWebViewActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_paymentwebview
    override fun getViewType(): Type.View = Type.View.PAYMENT_WEBVIEW

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        mViewModel.pgResponse = intent.getSerializableExtra("pgResponse") as PGResponse

        // 전 결제 방식 공통 필드
        var params = "$SELLER_ID=${URLEncoder.encode(mViewModel.pgResponse.pgMid, CHARSET)}&" +
                "$ORDER_ID=${URLEncoder.encode(mViewModel.pgResponse.pgOid, CHARSET)}&" +
                "$ORDER_PRICE=${URLEncoder.encode(mViewModel.pgResponse.pgAmount, CHARSET)}&" +
                "$USER_NAME=${URLEncoder.encode(mViewModel.pgResponse.purchaseUserName, CHARSET)}&" +
                "$PRODUCT_NAME=${URLEncoder.encode(mViewModel.pgResponse.productName, CHARSET)}&" +
                "$AUTH_NEXT_URL=${URLEncoder.encode(mViewModel.pgResponse.nextUrl, CHARSET)}&" +
                "$FINISH_URL=${URLEncoder.encode(mViewModel.pgResponse.returnUrl, CHARSET)}&" +
                "$TIMESTAMP=${URLEncoder.encode(mViewModel.pgResponse.timestamp, CHARSET)}&" +
                "$SIGNATURE=${URLEncoder.encode(mViewModel.pgResponse.signature)}&" +
                "$HASH_SIGNKEY=${URLEncoder.encode(mViewModel.pgResponse.key)}&" +
                "$OFFER_PERIOD=${URLEncoder.encode(mViewModel.pgResponse.offerPeriod, CHARSET)}&" +
                "$PURCHASE_EMAIL=${URLEncoder.encode(mViewModel.pgResponse.purchaseEmail, CHARSET)}&" +
                "$PURCHASE_MOBILE=${URLEncoder.encode(mViewModel.pgResponse.purchasePhone, CHARSET)}&" +
                "$STORE_NAME=${URLEncoder.encode(mViewModel.pgResponse.mallName, CHARSET)}"

        when (intent.getStringExtra("payMethod")) {
            "CARD" -> {
                URL = "https://mobpay.lpay.com/smart/wcard/"
                //  URL = "https://devmobpay.lpay.com:410/smart/wcard/"
                params = "$params&$COMPLEX_FIELD=${URLEncoder.encode("twotrs_isp=Y& block_isp=Y& twotrs_isp_noti=N&apprun_checked=Y", "EUC-KR")}"
            }
            "VBank" -> {
                // 무통장 입금
                URL = "https://devmobpay.lpay.com:410/smart/vbank/"
//                URL = "https://ksmobile.inicis.com/smart/vbank/"
            }
            "DirectBank" -> {
                // 실시간 계좌
                URL = "https://devmobpay.lpay.com:410/smart/bank/"
                params = "$params&$COMPLEX_FIELD=${URLEncoder.encode("apprun_checked=Y", "EUC-KR")}"
            }
            "TOKEN" -> {
            }
        }

        mBinding.webviewPayment.settings.defaultTextEncodingName = "EUC-KR"
        mBinding.webviewPayment.settings.javaScriptEnabled = true
        mBinding.webviewPayment.settings.javaScriptCanOpenWindowsAutomatically = true
        mBinding.webviewPayment.settings.setSupportMultipleWindows(true)
        mBinding.webviewPayment.settings.allowFileAccessFromFileURLs = true
        mBinding.webviewPayment.settings.allowUniversalAccessFromFileURLs = true
        mBinding.webviewPayment.settings.allowFileAccess = true
        mBinding.webviewPayment.settings.allowContentAccess = true

        mBinding.webviewPayment.webViewClient = PaymentWebViewClient()
        mBinding.webviewPayment.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                val dialog = AlertDialog.Builder(this@PaymentWebViewActivity)
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            result?.confirm()
                        }
                        .setCancelable(false)
                        .create()

                if (!isFinishing) {
                    dialog.show()
                }

                return true
            }
        }

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

        if (!mViewModel.pgResponse.jsUrl.isEmpty()) {
            URL = mViewModel.pgResponse.jsUrl
        }

        mBinding.webviewPayment.postUrl(URL, params.toByteArray())
    }

    inner class PaymentWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (url == RESULT_URL) {
                mBinding.webviewPayment.visibility = View.GONE
                view?.evaluateJavascript("(function(){return window.document.body.outerHTML})();") { html ->
                    StringEscapeUtils.unescapeJava(html).let {
                        val document = Jsoup.parse(it)
                        val resultCode = document.getElementById("resultCode")?.`val`() ?: ""

                        if (resultCode == RESULT_CODE_SUCCESS) {
                            val pgTid = document.getElementById("pgTid")?.`val`() ?: ""
                            val resultMsg = document.select("input[name=resultMsg]").`val`() ?: ""
                            val pgKind = mViewModel.pgResponse.pgKind
                            val pgMid = mViewModel.pgResponse.pgMid
                            val pgOid = mViewModel.pgResponse.pgOid
                            val pgAmount = mViewModel.pgResponse.pgAmount
                            val pgTidSample = document.select("input[name=pgTidSample]").`val`()
                                    ?: ""

                            val authToken = document.getElementById("rsltAuthToken")?.`val`() ?: ""
                            val authUrl = document.getElementById("authUrl")?.`val`() ?: ""
                            val netCancelUrl = document.getElementById("netCancelUrl")?.`val`()
                                    ?: ""
                            val checkAckUrl = document.getElementById("checkAckUrl")?.`val`() ?: ""

                            val vbankReceivedCd = document.getElementById("vbankReceivedCd")?.`val`()
                                    ?: ""
                            val vbankReceivedNm = document.getElementById("vbankReceivedNm")?.`val`()
                                    ?: ""
                            val vbankReceivedNo = document.getElementById("vbankReceivedNo")?.`val`()
                                    ?: ""

                            CommonUtil.debug("결제 요청", "[$resultCode] pgTid: $pgTid pgOid: $pgOid")

                            PGAuth().apply {
                                this.pgTid = pgTid
                                this.resultCode = resultCode
                                this.resultMsg = resultMsg
                                this.pgKind = pgKind
                                this.pgMid = pgMid
                                this.pgAmount = pgAmount
                                this.pgOid = pgOid
                                this.pgTidSample = pgTidSample
                                this.cardQuota = mViewModel.pgResponse.cardQuota
                                this.cardNo = mViewModel.pgResponse.cardCd ?: ""
                                this.returnUrl = mViewModel.pgResponse.returnUrl

                                this.authToken = authToken
                                this.authUrl = authUrl
                                this.netCancel = netCancelUrl
                                this.checkAckUrl = checkAckUrl

                                // 무통자 입금(가상계좌)
                                this.vbankReceivedCd = vbankReceivedCd
                                this.vbankReceivedNm = vbankReceivedNm
                                this.vbankReceivedNo = vbankReceivedNo

                                this.parentMethodCd = mViewModel.pgResponse.parentMethodCd
                                this.purchaseEmail = mViewModel.pgResponse.purchaseEmail
                                this.purchaseUserName = mViewModel.pgResponse.purchaseUserName
                                this.purchasePhone = mViewModel.pgResponse.purchasePhone
                                this.productName = mViewModel.pgResponse.productName

                                this.web = false
                            }.let { pgAuth ->
                                intent.putExtra("pgAuth", pgAuth)
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                        } else {
                            setResult(Activity.RESULT_CANCELED, intent)
                            finish()
                        }

                    }
                }
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            CommonUtil.debug("<LPAY>", url.toString())

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
                        AlertDialog.Builder(this@PaymentWebViewActivity)
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setTitle("NOTIFICATION")
                                .setMessage("모바일 ISP 어플리케이션이 설치되어있지 않습니다.\n설치를 눌러 진행해 주십시요.\n취소를 누르면 결제가 취소됩니다.")
                                .setPositiveButton("설치") { dialog, which ->
                                    view?.loadUrl(INSTALL_URL)
                                    finish()
                                }
                                .setNegativeButton("취소") { dialog, which ->
                                    ToastUtil.showMessage("(-1)결제를 취소하셨습니다.")
                                    finish()
                                }.create().show()
                        return false
                    } else if (url.startsWith("intent://")) {
                        try {
                            var excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            val pNm = excepIntent.`package`
                            excepIntent = Intent(Intent.ACTION_VIEW)
                            excepIntent.data = "market://search?q=$pNm".toUri()
                            startActivity(excepIntent)
                        } catch (e: URISyntaxException) {
                            catchException("<LPAY>", "INTENT:// 진입될 시 예외 처리 오류: $e")
                        } catch (e: ActivityNotFoundException) {
                            catchException("<LPAY>", "INTENT:// AVD에서 진입될 시 예외 처리 오류: $e")
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

    private fun catchException(tas: String, message: String) {
        AlertDialog.Builder(this@PaymentWebViewActivity)
                .setMessage("해당 외부 앱과 연결할 수 없습니다.")
                .setPositiveButton("확인") { dialog, which ->
                    finish()
                }.create().show()
        CommonUtil.debug(tas, message)
    }
}