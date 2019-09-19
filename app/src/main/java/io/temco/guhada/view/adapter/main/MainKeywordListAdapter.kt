package io.temco.guhada.view.adapter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.main.Keyword
import io.temco.guhada.databinding.ItemMainlistKeywordBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 메인화면 키워드 Adapter
 * @author park jungho
 * @since 2019.09.19
 */
class MainKeywordListAdapter : RecyclerView.Adapter<MainKeywordListAdapter.Holder>() {
    private var rightMarginVlu = -1
    private var lastRightMarginVlu = -1

    var mList: List<Keyword> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mainlist_keyword, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: List<Keyword>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemMainlistKeywordBinding) : BaseViewHolder<ItemMainlistKeywordBinding>(binding.root) {
        fun bind(key: Keyword) {
            setSpacing()
            mBinding.title = "#"+key.keyword
            mBinding.imgUrl = key.url
            mBinding.executePendingBindings()
        }

        private fun setSpacing() {
            if(rightMarginVlu == -1){
                rightMarginVlu = CommonViewUtil.dipToPixel(itemView.context,6)
                lastRightMarginVlu = CommonViewUtil.dipToPixel(itemView.context,10)
                if(CustomLog.flag)CustomLog.L("MainKeywordListAdapter","rightMarginVlu",rightMarginVlu,"lastRightMarginVlu",lastRightMarginVlu)
            }
            (mBinding.linearlayoutContents.layoutParams as ViewGroup.MarginLayoutParams).apply {
                leftMargin = if(adapterPosition == 0) lastRightMarginVlu
                else 0
                rightMargin = if (adapterPosition < mList.size - 1) rightMarginVlu
                else lastRightMarginVlu
                if(CustomLog.flag)CustomLog.L("MainKeywordListAdapter","leftMargin",leftMargin,"rightMargin",rightMargin)
            }.let {
                mBinding.linearlayoutContents.layoutParams = it
            }
        }

    }
}