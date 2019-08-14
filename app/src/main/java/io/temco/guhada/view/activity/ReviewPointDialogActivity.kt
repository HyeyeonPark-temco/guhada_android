package io.temco.guhada.view.activity

import android.os.Bundle
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.view.activity.base.BindActivity


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class ReviewPointDialogActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewpointdialogBinding>() {

    override fun getBaseTag(): String = ReviewPointDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewpointdialog
    override fun getViewType(): Type.View = Type.View.REVIEW_POINT_DIALOG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun init() {
    }


    ////////////////////////////////////////////////////////////////////////////////
}