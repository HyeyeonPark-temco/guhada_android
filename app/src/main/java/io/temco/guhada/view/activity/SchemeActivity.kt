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

    override fun init() {
        try{
            var uriData : Uri = intent.data
            var pgState = uriData.getQueryParameter("pg_state")
            if(CustomLog.flag)CustomLog.L("SchemeActivity","pgState",pgState)
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        bring2Front()
    }

    ////////////////////////////////////////////////////////////////////////////////


    private fun bring2Front() {
        val activtyManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = activtyManager.getRunningTasks(10)
        for (runningTaskInfo in runningTaskInfos) {
            if (this.packageName == runningTaskInfo.topActivity.getPackageName()) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME)
                finish()
                return
            }
        }
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}