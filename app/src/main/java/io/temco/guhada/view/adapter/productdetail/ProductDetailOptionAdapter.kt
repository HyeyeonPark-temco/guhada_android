package io.temco.guhada.view.adapter.productdetail

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
import io.temco.guhada.data.model.option.Option
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.databinding.ItemProductdetailOptionBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 상세-옵션 list adapter
 * @author Hyeyeon Park
 */
class ProductDetailOptionAdapter(val viewModel: ProductDetailMenuViewModel) : RecyclerView.Adapter<ProductDetailOptionAdapter.Holder>() {
    var options: List<Option> = ArrayList()
    var mOptionInfoList = mutableListOf<OptionInfo>()
    lateinit var mBinding: ItemProductdetailOptionBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_option, parent, false)
        return Holder(mBinding)
    }

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(options[position])
    }

    fun setItems(list: List<Option>) {
        this.options = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: io.temco.guhada.databinding.ItemProductdetailOptionBinding) : BaseViewHolder<ItemProductdetailOptionBinding>(binding.root) {
        fun bind(option: Option) {
            if (adapterPosition > 0) {
                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = 50
                }.let {
                    binding.linearlayoutProductdetailOption.layoutParams = it
                }
            }

            binding.viewModel = viewModel
            binding.option = option

            binding.recyclerviewProductdetailOptionattr.adapter = ProductDetailOptionAttrAdapter(viewModel, option, adapterPosition).apply {
                if (binding.recyclerviewProductdetailOptionattr.adapter != null) {
                    this.prevSelectedPos = (binding.recyclerviewProductdetailOptionattr.adapter as ProductDetailOptionAttrAdapter).prevSelectedPos
                    this.selectedPos = (binding.recyclerviewProductdetailOptionattr.adapter as ProductDetailOptionAttrAdapter).selectedPos
                }

                this.mAttrSelectedListener = { attr ->
                    // TODO 옵션 선택 시 수량 체크

                }
            }


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
        }
    }

}
