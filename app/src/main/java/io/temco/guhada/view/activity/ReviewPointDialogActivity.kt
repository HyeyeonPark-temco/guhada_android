package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Html
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.review.ReviewAvailableOrder
import io.temco.guhada.data.viewmodel.ReviewPointDialogViewModel
import io.temco.guhada.view.activity.base.BindActivity


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class ReviewPointDialogActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewpointdialogBinding>() {

    private lateinit var mViewModel: ReviewPointDialogViewModel

    override fun getBaseTag(): String = ReviewPointDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewpointdialog
    override fun getViewType(): Type.View = Type.View.REVIEW_POINT_DIALOG

    override fun init() {
        mViewModel = ReviewPointDialogViewModel(this)
        mBinding.viewModel = mViewModel
        if (intent != null && intent.extras != null) {
            if (intent.extras.containsKey("type")) {
                mViewModel.mTypeNotPresentException.set(intent.extras.getInt("type"))
                mBinding.maxPoint = 12345
                mBinding.textviewReviewpointdialogComplete.text = Html.fromHtml(resources.getString(R.string.review_result_dialog_complete_payment_desc, 12345))
            }
        }
        mViewModel.getPointSummary()

        mBinding.setOnClickClose {
            setResult(Activity.RESULT_OK)
            finish()
        }
        mBinding.setOnClickOk {
            // 구매 확정인 경우 별도로
            if (mViewModel.mTypeNotPresentException.get() == 0) {
                val reviewData = ReviewAvailableOrder().apply {
                    intent.getSerializableExtra("purchaseOrder").let {
                        if (it != null) {
                            val purchaseOrder = it as PurchaseOrder
                            this.dealId = purchaseOrder.dealId
                            this.orderProdGroupId = purchaseOrder.orderProdGroupId.toInt()
                            this.productId = purchaseOrder.productId
                            this.brandName = purchaseOrder.brandName
                            this.prodName = purchaseOrder.dealName
                            this.optionAttribute1 = purchaseOrder.optionAttribute1 ?: ""
                            this.optionAttribute2 = purchaseOrder.optionAttribute2 ?: ""
                            this.optionAttribute3 = purchaseOrder.optionAttribute3 ?: ""
                            this.season = purchaseOrder.season
                            this.discountPrice = purchaseOrder.discountPrice
                            this.orderPrice = purchaseOrder.orderPrice
                            this.originalPrice = purchaseOrder.originalPrice
                            this.quantity = purchaseOrder.quantity
                            this.imageUrl = purchaseOrder.imageUrl
                            this.imageName = purchaseOrder.imageName
                            this.shipCompleteTimestamp = purchaseOrder.shipCompleteTimestamp
                                    ?: "" // [2019.08.16] 현재 미배송 상품에도 연결되서 shipCompleteTimestamp 비어있음
                        }
                    }
                }

                val intent = Intent(this, ReviewWriteActivity::class.java)
                intent.putExtra("reviewData", reviewData)
                startActivityForResult(intent, RequestCode.REVIEW_WRITE.flag)
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.REVIEW_WRITE.flag && resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
}