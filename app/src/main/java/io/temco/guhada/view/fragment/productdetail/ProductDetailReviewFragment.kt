package io.temco.guhada.view.fragment.productdetail

import android.view.View
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
import io.temco.guhada.view.adapter.productdetail.ProductDetailReviewGraphScoreAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.ListBottomSheetFragment
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment.Companion.bindProductReviewSummary

/**
 * 상품상세-상품 리뷰
 * @author Hyeyeon Park
 */
class ProductDetailReviewFragment : BaseFragment<LayoutProductdetailReviewBinding>() {

    enum class ReviewRating(val pos: Int, val value: String?, val label: String) {
        ALL(0, null, "전체 평점"),
        FIVE(1, "FIVE", "5점 만"),
        FOUR(2, "FOUR", "4점 만"),
        THREE(3, "THREE", "3점 만"),
        TWO(4, "TWO", "2점 만"),
        ONE(5, "ONE", "1점 만"),
    }

    enum class ReviewSorting(val pos: Int, val value: String, val label: String) {
        CREATED_DESC(0, "created_at,desc", "최신 순"),
        RATING_DESC(1, "product_rating,desc", "평점 높은 순"),
        RATING_ASC(2, "product_rating,asc", "평점 낮은 순")
    }

    enum class ReviewTab(val pos: Int) {
        ALL(0),
        PHOTO(1),
        SIZE(2),
        COMMENT(3)
    }

    var productId = 0L
    private lateinit var loadingIndicatorUtil: LoadingIndicatorUtil
    lateinit var mViewModel: ProductDetailReviewViewModel
    lateinit var notifyTotalCount: (totalReviewsCount: Int) -> Unit
    lateinit var notifySummary: (averageReviewsRating: Float) -> Unit

    override fun getBaseTag(): String = ProductDetailReviewFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_review
    override fun init() {
        initViewModel()
        initRatingSpinner()
        initSortingSpinner()
        initTab()

        mViewModel.getProductReviewSummary()
        mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = ReviewTab.ALL.pos)
        mViewModel.mTabSelectBlock = false

        mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply {
            this.list = arrayListOf()
            this.mViewModel = this@ProductDetailReviewFragment.mViewModel
        }
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = ProductDetailReviewViewModel().apply { this.productId = this@ProductDetailReviewFragment.productId }
        mViewModel.listener = object : OnProductDetailReviewListener {
            override fun showMessage(message: String) = ToastUtil.showMessage(message)

            override fun hideLoadingIndicator() = this@ProductDetailReviewFragment.hideLoadingIndicator()

            override fun notifySummary(averageReviewsRating: Float) = this@ProductDetailReviewFragment.notifySummary(averageReviewsRating)

            override fun notifyTotalCount(totalReviewsCount: Int) = this@ProductDetailReviewFragment.notifyTotalCount(totalReviewsCount)

            override fun showLoadingIndicator(task: () -> Unit) {
                this@ProductDetailReviewFragment.showLoadingIndicator()
                task()
            }

            override fun onClickWriteReview() {
                if (CommonUtil.checkToken()) {
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, MyPageTabType.REVIEW.ordinal, true)
                    (context as ProductFragmentDetailActivity).setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE)
                    (context as ProductFragmentDetailActivity).finish()
                } else {
                    CustomMessageDialog(message = BaseApplication.getInstance().getString(R.string.login_message_requiredlogin),
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.startLoginPage(context as AppCompatActivity)
                            }).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = baseTag)
                }
            }
        }
    }

    private fun initTab() {
        mBinding.tablayoutProductdetailReview.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (!mViewModel.mTabSelectBlock) {
                    // 정렬 스피너 초기화
                    mViewModel.mSelectedSorting = ReviewSorting.CREATED_DESC.value
                    mBinding.textviewReviewSorting.text = ReviewSorting.CREATED_DESC.label

                    // 평점 스피너 초기화
                    mViewModel.mSelectedRating = ReviewRating.ALL.value
                    mBinding.textviewReviewRating.text = ReviewRating.ALL.label

                    resetReview()
                    mViewModel.mSelectedTabPos = tab?.position ?: ReviewTab.ALL.pos
                    mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, mViewModel.mSelectedTabPos)
                }
            }
        })
    }

    private fun initRatingSpinner() {
        val onClickRatingListener = View.OnClickListener {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mTitle = "리뷰평점"
                this.mList = mutableListOf(ReviewRating.ALL.label, ReviewRating.FIVE.label, ReviewRating.FOUR.label, ReviewRating.THREE.label, ReviewRating.TWO.label, ReviewRating.ONE.label)
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        mViewModel.mTabSelectBlock = true
                        resetReview()
                        mBinding.tablayoutProductdetailReview.getTabAt(0)?.select()
                        mViewModel.mTabSelectBlock = false

                        val rating = when (position) {
                            ReviewRating.ALL.pos -> ReviewRating.ALL
                            ReviewRating.FIVE.pos -> ReviewRating.FIVE
                            ReviewRating.FOUR.pos -> ReviewRating.FOUR
                            ReviewRating.THREE.pos -> ReviewRating.THREE
                            ReviewRating.TWO.pos -> ReviewRating.TWO
                            ReviewRating.ONE.pos -> ReviewRating.ONE
                            else -> ReviewRating.ALL
                        }

                        mViewModel.mSelectedTabPos = ReviewTab.ALL.pos  // 평점 필터는 [전체리뷰]만 가능
                        mViewModel.mSelectedRating = rating.value
                        mBinding.textviewReviewRating.text = rating.label
                        mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = mViewModel.mSelectedTabPos)
                        this@apply.dismiss()
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }
            if (fragmentManager != null) bottomSheet.show(fragmentManager!!, baseTag)
        }

        mBinding.imageviewReviewRating.setOnClickListener(onClickRatingListener)
        mBinding.textviewReviewRating.setOnClickListener(onClickRatingListener)
    }

    private fun initSortingSpinner() {
        val onClickSortingListener = View.OnClickListener {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mTitle = "리뷰순서"
                this.mList = mutableListOf(ReviewSorting.CREATED_DESC.label, ReviewSorting.RATING_DESC.label, ReviewSorting.RATING_ASC.label)
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        resetReview()

                        val sorting = when (position) {
                            ReviewSorting.CREATED_DESC.pos -> ReviewSorting.CREATED_DESC
                            ReviewSorting.RATING_DESC.pos -> ReviewSorting.RATING_DESC
                            ReviewSorting.RATING_ASC.pos -> ReviewSorting.RATING_ASC
                            else -> ReviewSorting.CREATED_DESC
                        }

                        mViewModel.mSelectedSorting = sorting.value
                        mBinding.textviewReviewSorting.text = sorting.label
                        mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = mViewModel.mSelectedTabPos)
                        this@apply.dismiss()
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }
            if (fragmentManager != null) bottomSheet.show(fragmentManager!!, baseTag)
        }

        mBinding.imageviewReviewSorting.setOnClickListener(onClickSortingListener)
        mBinding.textviewReviewSorting.setOnClickListener(onClickSortingListener)
    }

    private fun resetReview() {
        mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply {
            this.list = arrayListOf()
            this.mViewModel = this@ProductDetailReviewFragment.mViewModel
        }
        mViewModel.reviewResponse = ReviewResponse()
        mViewModel.reviewPage = 0
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        if (::loadingIndicatorUtil.isInitialized) loadingIndicatorUtil.hide()
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
            if (summary.totalReviewsCount > 0)
                this.adapter = ProductDetailReviewGraphScoreAdapter().apply {
                    this.mList = when (this@bindProductReviewSummary.id) {
                        R.id.recyclerview_productdetail_reviewgraph1 -> summary.satisfaction.sizes.toMutableList()
                        R.id.recyclerview_productdetail_reviewgraph2 -> summary.satisfaction.colors.toMutableList()
                        R.id.recyclerview_productdetail_reviewgraph3 -> summary.satisfaction.lengths.toMutableList()
                        else -> mutableListOf()
                    }
                    this.mMax = summary.totalReviewsCount
                }
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
        fun notifyTotalCount(totalReviewsCount: Int)
        fun notifySummary(averageReviewsRating: Float)
        fun onClickWriteReview()
    }
}