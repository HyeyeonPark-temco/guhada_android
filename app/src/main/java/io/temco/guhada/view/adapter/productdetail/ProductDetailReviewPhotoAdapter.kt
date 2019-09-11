package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.databinding.ItemImagepagerBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 리뷰-사진 list adpater
 * @author Hyeyeon Park
 * @since 2019.09.09
 */
class ProductDetailReviewPhotoAdapter : RecyclerView.Adapter<ProductDetailReviewPhotoAdapter.Holder>() {
    var mList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.rootView.context), R.layout.item_imagepager, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemImagepagerBinding) : BaseViewHolder<ItemImagepagerBinding>(binding.root) {
        fun bind(url: String) {
            mBinding.url = url
            mBinding.executePendingBindings()
        }
    }
}