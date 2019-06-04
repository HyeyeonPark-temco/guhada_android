package io.temco.guhada.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.databinding.ItemProductdetailClaimBinding
import io.temco.guhada.view.adapter.base.BaseViewHolder

class ClaimAdapter : RecyclerView.Adapter<ClaimAdapter.Holder>() {
    private var list: MutableList<ClaimResponse.Claim> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_claim, parent, false))
    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.e("ㅇㅇㅇ", (holder.binding.textviewProductdetailQnaReplydate.layout == null).toString())
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<ClaimResponse.Claim>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addItems(list: MutableList<ClaimResponse.Claim>) {
        val rangeStart = this.list.size
        this.list.addAll(list)
        notifyItemRangeChanged(rangeStart, this.list.size)
    }

    inner class Holder(val binding: ItemProductdetailClaimBinding) : BaseViewHolder<ItemProductdetailClaimBinding>(binding.root) {
        fun bind(item: ClaimResponse.Claim) {
            binding.claim = item
            binding.isReplyClosed = true

            if (item.private) {
                binding.replyArrowVisibility = false
            } else {
                if (!item.reply.isNullOrBlank()) {
                    // 답변 완료 -> arrow 표시
                    binding.replyArrowVisibility = true
                } else {
                    // 미답변
                    binding.textviewProductdetailClaimContent.viewTreeObserver.addOnGlobalLayoutListener {
                        if (binding.textviewProductdetailClaimContent.layout != null) {
                            val count = binding.textviewProductdetailClaimContent.layout?.getEllipsisCount(binding.textviewProductdetailClaimContent.lineCount - 1)
                            binding.replyArrowVisibility = count ?: 0 > 0
                        } else {
                            binding.replyArrowVisibility = false
                        }
                    }
                }
            }

            binding.setOnClickReplyArrow {
                binding.isReplyClosed = !binding.isReplyClosed!!

                binding.linearlayoutProductdetailClaim.setBackgroundColor(if (binding.isReplyClosed == true) {
                    binding.root.resources.getColor(R.color.common_white)
                } else {
                    binding.root.resources.getColor(R.color.pale_grey)
                })

                binding.notifyPropertyChanged(BR.isReplyClosed)
                binding.executePendingBindings()
            }

            binding.executePendingBindings()
        }
    }
}