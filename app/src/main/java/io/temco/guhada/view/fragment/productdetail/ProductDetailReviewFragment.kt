package io.temco.guhada.view.fragment.productdetail

import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.ProductDetailReviewViewModel
import io.temco.guhada.databinding.LayoutProductdetailReviewBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class ProductDetailReviewFragment : BaseFragment<LayoutProductdetailReviewBinding>() {
    private lateinit var mViewModel: ProductDetailReviewViewModel
    override fun getBaseTag(): String = ProductDetailReviewFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_review
    override fun init() {

    }
}