package io.temco.guhada.view.adapter.productdetail

import android.util.Log
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
import io.temco.guhada.view.fragment.productdetail.ProductDetailMenuFragment.Companion.bindOptionAttr
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 상세-옵션 list adapter
 * @author Hyeyeon Park
 */
class ProductDetailOptionAdapter(val viewModel: ProductDetailMenuViewModel) : RecyclerView.Adapter<ProductDetailOptionAdapter.Holder>() {
    var options: List<Option> = ArrayList()
    var mOptionInfoList = mutableListOf<OptionInfo>()
    lateinit var mBinding: ItemProductdetailOptionBinding

    ///
    var selectedAttr = mutableMapOf<String, String>()

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
                //                if (binding.recyclerviewProductdetailOptionattr.adapter != null) {
//                    this.prevSelectedPos = (binding.recyclerviewProductdetailOptionattr.adapter as ProductDetailOptionAttrAdapter).prevSelectedPos
//                    this.selectedPos = (binding.recyclerviewProductdetailOptionattr.adapter as ProductDetailOptionAttrAdapter).selectedPos
//                }

                this.mAttrSelectedListener = { attr ->
                    Log.e("선택 옵션", attr.name)

                    selectedAttr[option.label] = attr.name

//                    var optInfos = mutableListOf<OptionInfo>()
//                    for (key in selectedAttr.keys) {
//                        val value = selectedAttr[key]
//
//                        Log.e("선택 옵션", "$key    $value")
//                        if (value != null) {
//                            if (optInfos.isEmpty()) {
//                                for (optionInfo in mOptionInfoList) {
//                                    if (optionInfo.label1 == key && optionInfo.attribute1 == value) {
//                                        optInfos.add(optionInfo)
//                                    } else if (optionInfo.label2 == key && optionInfo.attribute2 == value) {
//                                        optInfos.add(optionInfo)
//                                    } else if (optionInfo.label3 == key && optionInfo.attribute3 == value) {
//                                        optInfos.add(optionInfo)
//                                    }
//                                }
//                            } else {
//                                val tempList = optInfos
//                                optInfos = mutableListOf()
//                                for (optionInfo in tempList) {
//                                    if (optionInfo.label1 == key && optionInfo.attribute1 == value) {
//                                        optInfos.add(optionInfo)
//                                    } else if (optionInfo.label2 == key && optionInfo.attribute2 == value) {
//                                        optInfos.add(optionInfo)
//                                    } else if (optionInfo.label3 == key && optionInfo.attribute3 == value) {
//                                        optInfos.add(optionInfo)
//                                    }
//                                }
//                            }
//                        }
//                    }

                    var stockMap = mutableMapOf<String, Int>()

                    for (info in mOptionInfoList) {
                        Log.e("OPTION INFO", info.toString())

                        if (info.attribute1 == attr.name || info.attribute2 == attr.name || info.attribute3 == attr.name) {
                            val attr1Stock = stockMap[info.attribute2.toString()] ?: 0
                            val attr2Stock = stockMap[info.attribute2.toString()] ?: 0
                            val attr3Stock = stockMap[info.attribute3.toString()] ?: 0

                            stockMap[info.attribute1.toString()] = attr1Stock + info.stock
                            stockMap[info.attribute2.toString()] = attr2Stock + info.stock
                            stockMap[info.attribute3.toString()] = attr3Stock + info.stock
                        }
                    }

                    Log.e("맵", stockMap.toString())

                    //      for (key in stockMap.keys) {
                    for (i in 0 until options.size) {
                        val opt = options[i]
                        for (j in 0 until opt.attributeItems.size) {
                            for (info in mOptionInfoList) {
                                val attrItem = opt.attributeItems[j]
                                if (info.attribute1 == attrItem.name || info.attribute2 == attrItem.name || info.attribute3 == attrItem.name) {
                                    options[i].attributeItems[j].enabled = stockMap[attrItem.name] ?: 0 > 0
                                }
                            }
                        }
                    }
                    //     }

                    Log.e("옵션 리스트2", options.toString())
                    // Log.e("선택선택선택", optInfos.toString())


                    for (i in 0 until options.size) {
                        if (i != adapterPosition)
                            this@ProductDetailOptionAdapter.notifyItemChanged(i)
                    }
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
