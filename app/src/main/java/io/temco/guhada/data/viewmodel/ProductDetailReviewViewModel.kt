package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.ReviewSummary
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment

class ProductDetailReviewViewModel : BaseObservableViewModel() {
    lateinit var listener: ProductDetailReviewFragment.OnProductDetailReviewListener
    var productId: Long = 0
    var review: ReviewSummary = ReviewSummary()
        @Bindable
        get() = field


    fun getProductReviewSummary() {
        LoginServer.getProductReviewSummary(OnServerListener { success, o ->
            if (success) {
                val model = o as BaseModel<*>
                this.review = model.data as ReviewSummary
                notifyPropertyChanged(BR.review)
            } else {
                if (o != null) {
                    listener.showMessage(o as String)
                } else {
                    listener.showMessage("PRODUCT REVIEW SUMMARY ERROR")
                }
            }
        }, productId)
    }
}