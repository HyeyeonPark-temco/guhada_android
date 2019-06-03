package io.temco.guhada.view.adapter

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
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.view.adapter.base.BaseViewHolder

class ProductDetailOptionAttrAdapter(val viewModel: ProductDetailViewModel, var option: Product.Option) : RecyclerView.Adapter<ProductDetailOptionAttrAdapter.Holder>() {
    var list: List<String> = ArrayList()
    private var prevSelectedPos: Int = -1
    private var selectedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_optionattr, parent, false))
    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: List<String>?) {
        if (list != null && list.isNotEmpty()) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    inner class Holder(val binding: io.temco.guhada.databinding.ItemProductdetailOptionattrBinding) : BaseViewHolder<io.temco.guhada.databinding.ItemProductdetailOptionattrBinding>(binding.root) {
        fun bind(attr: String) {
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
            if (attr.split("#").size >= 2) {
                binding.imageviewProductdetailOptionattr.setBackgroundColor(Color.parseColor(attr))
                binding.imageviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.textviewProductdetailOptionattr.visibility = View.GONE
            } else {
                binding.textviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.imageviewProductdetailOptionattr.visibility = View.GONE
            }

            binding.framelayoutProductdetailOptionattr.setOnClickListener {
                prevSelectedPos = selectedPos
                selectedPos = adapterPosition
                notifyItemChanged(selectedPos)
                notifyItemChanged(prevSelectedPos)

                // TOTAL PRICE
                viewModel.optionMap[option.type] = selectedPos
                if (viewModel.totalPrice.get() == 0 && viewModel.optionMap.keys.size == viewModel.product.value?.options?.size) {
                    viewModel.totalPrice = ObservableInt(viewModel.product.value?.discountPrice
                            ?: viewModel.product.value?.sellPrice ?: 0)
                    viewModel.notifyPropertyChanged(BR.totalPrice)
                }
            }

            binding.attr = attr
            binding.executePendingBindings()
        }
    }
}