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
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.databinding.ItemProductdetailOptionattrBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 상세-옵션 attribute list adapter
 * @param optionPos 몇번째(row) option 인지 (ProductDetailOptionAdapter의 adapterPosition)
 * @author Hyeyeon Park
 */
class ProductDetailOptionAttrAdapter(val viewModel: ProductDetailMenuViewModel, var option: Option, var optionPos: Int) : RecyclerView.Adapter<ProductDetailOptionAttrAdapter.Holder>() {
    var list: List<OptionAttr> = ArrayList()
    var mAttrSelectedListener: (optionAttr: OptionAttr) -> Unit = {}
     var prevSelectedPos: Int = -1
     var selectedPos: Int = -1
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
            if(this@ProductDetailOptionAttrAdapter.selectedPos > -1){
                if (adapterPosition == selectedPos) {
                   setSelectedBorder()
                } else {
                    setUnselectedBorder()
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

            // 옵션 선택
            binding.framelayoutProductdetailOptionattr.setOnClickListener {
                prevSelectedPos = this@ProductDetailOptionAttrAdapter.selectedPos
                this@ProductDetailOptionAttrAdapter.selectedPos = adapterPosition

                // SELECT ATTR
                viewModel.onSelectAttr(optionAttr, option.type, adapterPosition)
                notifyItemChanged(adapterPosition)
                notifyItemChanged(prevSelectedPos)

                // TOTAL PRICE
                if (viewModel.totalPrice.get() == 0 && viewModel.optionMap.keys.size == viewModel.product.options?.size) {
                    viewModel.totalPrice = ObservableInt(viewModel.product.discountPrice)
                    viewModel.notifyPropertyChanged(BR.totalPrice)
                }

                // EXTRA PRICE
                viewModel.getExtraPrice()

                /////
                mAttrSelectedListener(optionAttr)
            }

            // CHECK SOLD OUT
            //Log.e("ATTR 어댑터", "ATTR NAME: ${optionAttr.name}     ENABLED : ${optionAttr.enabled}    isOptionEnabled: ${isOptionEnabled(optionAttr)} ")

            if (!isOptionEnabled(optionAttr)) {
//                if(selectedPos == adapterPosition){
//                     setUnselectedBorder()
//                    ToastUtil.showMessage("선택된 옵션(${optionAttr.name})은 품절입니다")
//                }

                mBinding.imageviewProductdetailSoldout.bringToFront()
                mBinding.imageviewProductdetailSoldout.visibility = View.VISIBLE
                mBinding.textviewProductdetailOptionattr.setTextColor(binding.root.context.resources.getColor(R.color.warm_grey))
                mBinding.framelayoutProductdetailOptionattr.setOnClickListener {}
            }

            binding.optionAttr = optionAttr
            binding.executePendingBindings()
        }

        private fun isOptionEnabled(optionAttr: OptionAttr): Boolean {
            for (item in option.attributeItems) {
                if (item.name == optionAttr.name) return item.enabled
            }
            return false
        }

        private fun setSelectedBorder(){
            binding.framelayoutProductdetailOptionattr.background = BaseApplication.getInstance().applicationContext.resources.getDrawable(R.drawable.border_all_purple_2dp)
            BaseApplication.getInstance().applicationContext.resources.let {
                val padding = (2 * it.displayMetrics.density + 0.5).toInt()
                binding.framelayoutProductdetailOptionattr.setPadding(padding)
            }
        }
        private fun setUnselectedBorder(){
            binding.framelayoutProductdetailOptionattr.background = BaseApplication.getInstance().applicationContext.resources.getDrawable(R.drawable.border_all_whitethree)
            BaseApplication.getInstance().applicationContext.resources.let {
                val padding = (1 * it.displayMetrics.density + 0.5).toInt()
                binding.framelayoutProductdetailOptionattr.setPadding(padding)
            }
        }

    }

}