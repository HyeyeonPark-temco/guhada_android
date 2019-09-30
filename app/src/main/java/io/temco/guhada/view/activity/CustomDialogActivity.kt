package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Html
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.review.ReviewAvailableOrder
import io.temco.guhada.data.viewmodel.CustomDialogActivityViewModel
import io.temco.guhada.data.viewmodel.ReviewPointDialogViewModel
import io.temco.guhada.view.activity.base.BindActivity


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class CustomDialogActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewpointdialogBinding>() {

    private lateinit var mViewModel: CustomDialogActivityViewModel

    override fun getBaseTag(): String = CustomDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewpointdialog
    override fun getViewType(): Type.View = Type.View.REVIEW_POINT_DIALOG

    override fun init() {
        mViewModel = CustomDialogActivityViewModel(this)

    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    ////////////////////////////////////////////////////////////////////////////////
}