package io.temco.guhada.view.adapter

import android.app.Activity
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.viewmodel.SellerInfoViewModel
import io.temco.guhada.databinding.ItemSellerinfoProductBinding
import io.temco.guhada.view.activity.ProductFragmentDetailActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 상세- 셀러스토어, 연관상품, 추천상품 Adapter
 * @author Hyeyeon Park
 * @since 2019.08.13
 */
class SellerInfoProductAdapter : RecyclerView.Adapter<SellerInfoProductAdapter.Holder>() {
    lateinit var mViewModel: SellerInfoViewModel
    var mList: MutableList<Deal> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sellerinfo_product, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: MutableList<Deal>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemSellerinfoProductBinding) : BaseViewHolder<ItemSellerinfoProductBinding>(binding.root) {
        lateinit var mViewModel: SellerInfoViewModel
        internal var width = 0
        internal var height = 0
        internal var layoutHeight = 0
        internal var margin = 0

        fun bind(deal: Deal) {
            setSpacing()
            setSquare(deal)

            mBinding.deal = deal
            mBinding.constraintlayoutSellerinfoStore.setOnClickListener {
                val dealId = deal.dealId.toLong()
                this@SellerInfoProductAdapter.mViewModel.getDetail(dealId = dealId, redirectProductDetailActivity = { this.redirectProductDetailActivity(dealId) })
            }
            mBinding.executePendingBindings()
        }


        private fun setSpacing() {
            val mSpanCount = 2
            val RIGHT_MARGIN = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
            (mBinding.constraintlayoutSellerinfoStore.layoutParams as ViewGroup.MarginLayoutParams).apply {
                rightMargin = if ((adapterPosition + 1) % mSpanCount == 0) 0
                else RIGHT_MARGIN
            }.let {
                mBinding.constraintlayoutSellerinfoStore.layoutParams = it
            }
        }

        private fun setSquare(deal: Deal) {
            if (width == 0) {
                val matrix = DisplayMetrics()
                (binding.root.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                width = (matrix.widthPixels - CommonViewUtil.dipToPixel(itemView.context, 20)) / 2
                height = width
                margin = CommonViewUtil.dipToPixel(itemView.context, 4)
            }

            val param = ConstraintLayout.LayoutParams(width, height)
            param.leftMargin = margin
            binding.imageviewSellerinfoStoreProduct.layoutParams = param

            val url = deal.productImage.url
            ImageUtil.loadImage(Glide.with(binding.root.context), binding.imageviewSellerinfoStoreProduct, url)
        }

        private fun redirectProductDetailActivity(dealId: Long) {
            val intent = Intent(binding.root.context, ProductFragmentDetailActivity::class.java)
            intent.putExtra("dealId", dealId)
            binding.root.context.startActivity(intent)
        }
    }
}