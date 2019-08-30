package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.databinding.ItemCommonhorizontalImageroundBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class CommonRoundImageResponseAdapter : RecyclerView.Adapter<CommonRoundImageResponseAdapter.Holder>() {
    var mList: MutableList<ImageResponse> = arrayListOf()

    var mClickSelectItemListener : OnClickSelectItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_commonhorizontal_imageround, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position],position)
    }

    fun setItems(list: MutableList<ImageResponse>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemCommonhorizontalImageroundBinding) : BaseViewHolder<ItemCommonhorizontalImageroundBinding>(binding.root) {
        fun bind(photo: ImageResponse, position: Int) {
            setSpacing()
            mBinding.imagePath = photo.url
            mBinding.executePendingBindings()
            mBinding.setOnClickImageModify {
                mClickSelectItemListener?.clickSelectItemListener(1, position, photo)
            }
            mBinding.setOnClickImageDel {
                mClickSelectItemListener?.clickSelectItemListener(0, position, photo)
            }
        }

        private fun setSpacing() {
            (mBinding.relativelayoutReviewwriteLayout.layoutParams as ViewGroup.MarginLayoutParams).apply {
                rightMargin = if (adapterPosition < mList.size - 1) CommonViewUtil.dipToPixel(context = mBinding.root.context,dip = 10)
                else CommonViewUtil.dipToPixel(context = mBinding.root.context,dip = 20)
            }.let {
                mBinding.relativelayoutReviewwriteLayout.layoutParams = it
            }
        }

    }
}