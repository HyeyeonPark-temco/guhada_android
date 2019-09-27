package io.temco.guhada.view.adapter.productdetail

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.ReportUserModel
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.review.ReviewResponseContent
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailReviewViewModel
import io.temco.guhada.databinding.ItemProductdetailReviewBinding
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailReviewAdapter : RecyclerView.Adapter<ProductDetailReviewAdapter.Holder>() {
     var list: MutableList<ReviewResponseContent> = mutableListOf()

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

            mBinding.setClickListener {
                if (CommonUtil.checkToken()) {
                    CommonUtil.startReportActivity(itemView.context as Activity, 1, ReportUserModel(reviewContent.review.userId.toInt()), null)
                } else {
                    CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.startLoginPage(itemView.context as AppCompatActivity)
                            }).show(manager = (itemView.context as AppCompatActivity).supportFragmentManager, tag = "ProductDetailReviewFragment")
                }
            }
            mBinding.framelayoutProductdetailReviewfiles.visibility = if (list?.isEmpty() != false) View.GONE else View.VISIBLE
            mBinding.linearlayoutProductdetailReviewfilescount.visibility = if (list?.size ?: 0 > 1) View.VISIBLE else View.GONE


            mBinding.imageviewProdudctdetailReviewlike.setOnClickListener {
                if (it.tag == "false") {
                    mViewModel.saveLike(reviewContent.review.id, successTask = {
                        it.tag = "true"
                        (it as ImageView).setImageResource(R.drawable.detail_icon_thumbsup_on)
                        mBinding.textviewProductdetailReviewlikecount.text = (mBinding.textviewProductdetailReviewlikecount.text.toString().toInt() + 1).toString()
                        mBinding.executePendingBindings()
                    })
                } else {
                    mViewModel.deleteLike(reviewContent.review.id, successTask = {
                        it.tag = "false"
                        (it as ImageView).setImageResource(R.drawable.detail_icon_thumbsup_off)
                        mBinding.textviewProductdetailReviewlikecount.text = (mBinding.textviewProductdetailReviewlikecount.text.toString().toInt() - 1).toString()
                        mBinding.executePendingBindings()
                    })
                }
            }

            mViewModel.getBookMarks(target = BookMarkTarget.REVIEW.target, successTask = {
                for (item

                in (it.content))
                    if (item.target == BookMarkTarget.REVIEW.target && item.targetId == reviewContent.review.id) {
                        mBinding.imageviewProdudctdetailReviewlike.tag = "true"
                        mBinding.imageviewProdudctdetailReviewlike.setImageResource(R.drawable.detail_icon_thumbsup_on)
                        return@getBookMarks
                    }
            })

            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }
}