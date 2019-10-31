package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.databinding.ItemProductdetailReviewscorecontentBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailReviewGraphScoreAdapter : RecyclerView.Adapter<ProductDetailReviewGraphScoreAdapter.Holder>() {
    var mMax = 0
    var mBiggest = 0
    var mList = mutableListOf<ReviewSummary.SatisfactionContent>()
        set(value) {
            field = value

            for (item in value)
                if (item.count > mBiggest) mBiggest = item.count
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_reviewscorecontent, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemProductdetailReviewscorecontentBinding) : BaseViewHolder<ItemProductdetailReviewscorecontentBinding>(binding.root) {
        fun bind(satisfaction: ReviewSummary.SatisfactionContent) {
            mBinding.isBiggest = satisfaction.count >= mBiggest
            mBinding.max = this@ProductDetailReviewGraphScoreAdapter.mMax
            mBinding.reviewSatisfaction = satisfaction
        }
    }
}