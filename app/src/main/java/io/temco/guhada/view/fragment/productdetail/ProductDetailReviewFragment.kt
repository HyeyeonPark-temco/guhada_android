package io.temco.guhada.view.fragment.productdetail

import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import io.temco.guhada.view.adapter.CommonNonBoxSpinnerAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailReviewAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 상품상세-상품 리뷰
 * @author Hyeyeon Park
 */
class ProductDetailReviewFragment : BaseFragment<LayoutProductdetailReviewBinding>() {

    enum class ReviewRating(val pos: Int, val value: String, val label: String) {
        ALL(0, "ALL", "전체 평점"),
        FIVE(1, "FIVE", "5점 만"),
        FOUR(2, "FOUR", "4점 만"),
        THREE(3, "THREE", "3점 만"),
        TWO(4, "TWO", "2점 만"),
        ONE(5, "ONE", "1점 만"),
    }

    enum class ReviewSorting(val pos: Int, val value: String, val label: String) {
        CREATED_DESC(0, "created_at,desc", "최신순"),
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
    lateinit var notifySummary: (averageReviewsRating: Float) -> Unit

    override fun getBaseTag(): String = ProductDetailReviewFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_review
    override fun init() {
        initViewModel()
        initRatingSpinner()
        initSortingSpinner()
        initTab()

        mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply { this.list = arrayListOf() }
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()

        mViewModel.getProductReviewSummary()
        // mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, ReviewTab.ALL.pos)
    }

    private fun initViewModel() {
        mViewModel = ProductDetailReviewViewModel().apply { this.productId = this@ProductDetailReviewFragment.productId }
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
                if (!mViewModel.mTabSelectBlock) {
                    mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply { this.list = arrayListOf() }
                    mViewModel.mSelectedTabPos = tab?.position ?: ReviewTab.ALL.pos
                    mViewModel.reviewResponse = ReviewResponse()
                    mViewModel.reviewPage = 0

                    mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, tab?.position
                            ?: ReviewTab.ALL.pos)

                    // 정렬 스피너 초기화
                    mViewModel.mSortingSpinnerBlock = true
                    mBinding.spinnerReviewSorting.setSelection(0)
                    mViewModel.mSortingSpinnerBlock = false

                    // 평점 스피너 초기화
                    mViewModel.mRatingSpinnerBlock = true
                    mBinding.spinnerReviewRating.setSelection(0, true)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1500)
                        mViewModel.mRatingSpinnerBlock = false
                    }
                }
            }
        })
    }

    private fun initRatingSpinner() {
        mBinding.spinnerReviewRating.adapter = CommonNonBoxSpinnerAdapter(mBinding.root.context, R.layout.item_common_spinner2,
                listOf(ReviewRating.ALL.label, ReviewRating.FIVE.label, ReviewRating.FOUR.label, ReviewRating.THREE.label, ReviewRating.TWO.label, ReviewRating.ONE.label))
        mBinding.spinnerReviewRating.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!mViewModel.mRatingSpinnerBlock) {
                    mViewModel.mTabSelectBlock = true
                    mBinding.tablayoutProductdetailReview.getTabAt(0)?.select()
                    mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply { this.list = arrayListOf() }
                    mViewModel.reviewResponse = ReviewResponse()
                    mViewModel.reviewPage = 0

                    // 정렬 스피너 초기화
                    mViewModel.mSortingSpinnerBlock = true
                    mBinding.spinnerReviewSorting.setSelection(0)

                    if (position == ReviewRating.ALL.pos) {
                        mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, ReviewTab.ALL.pos)
                    } else {
                        when (position) {
                            ReviewRating.FIVE.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = ReviewTab.ALL.pos, rating = ReviewRating.FIVE.value, sorting = null)
                            ReviewRating.FOUR.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = ReviewTab.ALL.pos, rating = ReviewRating.FOUR.value, sorting = null)
                            ReviewRating.THREE.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = ReviewTab.ALL.pos, rating = ReviewRating.THREE.value, sorting = null)
                            ReviewRating.TWO.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = ReviewTab.ALL.pos, rating = ReviewRating.TWO.value, sorting = null)
                            ReviewRating.ONE.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = ReviewTab.ALL.pos, rating = ReviewRating.ONE.value, sorting = null)
                        }
                    }
                    mViewModel.mTabSelectBlock = false
                    mViewModel.mSortingSpinnerBlock = false
                }
                mViewModel.mRatingSpinnerBlock = false
            }
        }
//        mBinding.spinnerReviewRating.setSelection(0)
    }

    private fun initSortingSpinner() {
        mBinding.spinnerReviewSorting.adapter = CommonNonBoxSpinnerAdapter(mBinding.root.context, R.layout.item_common_spinner2, listOf(ReviewSorting.CREATED_DESC.label, ReviewSorting.RATING_DESC.label, ReviewSorting.RATING_ASC.label))
        mBinding.spinnerReviewSorting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!mViewModel.mSortingSpinnerBlock) {
                    mViewModel.mSelectedRating = null
                    mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply { this.list = arrayListOf() }
                    mViewModel.reviewResponse = ReviewResponse()
                    mViewModel.reviewPage = 0

                    // 평점 스피너 초기화
                    mViewModel.mRatingSpinnerBlock = true
                    mBinding.spinnerReviewRating.setSelection(0)

                    when (position) {
                        ReviewSorting.CREATED_DESC.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = mViewModel.mSelectedTabPos, sorting = ReviewSorting.CREATED_DESC.value, rating = null)
                        ReviewSorting.RATING_DESC.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = mViewModel.mSelectedTabPos, sorting = ReviewSorting.RATING_DESC.value, rating = null)
                        ReviewSorting.RATING_ASC.pos -> mViewModel.getProductReview(page = mViewModel.reviewPage, size = mViewModel.reviewSize, tabPos = mViewModel.mSelectedTabPos, sorting = ReviewSorting.RATING_ASC.value, rating = null)
                    }
                    mViewModel.mRatingSpinnerBlock = false
                }
            }
        }
//        mBinding.spinnerReviewSorting.setSelection(0)
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        if (::loadingIndicatorUtil.isInitialized) loadingIndicatorUtil.hide()
    }

//    fun setProductId(productId: Long) {
//        mViewModel.productId = productId
//        if (productId > 0) {
//            if (mBinding != null) mBinding.recyclerviewProductdetailReview.adapter = ProductDetailReviewAdapter().apply { this.list = arrayListOf() }
//            mViewModel.getProductReviewSummary()
//            mViewModel.getProductReview(mViewModel.reviewPage, mViewModel.reviewSize, ReviewTab.ALL.pos)
//        }
//    }

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