package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.viewmodel.mypage.MyPageBookMarkViewModel
import io.temco.guhada.databinding.ItemMoreListBinding
import io.temco.guhada.databinding.ItemMypageProductListTwoBinding
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.holder.base.BaseProductViewHolder

/**
 * @author park jungho
 * 19.07.26
 *
 * 마이페이지 찜한상품 (BookMark)
 */
class MyPageDealListAdapter (private val model : ViewModel, list : ArrayList<Deal>) :
        CommonRecyclerAdapter<Deal, MyPageDealListAdapter.ListViewHolder>(list) {

    override fun getItemViewType(position: Int): Int {
        return items.let {
            if(items.isNullOrEmpty()) 0
            else if(items[position].dealId < 0) -1
            else 0
        }
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if(viewType == 0){
            var res = R.layout.item_mypage_product_list_two
            val binding : ItemMypageProductListTwoBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
            return MyPageProductListViewHolder(binding.root, binding)
        }else{
            var res = R.layout.item_more_list
            val binding : ItemMoreListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
            return MyPageMoreListViewHolder(binding.root, binding)
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: Deal, position: Int) {
        viewHolder.bind(model, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : ViewModel, position : Int, data : Deal)
    }

    /**
     * 메인 리스트에 사용할 base view holder
     */
    inner class MyPageProductListViewHolder(val containerView: View, val binding: ItemMypageProductListTwoBinding) : ListViewHolder(containerView,binding){
        internal var width = 0
        internal var height = 0
        internal var margin = 0
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(model : ViewModel, position : Int, data : Deal){
            // Thumbnail
            if(data != null){
                if (width == 0) {
                    val matrix = DisplayMetrics()
                    (itemView.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(itemView.context, 24)) / 2
                    margin = CommonViewUtil.dipToPixel(itemView.context, 8)
                }

                val param = LinearLayout.LayoutParams(width, width)
                if (position % 2 == 0) {
                    param.leftMargin = margin
                    param.rightMargin = margin/2
                } else {
                    param.leftMargin = margin/2
                    param.rightMargin = margin
                }
                binding.relativeImageLayout.setLayoutParams(param)

                binding.linearlayoutMypageproductlistadapterItemlayout.tag = data.dealId.toString()
                binding.linearlayoutMypageproductlistadapterItemlayout.setOnClickListener{
                    var id = it.tag.toString().toLong()
                    CommonUtil.startProductActivity(containerView.context as Activity, id)
                }
                binding.linearlayoutMypageproductlistadapterItemlayout.visibility = View.VISIBLE
                ImageUtil.loadImage((this@MyPageDealListAdapter.model as MyPageBookMarkViewModel).mRequestManager, binding.imageThumb, data.productImage.url)

                // Brand
                binding.textBrand.text = data.brandName

                // Season
                binding.textSeason.text = data.productSeason

                // Title
                binding.textTitle.text = data.productName

                if (data.isBoldName) {
                    binding.textTitle.setTypeface(null, Typeface.BOLD)
                } else {
                    binding.textTitle.setTypeface(null, Typeface.NORMAL)
                }

                // Option
                if (binding.layoutColor.getChildCount() > 0)binding.layoutColor.removeAllViews()

                if (data.options != null && data!!.options!!.size > 0) {
                    if(CustomLog.flag) CustomLog.L("MyPageDealListAdapter","data.options",data.options.toString())
                    for (o in data!!.options!!) {
                        when (Type.ProductOption.getType(o!!.type)) {
                            Type.ProductOption.COLOR -> addColor((containerView.context as Activity), binding.layoutColor, 5, o.attributes) // 5 Units
                            Type.ProductOption.RGB -> addColor((containerView.context as Activity), binding.layoutColor, 5, o.attributes) // 5 Units
                            Type.ProductOption.TEXT -> addText((containerView.context as Activity), o.attributes)
                        }
                    }
                }

                // Price
                if (data.discountRate > 0) {
                    binding.textPrice.setText(TextUtil.getDecimalFormat(data.discountPrice.toInt()))
                    binding.textPriceSalePer.setVisibility(View.VISIBLE)
                    binding.textPriceSalePer.setText(String.format((containerView.context as Activity).getString(R.string.product_price_sale_per), data.discountRate))
                    binding.textPricediscount.setVisibility(View.VISIBLE)
                    binding.textPricediscount.setPaintFlags(binding.textPricediscount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    binding.textPricediscount.setText(TextUtil.getDecimalFormat(data.sellPrice.toInt()))
                } else {
                    binding.textPrice.setText(TextUtil.getDecimalFormat(data.sellPrice.toInt()))
                    binding.textPriceSalePer.setVisibility(View.GONE)
                    binding.textPricediscount.setVisibility(View.GONE)
                }
                // Ship
                //if(CustomLog.flag)CustomLog.L("HomeListAdapter",item.title,"SubTitleViewHolder textShipFree","data.freeShipping",data.freeShipping)
                binding.textShipFree.setVisibility(if (data.isFreeShipping) View.VISIBLE else View.GONE)

                binding.btnDelete.tag = data.productId.toString()
                binding.btnDelete.contentDescription = position.toString()
                binding.btnDelete.setOnClickListener {
                    var id = it.tag.toString().toLong()
                    var position = it.contentDescription.toString().toInt()
                    if(model is MyPageBookMarkViewModel){
                        var position = it.contentDescription.toString().toInt()
                        model.getListAdapter().items.removeAt(position)
                        model.onClickDelete(id)
                    }
                }
            }else{
                binding.linearlayoutMypageproductlistadapterItemlayout.visibility = View.INVISIBLE
                binding.linearlayoutMypageproductlistadapterItemlayout.setOnClickListener(null)
            }

        }
    }

    inner class MyPageMoreListViewHolder(val containerView: View, val binding: ItemMoreListBinding) : ListViewHolder(containerView,binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) {  }
        override fun bind(model : ViewModel, position : Int, data : Deal){
            binding.linearlayoutMoreView.setOnClickListener {
                if(model is MyPageBookMarkViewModel){
                    model.getMyPageBookMarkList()
                }
            }
        }
    }

}