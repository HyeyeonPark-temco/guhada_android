package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.data.model.event.LuckyDrawList
import io.temco.guhada.databinding.ActivityLuckyeventdialogBinding
import io.temco.guhada.view.activity.base.BindActivity

class LuckyEventDialogActivity : BindActivity<ActivityLuckyeventdialogBinding>() {

    private lateinit var eventData : LuckyDrawList

    override fun getBaseTag(): String = this@LuckyEventDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_luckyeventdialog
    override fun getViewType(): Type.View = Type.View.LUCKY_EVENT_DIALOG

    override fun init() {
        if(intent?.extras!!.containsKey("eventData")){
            eventData = intent.extras.getSerializable("eventData") as LuckyDrawList
            if(CustomLog.flag)CustomLog.L("LuckyEventDialogActivity","eventData",eventData)

            setRequestOkPopup()



        }

        if(!::eventData.isInitialized) finish()
    }



    private fun setRequestOkPopup(){
        mBinding.title = eventData.title
        mBinding.imgUrl = eventData.imageUrl
        mBinding.date1 = DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerAnnouncementAt)
        mBinding.date2 = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerBuyFromAt)+ " - " +
                DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, eventData.winnerBuyToAt))
        mBinding.setClickCloseListener { finish() }
    }

}