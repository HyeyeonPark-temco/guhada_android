package io.temco.guhada.view.fragment.productdetail

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.common.ActivityMoveToMain
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.review.ReviewResponse
import io.temco.guhada.data.model.review.ReviewResponseContent
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailReviewViewModel
import io.temco.guhada.databinding.LayoutProductdetailReviewBinding
import io.temco.guhada.view.activity.ProductFragmentDetailActivity
import io.temco.guhada.view.adapter.productdetail.ProductDetailReviewAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment.Companion.bindProductReview

/**
 * 상품상세-상품 리뷰
 * @author Hyeyeon Park
 */
class ProductDetailReviewFragment : BaseFragment<LayoutProductdetailReviewBinding>() {

    enum class ReviewTab(val pos: Int) {
        ALL(0),
        PHOTO(1),
        SIZE(2),
        COMMENT(3)
    }

    private lateinit var loadingIndicatorUtil: LoadingIndicatorUtil
    var mViewModel: ProductDetailReviewViewModel = ProductDetailReviewViewModel()
    lateinit var notifySummary: (averageReviewsRating: Float) -> Unit

    override fun getBaseTag(): String = ProductDetailReviewFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_review
    override fun init() {
        initViewModel()
        initTab()

        mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
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
                if (CommonUtil.checkToken()) {
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, MyPageTabType.REVIEW.ordinal, true)
                    (context as ProductFragmentDetailActivity).setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE)
                    (context as ProductFragmentDetailActivity).finish()
                } else {
                    CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.startLoginPage(context as AppCompatActivity)
                            }).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "ProductDetailReviewFragment")
                }
            }
        }
    }

    private fun initTab() {
        mBinding.tablayoutProductdetailReview.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter()
                mViewModel.reviewResponse = ReviewResponse()
                mViewModel.reviewPage = 0
                mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, tab?.position
                        ?: ReviewTab.ALL.pos)
            }
        })
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        if (::loadingIndicatorUtil.isInitialized) loadingIndicatorUtil.hide()
    }

    fun setProductId(productId: Long) {
        mViewModel.productId = productId
        if (productId > 0) {
            mViewModel.getProductReviewSummary()
            mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, ReviewTab.ALL.pos)
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