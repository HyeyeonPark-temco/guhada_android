package io.temco.guhada.view.activity

import android.content.Intent
import android.view.View
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.ReportWriteViewModel
import io.temco.guhada.view.activity.base.BindActivity
import java.util.logging.Handler

class ReportActivity : BindActivity<io.temco.guhada.databinding.ActivityReportBinding>(), View.OnClickListener {
    private lateinit var mRequestManager: RequestManager
    private lateinit var mViewModel : ReportWriteViewModel
    private lateinit var mHandler: Handler
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_report
    override fun getViewType(): Type.View = Type.View.REPORT_WRITE

    override fun init() {
    }


    override fun onClick(v: View?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////





}