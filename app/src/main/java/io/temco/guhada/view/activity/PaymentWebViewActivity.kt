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
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.payment.PGResponse
import io.temco.guhada.data.viewmodel.payment.PaymentWebViewViewModel
import io.temco.guhada.databinding.ActivityPaymentwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import java.net.URISyntaxException
import java.net.URLEncoder


class PaymentWebViewActivity : BindActivity<ActivityPaymentwebviewBinding>() {
    private val RESULT_URL = "https://order.guhada.com/lotte/mobile/privyCertifyResult"
    private var mViewModel: PaymentWebViewViewModel = PaymentWebViewViewModel()
    private val RESULT_CODE_SUCCESS = "00"
    private val SCHEME = "ispmobile://"
    private val INSTALL_URL = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
    private var URL = ""
    private val SELLER_ID = "P_MID"
    private  val ORDER_ID = "P_OID"
    private val ORDER_PRICE = "P_AMT"
    private  val USER_NAME = "P_UNAME"
    private  val PRODUCT_NAME = "P_GOODS"
    private val AUTH_NEXT_URL = "P_NEXT_URL"
    private   val FINISH_URL = "P_RETURN_URL"
    private  val TIMESTAMP = "P_TIMESTAMP"
    private  val SIGNATURE = "P_SIGNATURE"
    private  val HASH_SIGNKEY = "P_MKEY"
    private  val OFFER_PERIOD = "P_OFFER_PERIOD"
    private   val PURCHASE_EMAIL = "P_EMAIL"
    private  val PURCHASE_MOBILE = "P_MOBILE"
    private  val STORE_NAME = "P_MNAME"
    private val CHARSET = "EUC-KR"

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

        URL = mViewModel.pgResponse.jsUrl
        // Log.e("결제 수단", "${intent.getStringExtra("payMethod")} ====> $URL")

        when (intent.getStringExtra("payMethod")) {
            "CARD" -> params = "$params&$COMPLEX_FIELD=${URLEncoder.encode("twotrs_isp=Y& block_isp=Y& twotrs_isp_noti=N&apprun_checked=Y", "EUC-KR")}"
            "VBank" -> {
            }      // 무통장 입금
            "DirectBank" -> params = "$params&$COMPLEX_FIELD=${URLEncoder.encode("apprun_checked=Y", "EUC-KR")}"   // 실시간 계좌이체
            "TOKEN" -> {
            }
        }

        mBinding.webviewPayment.settings.defaultTextEncodingName = "utf-8"
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

//            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//                return super.onConsoleMessage(consoleMessage)
//            }
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

        mBinding.webviewPayment.postUrl(URL, params.toByteArray())
    }

    inner class PaymentWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (url == mViewModel.pgResponse.returnUrl) {
                mBinding.webviewPayment.visibility = View.GONE
                view?.evaluateJavascript("(function(){return window.document.body.outerHTML})();") { html ->
                    StringEscapeUtils.unescapeJava(html).let {
                        val document = Jsoup.parse(it)
                        val resultCode = document.getElementById("resultCode")?.`val`() ?: ""
                        val resultMsg = document.select("input[name=resultMsg]").`val`() ?: ""
                        if(CustomLog.flag) CustomLog.L("결제 결과 메세지", resultMsg)

                        if (resultCode == RESULT_CODE_SUCCESS) {
                            val pgTid = document.getElementById("pgTid")?.`val`() ?: ""
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

                            if (CustomLog.flag) CustomLog.L("결제 요청", "[$resultCode] pgTid: $pgTid pgOid: $pgOid authToken: $authToken")


                            PGAuth().apply {
                                this.resultCode = resultCode
                                this.resultMsg = resultMsg
                                this.pgKind = pgKind

                                this.pgTid = pgTid
                                this.pgMid = pgMid
                                this.pgOid = pgOid
                                this.pgAmount = pgAmount

                                this.pgTidSample = pgTidSample
                                // this.cardQuota = mViewModel.pgResponse.cardQuota
                                //  this.cardNo = mViewModel.pgResponse.cardCd ?: ""
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
                            intent.putExtra("resultMessage",resultMsg)
                            setResult(Activity.RESULT_CANCELED, intent)
                            finish()
                        }
                    }
                }
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if(CustomLog.flag)  CustomLog.L("<LPAY>", url.toString())

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
                            var exceptIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            val pNm = exceptIntent.`package`
                            exceptIntent = Intent(Intent.ACTION_VIEW)
                            exceptIntent.data = "market://search?q=$pNm".toUri()
                            startActivity(exceptIntent)
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
        if(CustomLog.flag) CustomLog.L(tas, message)
    }
}