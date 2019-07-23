package io.temco.guhada.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.databinding.ItemProductdetailClaimBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ClaimAdapter : RecyclerView.Adapter<ClaimAdapter.Holder>() {
    private lateinit var context: Context
    private lateinit var parent: ViewGroup
    private var list: MutableList<Claim> = ArrayList()
    lateinit var mBinding: ItemProductdetailClaimBinding
    lateinit var holder: Holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.parent = parent
        this.context = parent.context
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_claim, parent, false)
        holder = Holder(mBinding)
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<Claim>) {
        this.list = list
        if (list.isEmpty()) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeChanged(0, list.size)
        }
    }

    fun addItems(list: MutableList<Claim>) {
        val rangeStart = this.list.size
        this.list.addAll(list)
        notifyItemRangeChanged(rangeStart - 1, this.list.size)
    }

    fun clearItems() {
        this.list.clear()
    }

    inner class Holder(val binding: ItemProductdetailClaimBinding) : BaseViewHolder<ItemProductdetailClaimBinding>(binding.root) {
        fun bind(item: Claim) {
            binding.isLastItem = adapterPosition == list.size - 1
            binding.claim = item
            binding.isReplyClosed = true
            binding.isArrowVisibilityForPending = false

            binding.textviewProductdetailClaimContent.viewTreeObserver.addOnGlobalLayoutListener {
                if (binding.textviewProductdetailClaimContent.layout != null) {
                    val count = binding.textviewProductdetailClaimContent.layout?.getEllipsisCount(binding.textviewProductdetailClaimContent.lineCount - 1)
                    binding.isEllipsized = count ?: 0 > 0
                    binding.executePendingBindings()
                }
            }

            binding.setOnClickReplyArrow {
                binding.isReplyClosed = !binding.isReplyClosed!!

                if (item.reply == null && binding.isEllipsized == true) {
                    binding.isArrowVisibilityForPending = true
                    binding.notifyPropertyChanged(BR.isArrowVisibilityForPending)
                }

                binding.linearlayoutProductdetailClaim.setBackgroundColor(if (binding.isReplyClosed == true) {
                    binding.root.resources.getColor(R.color.common_white)
                } else {
                    binding.root.resources.getColor(R.color.pale_grey)
                })

                binding.notifyPropertyChanged(BR.isReplyClosed)
            }
        }
    }
}