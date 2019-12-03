package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.google.gson.JsonObject
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.event.LuckyDrawList
import io.temco.guhada.data.model.event.Status
import io.temco.guhada.data.viewmodel.LuckyEventDialogViewModel
import io.temco.guhada.databinding.ActivityLuckyeventdialogBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.custom.dialog.CustomMessageDialog

/**
 * @author park jungho
 *
 * 럭키드로우 이벤트 응모하기, 당첨자 확인 팝업
 *
 */
class LuckyEventDialogActivity : BindActivity<ActivityLuckyeventdialogBinding>() {

    private lateinit var mViewModel: LuckyEventDialogViewModel
    private lateinit var eventData: LuckyDrawList
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun getBaseTag(): String = this@LuckyEventDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_luckyeventdialog
    override fun getViewType(): Type.View = Type.View.LUCKY_EVENT_DIALOG

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = LuckyEventDialogViewModel(this)
        mBinding.viewModel = mViewModel
        if (intent?.extras!!.containsKey("eventData")) {
            eventData = intent.extras.getSerializable("eventData") as LuckyDrawList
            if (CustomLog.flag) CustomLog.L("LuckyEventDialogActivity", "eventData", eventData)
            when (eventData.statusCode) {
                Status.START.code -> {
                    userCheck()
                }
                Status.WINNER_ANNOUNCEMENT.code -> {
                    if (CommonUtil.checkToken()) {
                        mLoadingIndicatorUtil.show()
                        getLuckyDrawWinner()
                    } else {
                        CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                                cancelTask = {
                                    onBackPressed()
                                },
                                confirmTask = {
                                    CommonUtil.startLoginPage(this@LuckyEventDialogActivity)
                                    this@LuckyEventDialogActivity.onBackPressed()
                                }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")

                    }
                }
            }
        }

        if (!::eventData.isInitialized) onBackPressed()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Flag.RequestCode.LOGIN -> if (resultCode == Activity.RESULT_OK) userCheck() else onBackPressed()
            else -> onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.dismiss()
    }

    /**
     * @author park jungho
     *
     * 유저 확인
     * 로그인여부 확인
     */
    private fun userCheck() {
        if (CommonUtil.checkToken()) {
            // 로그인 되어 있는 사용자
            mLoadingIndicatorUtil.show()
            mViewModel.getEventUser(object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(!resultFlag){
                        mLoadingIndicatorUtil.dismiss()
                        finish()
                    }
                    else{
                        var eventUser = value as EventUser
                        if (eventUser.isUserLuckyEventCheck()) {
                            // 인증 및 럭키드로우 이벤트 응모 가능
                            requestLuckyDraw()
                        } else {
                            mLoadingIndicatorUtil.dismiss()
                            // 회원 수정 화면
                            moveEdit()
                        }
                        if (CustomLog.flag) CustomLog.L("LuckyEventDialogActivity", "eventUser", eventUser)
                        if (CustomLog.flag) CustomLog.L("LuckyEventDialogActivity", "eventUser isUserLuckyEventCheck", eventUser.isUserLuckyEventCheck())
                    }
                }
            })
        } else {
            // 로그인 페이지로 이동
            mLoadingIndicatorUtil.dismiss()
            moveLogin()
        }
    }

    private fun requestLuckyDraw() {
        mViewModel.getRequestLuckyDraw(eventData.dealId.toString(), object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mLoadingIndicatorUtil.dismiss()
                if (resultFlag) {
                    setRequestOkPopup()
                } else {
                    CommonViewUtil.showDialog(this@LuckyEventDialogActivity, value.toString(), false, object : OnBaseDialogListener {
                        override fun onClickOk() {
                            setResult(Activity.RESULT_OK)
                            onBackPressed()
                        }
                    })
                }
            }
        })
    }


    /**
     * @author park jungho
     *
     * 당첨자 확인 팝업
     */
    private fun setRequestOkPopup() {
        TrackingUtil.sendKochavaEvent(TrackingEvent.MainEvent.View_Lucky_Event_Request_Product.eventName)
        mBinding.layoutLuckydrawContent.visibility = View.VISIBLE
        mBinding.layoutLuckydrawRequest.visibility = View.VISIBLE
        mBinding.title = eventData.title
        mBinding.imgUrl = eventData.imageUrl
        mBinding.date1 = DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerAnnouncementAt)
        mBinding.date2 = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerBuyFromAt) + " - " +
                DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerBuyToAt))
        mBinding.setClickCloseListener {
            setResult(Activity.RESULT_OK)
            onBackPressed()
        }
    }

    // 로그인 페이지 이동
    private fun moveLogin() {
        var intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("eventData", eventData)
        startActivityForResult(intent, Flag.RequestCode.LOGIN)
    }

    // 회원정보 수정 페이지 이동
    private fun moveEdit() {
        var intent = Intent(this, LuckyDrawEditActivity::class.java)
        intent.putExtra("snsUser", EventUser.SnsSignUp())
        startActivityForResult(intent, Flag.RequestCode.LOGIN)
    }

    // 유저의 사용자 정보 확인
    private fun getLuckyDrawWinner() {
        mViewModel.getRequestLuckyDrawWinner(eventData.dealId, object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mLoadingIndicatorUtil.dismiss()
                if (resultFlag) {
                    var data = value as JsonObject
                    mBinding.layoutLuckydrawContent.visibility = View.VISIBLE
                    mBinding.layoutLuckydrawWinner.visibility = View.VISIBLE
                    mBinding.winnerTitle = data.get("title").asString
                    mBinding.winnerUser = data.get("userName").asString
                    mBinding.setClickCloseListener { onBackPressed() }
                } else {
                    CommonViewUtil.showDialog(this@LuckyEventDialogActivity, value.toString(), false, true)
                }
            }
        })
    }

    override fun onBackPressed() {
        this.overridePendingTransition(0, 0)
        super.onBackPressed()
        this.overridePendingTransition(0, 0)
    }
}