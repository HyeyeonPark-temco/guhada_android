package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.databinding.ItemProductdetailReviewscoreBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailReviewScoreAdapter : RecyclerView.Adapter<ProductDetailReviewScoreAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemProductdetailReviewscoreBinding>(LayoutInflater.from(parent.context), R.layout.item_productdetail_reviewscore, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = 0

    override fun onBindViewHolder(holder: Holder, position: Int) {
    }

    inner class Holder(val binding: ItemProductdetailReviewscoreBinding) : BaseViewHolder<ItemProductdetailReviewscoreBinding>(binding.root) {

    }
}