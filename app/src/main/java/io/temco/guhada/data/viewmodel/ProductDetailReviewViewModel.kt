package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.ReviewResponse
import io.temco.guhada.data.model.ReviewSummary
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment

class ProductDetailReviewViewModel : BaseObservableViewModel() {
    lateinit var listener: ProductDetailReviewFragment.OnProductDetailReviewListener
    var productId: Long = 0
    var reviewPage = 0
    var reviewSize = 5
    var reviewSummary: ReviewSummary = ReviewSummary()
        @Bindable
        get() = field
    var reviewResponse: ReviewResponse = ReviewResponse()
        @Bindable
        get() = field
    var emptyVisibility: ObservableInt = ObservableInt(View.GONE)
        @Bindable
        get() = field

    fun getProductReviewSummary() {
        LoginServer.getProductReviewSummary(OnServerListener { success, o ->
            if (success) {
                val model = o as BaseModel<*>
                this.reviewSummary = model.data as ReviewSummary
                notifyPropertyChanged(BR.reviewSummary)


             listener.notifySummary(reviewSummary.averageReviewsRating)
            } else {
                if (o != null) {
                    listener.showMessage(o as String)
                } else {
                    listener.showMessage("PRODUCT REVIEW SUMMARY ERROR")
                }
            }
        }, productId)
    }

    fun getProductReview(page: Int, size: Int) {
        if (reviewResponse.last) {
            listener.showMessage("마지막 항목입니다.")
        } else {
            LoginServer.getProductReview(OnServerListener { success, o ->
                if (success) {
                    val model = o as BaseModel<*>
                    this.reviewResponse = model.data as ReviewResponse
                    if (reviewResponse.content.isNullOrEmpty()) emptyVisibility = ObservableInt(View.VISIBLE)

                    notifyPropertyChanged(BR.reviewResponse)
                    notifyPropertyChanged(BR.emptyVisibility)
                    listener.hideLoadingIndicator()
                } else {
                    if (o != null) {
                        listener.showMessage(o as String)
                    } else {
                        listener.showMessage("PRODUCT REVIEW ERROR")
                    }
                }
            }, productId, page, size)
        }
    }

    fun onClickMoreReview(size: Int) {
        listener.showLoadingIndicator { getProductReview(++reviewPage, size) }
    }


}