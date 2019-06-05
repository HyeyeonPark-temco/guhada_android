package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.databinding.ItemProductdetailOptionBinding
import io.temco.guhada.view.adapter.base.BaseViewHolder

class ProductDetailOptionAdapter(val viewModel: ProductDetailViewModel, var selectListener: ProductDetailOptionAttrAdapter.OnSelectAttrListener) : RecyclerView.Adapter<ProductDetailOptionAdapter.Holder>() {
    var list: List<Product.Option> = ArrayList()
    lateinit var mBinding: ItemProductdetailOptionBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_option, parent, false)
        return Holder(mBinding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: List<Product.Option>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: io.temco.guhada.databinding.ItemProductdetailOptionBinding) : BaseViewHolder<io.temco.guhada.databinding.ItemProductdetailOptionBinding>(binding.root) {
        fun bind(option: Product.Option) {
            if (adapterPosition > 0) {
                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = 50
                }.let {
                    binding.linearlayoutProductdetailOption.layoutParams = it
                }
            }
            binding.viewModel = viewModel
            binding.option = option
            binding.recyclerviewProductdetailOptionattr.adapter = ProductDetailOptionAttrAdapter(viewModel, option, selectListener)
            binding.recyclerviewProductdetailOptionattr.layoutManager = LinearLayoutManager(BaseApplication.getInstance().applicationContext, RecyclerView.HORIZONTAL, false)
            binding.executePendingBindings()
        }
    }

    fun notifyAttrAdapter() {
        mBinding.recyclerviewProductdetailOptionattr.adapter?.notifyDataSetChanged()
    }

    fun setItemSelected(optionAttr: OptionAttr) {
        if (::mBinding.isInitialized && optionAttr.rgb.isNotBlank()) {
            viewModel.colorName = ObservableField(optionAttr.name)
            viewModel.notifyPropertyChanged(BR.colorName)
            //(mBinding.recyclerviewProductdetailOptionattr.adapter as ProductDetailOptionAttrAdapter).setSelectedItemPos(prevSelectedPos, selectedPos)
            //(mBinding.recyclerviewProductdetailOptionattr.adapter as ProductDetailOptionAttrAdapter).setSelectedItemPos(prevSelectedPos, selectedPos)
        }
    }

    class OptionAttr {
        var rgb: String = ""
        var name: String = ""
    }
}
