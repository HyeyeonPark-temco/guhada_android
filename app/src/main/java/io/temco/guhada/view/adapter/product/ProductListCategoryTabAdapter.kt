package io.temco.guhada.view.adapter.product

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Category
import io.temco.guhada.databinding.ItemCategoryTabBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductListCategoryTabAdapter : RecyclerView.Adapter<ProductListCategoryTabAdapter.Holder>() {
    var mList: MutableList<Category> = arrayListOf()
    var selectIndex = -1

    var mClickSelectItemListener : OnClickSelectItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_category_tab, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position],position)
    }

    fun setItems(list: MutableList<Category>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemCategoryTabBinding) : BaseViewHolder<ItemCategoryTabBinding>(binding.root) {
        fun bind(data: Category, position: Int) {
            mBinding.textTitle.text = data.title
            if(CustomLog.flag)CustomLog.L("clickSelectItemListener","Holder position",position,"selectIndex",selectIndex)
            if(selectIndex == position){
                mBinding.textTitle.setTypeface(null, Typeface.BOLD)
                mBinding.textTitle.setTextColor(Color.parseColor("#5d2ed1"))
                mBinding.imgIdc.setBackgroundColor(Color.parseColor("#5d2ed1"))
            }else{
                mBinding.textTitle.setTypeface(null, Typeface.NORMAL)
                mBinding.textTitle.setTextColor(Color.parseColor("#111111"))
                mBinding.imgIdc.setBackgroundColor(Color.parseColor("#ffffff"))
            }
            mBinding.layoutTab.tag = data
            mBinding.layoutTab.setOnClickListener {
                mClickSelectItemListener?.clickSelectItemListener(0, position,it.tag as Category)
            }
            mBinding.executePendingBindings()
        }

        /*private fun setSpacing() {
            (mBinding.relativelayoutReviewwriteLayout.layoutParams as ViewGroup.MarginLayoutParams).apply {
                rightMargin = if (adapterPosition < mList.size - 1) CommonViewUtil.dipToPixel(context = mBinding.root.context,dip = 5)
                else CommonViewUtil.dipToPixel(context = mBinding.root.context,dip = 20)
            }.let {
                mBinding.relativelayoutReviewwriteLayout.layoutParams = it
            }
        }*/


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