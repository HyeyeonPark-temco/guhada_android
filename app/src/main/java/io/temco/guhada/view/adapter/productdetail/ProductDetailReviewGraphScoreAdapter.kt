package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.databinding.ItemProductdetailReviewscorecontentBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품상세-상품 리뷰 그래프 adapter
 * @author Hyeyeon Park
 * @since 2019.10.31
 */
class ProductDetailReviewGraphScoreAdapter : RecyclerView.Adapter<ProductDetailReviewGraphScoreAdapter.Holder>() {
    var mMax = 0
    var mBiggestCount = 0
    var mList = mutableListOf<ReviewSummary.SatisfactionContent>()
        set(value) {
            field = value

            if(mOriginList.isEmpty())
                mOriginList = value

            if(mBiggestCount == 0)
                for (item in value)
                    if (item.count > mBiggestCount) mBiggestCount = item.count

            if(mDisplayList.isEmpty())
                for(item in value)
                    if (item.count >= mBiggestCount) mDisplayList.add(item)
        }
    var mOriginList = mutableListOf<ReviewSummary.SatisfactionContent>()
    var mDisplayList = mutableListOf<ReviewSummary.SatisfactionContent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_reviewscorecontent, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setBiggest() {
        this.mList = mDisplayList
        this.notifyDataSetChanged()
    }

    fun setAll(){
        this.mList = mOriginList
        this.notifyDataSetChanged()
    }

    inner class Holder(binding: ItemProductdetailReviewscorecontentBinding) : BaseViewHolder<ItemProductdetailReviewscorecontentBinding>(binding.root) {
        fun bind(satisfaction: ReviewSummary.SatisfactionContent) {
            mBinding.isBiggest = satisfaction.count >= mBiggestCount
            mBinding.max = this@ProductDetailReviewGraphScoreAdapter.mMax
            mBinding.reviewSatisfaction = satisfaction
        }
    }
}