package io.temco.guhada.view.fragment.productdetail

import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.ReviewSummary
import io.temco.guhada.data.viewmodel.ProductDetailReviewViewModel
import io.temco.guhada.databinding.LayoutProductdetailReviewBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class ProductDetailReviewFragment : BaseFragment<LayoutProductdetailReviewBinding>() {
    private var mViewModel: ProductDetailReviewViewModel = ProductDetailReviewViewModel()
    override fun getBaseTag(): String = ProductDetailReviewFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_review
    override fun init() {
        mViewModel.listener = object : OnProductDetailReviewListener {
            override fun showMessage(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    fun setProductId(productId: Long) {
        mViewModel.productId = productId
        mViewModel.getProductReviewSummary()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("productReviewSummary")
        fun RecyclerView.bindProductReviewSummary(summary: ReviewSummary) {

        }
    }

    interface OnProductDetailReviewListener {
        fun showMessage(message: String)
    }
}