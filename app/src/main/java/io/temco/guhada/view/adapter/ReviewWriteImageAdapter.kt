package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.review.ReviewPhotos
import io.temco.guhada.databinding.ItemReviewwriteImageBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ReviewWriteImageAdapter : RecyclerView.Adapter<ReviewWriteImageAdapter.Holder>() {
    var mList: MutableList<ReviewPhotos> = arrayListOf()

    var mClickSelectItemListener : OnClickSelectItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_reviewwrite_image, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position],position)
    }

    fun setItems(list: MutableList<ReviewPhotos>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemReviewwriteImageBinding) : BaseViewHolder<ItemReviewwriteImageBinding>(binding.root) {
        fun bind(photo: ReviewPhotos, position: Int) {
            setSpacing()
            mBinding.photo = photo
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
                rightMargin = if (adapterPosition < mList.size - 1) CommonViewUtil.dipToPixel(context = mBinding.root.context,dip = 5)
                else CommonViewUtil.dipToPixel(context = mBinding.root.context,dip = 20)
            }.let {
                mBinding.relativelayoutReviewwriteLayout.layoutParams = it
            }
        }


        /*private fun setPic(imageView : ImageView, imgFile : String) {
            val targetW: Int = imageView.width
            val targetH: Int = imageView.height

            val bmOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true

                val photoW: Int = outWidth
                val photoH: Int = outHeight
                val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

                inJustDecodeBounds = false
                inSampleSize = scaleFactor
                inPurgeable = true
            }
            BitmapFactory.decodeFile(imgFile, bmOptions)?.also { bitmap -> imageView.setImageBitmap(bitmap) }
        }*/
    }
}