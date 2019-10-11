package io.temco.guhada.data.viewmodel.productdetail

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.review.ReviewResponse
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment

class ProductDetailReviewViewModel : BaseObservableViewModel() {
    lateinit var listener: ProductDetailReviewFragment.OnProductDetailReviewListener
    var productId: Long = 0
    var reviewPage = 0
    val reviewSize = 2
    var reviewSummary: ReviewSummary = ReviewSummary()
        @Bindable
        get() = field
    var reviewResponse: ReviewResponse = ReviewResponse()
        @Bindable
        get() = field
    var emptyVisibility: ObservableInt = ObservableInt(View.VISIBLE)
        @Bindable
        get() = field

    var mSelectedTabPos = ProductDetailReviewFragment.ReviewTab.ALL.pos
    var mSelectedRating: String? = null
    var mSelectedSorting: String? = null
    var mTabSelectBlock = true
    var mRatingSpinnerBlock = true
    var mSortingSpinnerBlock = true

    fun getProductReviewSummary() {
        if(productId > 0)
            UserServer.getProductReviewSummary(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            try {
                                this.reviewSummary = it.data as ReviewSummary
                                if(CustomLog.flag)CustomLog.L("getProductReviewSummary","reviewSummary",reviewSummary)
                                notifyPropertyChanged(BR.reviewSummary)

                                if (::listener.isInitialized)
                                    listener.notifySummary(reviewSummary.averageReviewsRating)
                                if (::listener.isInitialized)
                                    listener.notifyTotalCount(reviewSummary.totalReviewsCount)
                            } catch (e: Exception) {
                                if (CustomLog.flag) CustomLog.E(e)
                            }
                        },
                        failedTask = {
                            listener.showMessage(it.message)
                        },
                        dataNotFoundTask = {

                        })
            }, productId)
    }

    fun getProductReview(page: Int, size: Int, tabPos: Int, rating: String? = null, sorting: String? = ProductDetailReviewFragment.ReviewSorting.CREATED_DESC.value) {
        mSelectedTabPos = tabPos

        if (reviewResponse.last) {
            listener.showMessage(BaseApplication.getInstance().getString(R.string.productdetail_review_message_lastitem))
        } else {
            val resultTask: (success: Boolean, o: Any) -> Unit = { success: Boolean, o: Any? ->
                if (success) {
                    if (o != null) {
                        val model = o as BaseModel<*>
                        this.reviewResponse = model.data as ReviewResponse
                        emptyVisibility = if (reviewResponse.content.isNullOrEmpty()) ObservableInt(View.VISIBLE)
                        else ObservableInt(View.GONE)

                        notifyPropertyChanged(BR.reviewResponse)
                        notifyPropertyChanged(BR.emptyVisibility)
                    }
                } else {
                    this.reviewResponse = ReviewResponse()
                    emptyVisibility = ObservableInt(View.VISIBLE)

                    notifyPropertyChanged(BR.reviewResponse)
                    notifyPropertyChanged(BR.emptyVisibility)
                }

                if (::listener.isInitialized) listener.hideLoadingIndicator()
            }

            if (rating == null && sorting == null) { // 최신순 & 전체 평점
                when (tabPos) {
                    ProductDetailReviewFragment.ReviewTab.ALL.pos -> getAllReview(page, size, resultTask)
                    ProductDetailReviewFragment.ReviewTab.PHOTO.pos -> getPhotoReview(page, size, resultTask)
                    ProductDetailReviewFragment.ReviewTab.SIZE.pos -> getSizeReview(page, size, resultTask)
                }
            } else if (rating != null && sorting == null) {
                mSelectedRating = rating
                getAllReviewWithRating(page, size, mSelectedRating
                        ?: ProductDetailReviewFragment.ReviewRating.ALL.value, resultTask)
            } else if (rating == null && sorting != null) {
                mSelectedSorting = sorting

                when (tabPos) {
                    ProductDetailReviewFragment.ReviewTab.ALL.pos -> getAllReviewWithSorting(page, size, mSelectedSorting
                            ?: ProductDetailReviewFragment.ReviewSorting.CREATED_DESC.value, resultTask)
                    ProductDetailReviewFragment.ReviewTab.PHOTO.pos -> getPhotoReviewWithSorting(page, size, mSelectedSorting
                            ?: ProductDetailReviewFragment.ReviewSorting.CREATED_DESC.value, resultTask)
                    ProductDetailReviewFragment.ReviewTab.SIZE.pos -> getSizeReviewWithSorting(page, size, mSelectedSorting
                            ?: ProductDetailReviewFragment.ReviewSorting.CREATED_DESC.value, resultTask)
                }
            }
        }
    }

    // 좋아요 여부 조회
    fun getBookMarks(target: String, successTask: (bookMark: BookMark) -> Unit) {
        ServerCallbackUtil.callWithToken(task = {
            val accessToken = it.split("Bearer")[1]
            val userId = JWT(accessToken).getClaim("userId").asInt()
            if (userId != null)
                UserServer.getBookMarkWithoutTargetIdAndTarget(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = { model ->
                                successTask(model.data as BookMark)
                            })
                }, userId = userId, accessToken = it)
        }, invalidTokenTask = {})
    }

    // 좋아요 등록
    fun saveLike(reviewId: Long, successTask: () -> Unit) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            val bookMark = BookMarkResponse(target = BookMarkTarget.REVIEW.target, targetId = reviewId)
            UserServer.saveBookMark(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { successTask() })
            }, accessToken = accessToken, response = bookMark.getProductBookMarkRespose())
        })
    }

    // 좋아요 삭제
    fun deleteLike(reviewId: Long, successTask: () -> Unit) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            UserServer.deleteBookMark(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { successTask() })
            }, accessToken = accessToken, targetId = reviewId, target = BookMarkTarget.REVIEW.target)
        })
    }

    private fun getAllReview(page: Int, size: Int, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductReview(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size)
    }

    private fun getAllReviewWithRating(page: Int, size: Int, rating: String, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductReviewWithRating(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size, rating = rating)
    }

    private fun getAllReviewWithSorting(page: Int, size: Int, sorting: String, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductReviewWithSorting(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size, sorting = sorting)
    }

    private fun getPhotoReview(page: Int, size: Int, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductPhotoReview(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size)
    }

    private fun getPhotoReviewWithSorting(page: Int, size: Int, sorting: String, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductPhotoReviewWithSorting(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size, sorting = sorting)
    }

    private fun getSizeReview(page: Int, size: Int, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductSizeReview(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size)
    }

    private fun getSizeReviewWithSorting(page: Int, size: Int, sorting: String, resultTask: (success: Boolean, o: Any) -> Unit) {
        if (productId > 0)
            UserServer.getProductSizeReviewWithSorting(OnServerListener { success, o ->
                resultTask(success, o)
            }, productId, page, size, sorting = sorting)
    }


    fun onClickMoreReview() {
        listener.showLoadingIndicator { getProductReview(++reviewPage, reviewSize, mSelectedTabPos, rating = mSelectedRating, sorting = mSelectedSorting) }
    }

    // TODO 첫 리뷰 작성하기 버튼
    fun onClickWriteReview() {
        listener.onClickWriteReview()
    }

}