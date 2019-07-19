package io.temco.guhada.view.adapter.productdetail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.option.Option
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.viewmodel.ProductDetailMenuViewModel
import io.temco.guhada.databinding.ItemProductdetailOptionattrBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailOptionAttrAdapter(val viewModel: ProductDetailMenuViewModel, var option: Option) : RecyclerView.Adapter<ProductDetailOptionAttrAdapter.Holder>() {
    var list: List<OptionAttr> = ArrayList()
    private var prevSelectedPos: Int = -1
    private var selectedPos: Int = -1
    private lateinit var mHolder: Holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mHolder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_optionattr, parent, false))
        return mHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position], selectedPos)
    }

    fun setItems(list: List<OptionAttr>?) {
        if (list != null && list.isNotEmpty()) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    inner class Holder(val binding: io.temco.guhada.databinding.ItemProductdetailOptionattrBinding) : BaseViewHolder<ItemProductdetailOptionattrBinding>(binding.root) {
        fun bind(optionAttr: OptionAttr, selectedPos: Int) {
            // BORDER
            if (adapterPosition == selectedPos) {
                binding.framelayoutProductdetailOptionattr.background = BaseApplication.getInstance().applicationContext.resources.getDrawable(R.drawable.border_all_purple_2dp)
                BaseApplication.getInstance().applicationContext.resources.let {
                    val padding = (2 * it.displayMetrics.density + 0.5).toInt()
                    binding.framelayoutProductdetailOptionattr.setPadding(padding)
                }

            } else {
                binding.framelayoutProductdetailOptionattr.background = BaseApplication.getInstance().applicationContext.resources.getDrawable(R.drawable.border_all_whitethree)
                BaseApplication.getInstance().applicationContext.resources.let {
                    val padding = (1 * it.displayMetrics.density + 0.5).toInt()
                    binding.framelayoutProductdetailOptionattr.setPadding(padding)
                }
            }

            // CONTENT
            if (optionAttr.rgb.isNotBlank()) {
                binding.imageviewProductdetailOptionattr.setBackgroundColor(Color.parseColor(optionAttr.rgb))
                binding.imageviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.textviewProductdetailOptionattr.visibility = View.GONE
            } else {
                binding.textviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.imageviewProductdetailOptionattr.visibility = View.GONE
            }

            binding.framelayoutProductdetailOptionattr.setOnClickListener {
                prevSelectedPos = this@ProductDetailOptionAttrAdapter.selectedPos
                this@ProductDetailOptionAttrAdapter.selectedPos = adapterPosition

                // SELECT ATTR
//                selectListener.onClickAttr(prevSelectedPos, this@ProductDetailOptionAttrAdapter.selectedPos)
                viewModel.onSelectAttr(optionAttr, option.type, adapterPosition)
                notifyItemChanged(this@ProductDetailOptionAttrAdapter.selectedPos)
                notifyItemChanged(prevSelectedPos)

                // TOTAL PRICE
                if (viewModel.totalPrice.get() == 0 && viewModel.optionMap.keys.size == viewModel.product.options?.size) {
                    viewModel.totalPrice = ObservableInt(viewModel.product.discountPrice)
                    viewModel.notifyPropertyChanged(BR.totalPrice)
                }

                // EXTRA PRICE
                viewModel.getExtraPrice()
            }

            binding.optionAttr = optionAttr
            binding.executePendingBindings()
        }
    }

}