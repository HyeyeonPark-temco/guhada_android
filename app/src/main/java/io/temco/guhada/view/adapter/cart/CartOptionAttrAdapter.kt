package io.temco.guhada.view.adapter.cart

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.viewmodel.cart.CartViewModel
import io.temco.guhada.databinding.ItemProductdetailOptionattrBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 장바구니 상품 옵션 속성 리스트 Adapter
 * @author Hyeyeon Park
 */
class CartOptionAttrAdapter(val mViewModel: CartViewModel) : RecyclerView.Adapter<CartOptionAttrAdapter.Holder>() {
    private var items: List<OptionAttr> = ArrayList()
    private var prevSelectedPos: Int = -1
    private var selectedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_optionattr, parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])

    fun setItems(items: MutableList<OptionAttr>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemProductdetailOptionattrBinding) : BaseViewHolder<ItemProductdetailOptionattrBinding>(binding.root) {
        fun bind(optionAttr: OptionAttr) {
            // BORDER
            binding.optionAttr = optionAttr

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
            if (optionAttr.rgb.isNotBlank() && optionAttr.rgb != "null") {
                binding.imageviewProductdetailOptionattr.setBackgroundColor(Color.parseColor(optionAttr.rgb))
                binding.imageviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.textviewProductdetailOptionattr.visibility = View.GONE
            } else {
                binding.textviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.imageviewProductdetailOptionattr.visibility = View.GONE
            }

            // CLICK EVENT
            binding.framelayoutProductdetailOptionattr.setOnClickListener {
                prevSelectedPos = this@CartOptionAttrAdapter.selectedPos
                this@CartOptionAttrAdapter.selectedPos = adapterPosition

                notifyItemChanged(this@CartOptionAttrAdapter.selectedPos)
                notifyItemChanged(prevSelectedPos)

                mViewModel.onSelectAttr(optionAttr)
            }

            binding.executePendingBindings()
        }

    }

}