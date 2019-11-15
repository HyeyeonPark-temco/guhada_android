package io.temco.guhada.view.activity

import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.data.model.event.LuckyDrawList
import io.temco.guhada.data.viewmodel.LuckyEventDialogViewModel
import io.temco.guhada.databinding.ActivityLuckyeventdialogBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * @author park jungho
 *
 * 럭키드로우 이벤트 응모하기, 당첨자 확인 팝업
 *
 */
class LuckyEventDialogActivity : BindActivity<ActivityLuckyeventdialogBinding>() {

    private lateinit var mViewModel : LuckyEventDialogViewModel
    private lateinit var eventData : LuckyDrawList

    override fun getBaseTag(): String = this@LuckyEventDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_luckyeventdialog
    override fun getViewType(): Type.View = Type.View.LUCKY_EVENT_DIALOG

    override fun init() {
        if(intent?.extras!!.containsKey("eventData")){
            eventData = intent.extras.getSerializable("eventData") as LuckyDrawList
            if(CustomLog.flag)CustomLog.L("LuckyEventDialogActivity","eventData",eventData)

            setUserCheck()
        }

        if(!::eventData.isInitialized) finish()
    }


    /**
     * @author park jungho
     *
     * 유저 확인
     * 로그인여부 확인
     */
    private fun setUserCheck(){
        if(CommonUtil.checkToken()){
            // 로그인 되어 있는 사용자


        }else{
            // 로그인 페이지로 이동
            moveLogin()
        }
    }


    /**
     * @author park jungho
     *
     * 당첨자 확인 팝업
     */
    private fun setRequestOkPopup(){
        mBinding.layoutLuckydrawContent.visibility = View.VISIBLE
        mBinding.layoutLuckydrawRequest.visibility = View.VISIBLE
        mBinding.title = eventData.title
        mBinding.imgUrl = eventData.imageUrl
        mBinding.date1 = DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerAnnouncementAt)
        mBinding.date2 = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerBuyFromAt)+ " - " +
                DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerBuyToAt))
        mBinding.setClickCloseListener { finish() }
    }

    // 로그인 페이지 이동
    private fun moveLogin(){

    }

    // 유저의 사용자 정보 확인
    private fun getUserDataCheck(){

    }

}