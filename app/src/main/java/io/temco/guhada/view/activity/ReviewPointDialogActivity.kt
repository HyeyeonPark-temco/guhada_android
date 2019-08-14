package io.temco.guhada.view.activity

import android.app.Activity
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.ReviewPointDialogViewModel
import io.temco.guhada.view.activity.base.BindActivity


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class ReviewPointDialogActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewpointdialogBinding>() {

    private lateinit var mViewModel : ReviewPointDialogViewModel

    override fun getBaseTag(): String = ReviewPointDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewpointdialog
    override fun getViewType(): Type.View = Type.View.REVIEW_POINT_DIALOG

    override fun init() {
        mViewModel = ReviewPointDialogViewModel(this)
        mBinding.viewModel = mViewModel
        if(intent != null && intent.extras != null){
            if(intent.extras.containsKey("type")){
                mViewModel.mTypeNotPresentException.set(intent.extras.getInt("type"))
            }
        }
        mViewModel.getPointSummary()

        mBinding.setOnClickClose {
            setResult(Activity.RESULT_OK)
            finish()
        }
        mBinding.setOnClickOk {
            // 구매 확정인 경우 별도로
            if(mViewModel.mTypeNotPresentException.get() == 0){

            }else{
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
}