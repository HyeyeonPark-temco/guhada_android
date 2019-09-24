package io.temco.guhada.view.fragment.productdetail

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.review.ReviewResponseContent
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailReviewViewModel
import io.temco.guhada.databinding.LayoutProductdetailReviewBinding
import io.temco.guhada.view.activity.ProductFragmentDetailActivity
import io.temco.guhada.view.adapter.productdetail.ProductDetailReviewAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType

/**
 * 상품상세-상품 리뷰
 * @author Hyeyeon Park
 */
class ProductDetailReviewFragment : BaseFragment<LayoutProductdetailReviewBinding>() {
    private lateinit var loadingIndicatorUtil: LoadingIndicatorUtil
    var mViewModel: ProductDetailReviewViewModel = ProductDetailReviewViewModel()
    lateinit var notifySummary: (averageReviewsRating: Float) -> Unit

    override fun getBaseTag(): String = ProductDetailReviewFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_review
    override fun init() {
        mViewModel.listener = object : OnProductDetailReviewListener {
            override fun showMessage(message: String) {
                ToastUtil.showMessage(message)
            }

            override fun showLoadingIndicator(task: () -> Unit) {
                this@ProductDetailReviewFragment.showLoadingIndicator()
                task()
            }

            override fun hideLoadingIndicator() {
                this@ProductDetailReviewFragment.hideLoadingIndicator()
            }

            override fun notifySummary(averageReviewsRating: Float) {
                this@ProductDetailReviewFragment.notifySummary(averageReviewsRating)
            }

            override fun onClickWriteReview() {
                if(CommonUtil.checkToken()){
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, MyPageTabType.REVIEW.ordinal, true)
                    (context as ProductFragmentDetailActivity).setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE)
                    (context as ProductFragmentDetailActivity).finish()
                }else{
                    CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.startLoginPage(context as AppCompatActivity)
                            }).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "ProductDetailReviewFragment")
                }
            }
        }

        mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        if (::loadingIndicatorUtil.isInitialized) loadingIndicatorUtil.hide()
    }

    fun setProductId(productId: Long) {
        mViewModel.productId = productId
        if (productId > 0) {
            mViewModel.getProductReviewSummary()
            mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize)
        }
    }

    private fun showLoadingIndicator() {
        if (!::loadingIndicatorUtil.isInitialized) {
            loadingIndicatorUtil = LoadingIndicatorUtil(this@ProductDetailReviewFragment.context
                    ?: BaseApplication.getInstance().applicationContext)
        }
        loadingIndicatorUtil.show()
    }

    private fun hideLoadingIndicator() {
        if (::loadingIndicatorUtil.isInitialized) {
            loadingIndicatorUtil.hide()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("productReviewSummary")
        fun RecyclerView.bindProductReviewSummary(summary: ReviewSummary) {

        }

        @JvmStatic
        @BindingAdapter("productReview")
        fun RecyclerView.bindProductReview(reviewList: MutableList<ReviewResponseContent>) {
            val adapter = this.adapter as ProductDetailReviewAdapter
            if (adapter.itemCount > 0) {
                adapter.addItems(reviewList)
            } else {
                adapter.setItems(reviewList)
            }
        }
    }

    interface OnProductDetailReviewListener {
        fun showMessage(message: String)
        fun showLoadingIndicator(task: () -> Unit)
        fun hideLoadingIndicator()
        fun notifySummary(averageReviewsRating: Float)
        fun onClickWriteReview()
    }
}