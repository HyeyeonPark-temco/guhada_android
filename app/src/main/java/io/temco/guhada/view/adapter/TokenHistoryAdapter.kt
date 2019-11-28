package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.TokenActionType
import io.temco.guhada.data.model.blockchain.TokenHistory
import io.temco.guhada.databinding.ItemTokenhistoryBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 토큰 히스토리 리스트 Adapter
 * @author Hyeyeon Park
 * @since 2019.11.28
 */
class TokenHistoryAdapter : RecyclerView.Adapter<TokenHistoryAdapter.Holder>() {
    var mList = mutableListOf<TokenHistory.TokenItemResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_tokenhistory, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(mList[position])

    fun addItems(items: MutableList<TokenHistory.TokenItemResponse>) {
        val startPos = mList.size
        mList.addAll(items)
        this.notifyItemChanged(startPos, items.size)
    }

    fun clear(){
        mList.clear()
        this.notifyDataSetChanged()
    }

    inner class Holder(binding: ItemTokenhistoryBinding) : BaseViewHolder<ItemTokenhistoryBinding>(binding.root) {
        fun bind(item: TokenHistory.TokenItemResponse) {
            mBinding.item = item
            mBinding.executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("tokenTextColor")
        fun TextView.bindTextColor(type: String) {
            val dueToColor = BaseApplication.getInstance().resources.getColor(R.color.warm_grey_two)
            val saveColor = BaseApplication.getInstance().resources.getColor(R.color.common_blue_purple)
            val changeColor = BaseApplication.getInstance().resources.getColor(R.color.brick)

            when (type) {
                TokenActionType.SAVE.type -> this.setTextColor(saveColor)
                TokenActionType.CHANGE_POINT.type -> this.setTextColor(changeColor)
                else -> this.setTextColor(dueToColor)
            }
        }
    }
}