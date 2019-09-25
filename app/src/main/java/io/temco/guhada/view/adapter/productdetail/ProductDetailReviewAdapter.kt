package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.review.ReviewResponseContent
import io.temco.guhada.databinding.ItemProductdetailReviewBinding
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailReviewAdapter : RecyclerView.Adapter<ProductDetailReviewAdapter.Holder>() {
    private var list: MutableList<ReviewResponseContent> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemProductdetailReviewBinding>(LayoutInflater.from(parent.context), R.layout.item_productdetail_review, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<ReviewResponseContent>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addItems(list: MutableList<ReviewResponseContent>) {
        val rangeStart = this.list.size
        this.list.addAll(list)
        notifyItemRangeChanged(rangeStart, this.list.size)
    }

    inner class Holder(mBinding: ItemProductdetailReviewBinding) : BaseViewHolder<ItemProductdetailReviewBinding>(mBinding.root) {
        fun bind(reviewContent: ReviewResponseContent) {
            if (adapterPosition == list.size - 1) mBinding.viewProductdetailreviewLine.visibility = View.GONE
            mBinding.reviewContent = reviewContent

            // 리뷰 사진 view pager
            val list = reviewContent.reviewPhotos
            if (list != null) {
                mBinding.viewpagerProductdetailReviewfiles.adapter = ImagePagerAdapter().apply {
                    Observable.fromIterable(list).map {
                        Product.Image().apply { this.url = it.reviewPhotoUrl }
                    }.subscribe {
                        this.list.add(it)
                    }
                }

                mBinding.viewpagerProductdetailReviewfiles.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        mBinding.textviewProductdetailReviewfilespos.text = (position + 1).toString()
                        mBinding.executePendingBindings()
                    }
                })
                mBinding.textviewProductdetailReviewfilescount.text = list.size.toString()
            }

            mBinding.framelayoutProductdetailReviewfiles.visibility = if (list?.isEmpty() != false) View.GONE else View.VISIBLE
            mBinding.linearlayoutProductdetailReviewfilescount.visibility = if (list?.size ?: 0 > 1) View.VISIBLE else View.GONE

            mBinding.executePendingBindings()
        }
    }
}