package io.temco.guhada.view.activity

import android.content.Intent
import android.net.Uri
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.view.activity.base.BindActivity
import android.app.ActivityManager.MOVE_TASK_WITH_HOME
import android.R.id
import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.text.TextUtils
import io.temco.guhada.common.ActivityMoveToMain
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.enum.ResultCode


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class SchemeActivity : BindActivity<io.temco.guhada.databinding.ActivityCustomdialogBinding>() {

    override fun getBaseTag(): String = SchemeActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_scheme
    override fun getViewType(): Type.View = Type.View.SCHEME

    var schemeData : ActivityMoveToMain? = null

    override fun init() {
        try{
            val uriData : Uri = intent.data
            if(CustomLog.flag)CustomLog.L("SchemeActivity","query",intent.data.query?:"")
            val pgState = uriData.getQueryParameter("pg_state")
            val arg1 = uriData.getQueryParameter("arg1")?:""
            val arg2 = uriData.getQueryParameter("arg2")?:""
            if(!TextUtils.isEmpty(pgState)){
                if(CustomLog.flag)CustomLog.L("SchemeActivity","pgState",pgState)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","arg1",arg1)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","arg2",arg2)
                // constructor(resultCode: Int, resultPageIndex: Int, isMoveToMain: Boolean, isInitMain: Boolean, state: String, arg1: String, arg2: String) {
                //  ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, Info.MainHomeIndex.LUCKY_DRAW,true, isMainActivity)
                schemeData = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag, 0, true, true, pgState,arg1,arg2)
            }
            if(BaseApplication.getInstance().activityState != null) BaseApplication.getInstance().activityState.remove(this.javaClass.simpleName)
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        bring2Front()
    }

    ////////////////////////////////////////////////////////////////////////////////


    private fun bring2Front() {
        val activtyManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = activtyManager.getRecentTasks(20, ActivityManager.RECENT_IGNORE_UNAVAILABLE)
        for ((i, runningTaskInfo) in runningTaskInfos.withIndex()) {
            if(CustomLog.flag)CustomLog.L("SchemeActivity",i, "packageName",runningTaskInfo.baseIntent.component.packageName)
            if(CustomLog.flag)CustomLog.L("SchemeActivity",i, "baseIntent component className",runningTaskInfo.baseIntent.component.className)
            if (BaseApplication.getInstance().activityState != null && BaseApplication.getInstance().activityState.size >= 1) {
                if (this.packageName == runningTaskInfo.baseIntent.component.packageName) {
                    activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME)
                    if(CustomLog.flag)CustomLog.L("SchemeActivity",i, "bring2Front",runningTaskInfo.id)
                    if(schemeData!=null) {
                        BaseApplication.getInstance().moveToMain = schemeData
                        setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    }
                    finish()
                    return
                }
            }
        }
        var intent = Intent(this, SplashActivity::class.java)
        intent.putExtra("schemeData",schemeData)
        startActivity(intent)
        finish()
        /*val runningTaskInfos = activtyManager.runningAppProcesses
        for (runningTaskInfo in runningTaskInfos) {
            if(runningTaskInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front processName",runningTaskInfo.processName)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front importance",runningTaskInfo.importance)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front importanceReasonCode",runningTaskInfo.importanceReasonCode)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front importanceReasonPid",runningTaskInfo.importanceReasonPid)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front lastTrimLevel",runningTaskInfo.lastTrimLevel)
                if (this.packageName == runningTaskInfo.processName) {
                    activtyManager.moveTaskToFront(runningTaskInfo.pid, ActivityManager.MOVE_TASK_WITH_HOME)
                    if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front",runningTaskInfo.pid)
                    finish()
                    return
                }
            }
            *//*if (this.packageName == runningTaskInfo.topActivity.getPackageName()) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME)
                if(CustomLog.flag)CustomLog.L("SchemeActivity","bring2Front",runningTaskInfo.id)
                finish()
                return
            }*//*
        }*/
    }
}