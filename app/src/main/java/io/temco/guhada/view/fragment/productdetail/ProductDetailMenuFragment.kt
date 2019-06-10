package io.temco.guhada.view.fragment.productdetail

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.ProductDetailMenuViewModel
import io.temco.guhada.view.adapter.ProductDetailOptionAdapter
import io.temco.guhada.view.adapter.ProductDetailOptionAttrAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class ProductDetailMenuFragment(val mViewModel: ProductDetailMenuViewModel) : BaseFragment<io.temco.guhada.databinding.LayoutProductdetailMenuBinding>() {
    override fun getBaseTag(): String = ProductDetailMenuFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_menu

    override fun init() {
        mBinding.recyclerviewProductdetailMenu.adapter = ProductDetailOptionAdapter(mViewModel)
        mBinding.recyclerviewProductdetailMenu.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    fun getProductCount(): Int = mViewModel.productCount.get()
    fun getTotalPrice(): Int = mViewModel.totalPrice.get()
    fun getSelectedOptionCount(): Int = mViewModel.optionMap.keys.size

    companion object {
        @JvmStatic
        @BindingAdapter("productOption")
        fun RecyclerView.bindOption(list: List<Product.Option>?) {
            if (list != null && list.isNotEmpty() && this.adapter != null) {
                (this.adapter as ProductDetailOptionAdapter).setItems(list)
            }
        }

        @JvmStatic
        @BindingAdapter("productOptionAttr")
        fun RecyclerView.bindOptionAttr(option: Product.Option?) {
            if (option != null && this.adapter != null) {
                val attrList: MutableList<ProductDetailOptionAdapter.OptionAttr> = ArrayList()

                if (option.type == "COLOR") {
                    for (i in 0 until option.rgb.size) {
                        ProductDetailOptionAdapter.OptionAttr().apply {
                            rgb = option.rgb[i]
                            name = option.attributes[i]
                        }.let {
                            attrList.add(it)
                        }
                    }
                } else {
                    for (attr in option.attributes) {
                        ProductDetailOptionAdapter.OptionAttr().apply {
                            name = attr
                        }.let {
                            attrList.add(it)
                        }
                    }
                }

                (this.adapter as ProductDetailOptionAttrAdapter).setItems(attrList.toList())
            }
        }

    }
}