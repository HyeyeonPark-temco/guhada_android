package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.common.util.TextUtil
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
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(model : ViewModel, position : Int, data : Deal){
            // Thumbnail
            if(data != null){
                binding.linearlayoutMypageproductlistadapterItemlayout.tag = data.dealId.toString()
                binding.linearlayoutMypageproductlistadapterItemlayout.setOnClickListener{
                    var id = it.tag.toString().toLong()
                    CommonUtil.startProductActivity(containerView.context as Activity, id)
                }
                binding.linearlayoutMypageproductlistadapterItemlayout.visibility = View.VISIBLE
                ImageUtil.loadImage((this@MyPageDealListAdapter.model as MyPageBookMarkViewModel).mRequestManager, binding.imageThumb, data.productImage.url)

                // Brand
                binding.textBrand.setText(data.brandName)

                // Season
                binding.textSeason.setText(data.productSeason)

                // Title
                binding.textTitle.setText(data.productName)

                // Option
                if (binding.layoutColor.getChildCount() > 0)binding.layoutColor.removeAllViews()

                if (data.options != null && data!!.options!!.size > 0) {
                    for (o in data!!.options!!) {
                        when (Type.ProductOption.getType(o!!.type)) {
                            Type.ProductOption.COLOR -> addColor((containerView.context as Activity), binding.layoutColor, 5, o.attributes) // 5 Units
                            Type.ProductOption.RGB -> addColor((containerView.context as Activity), binding.layoutColor, 5, o.attributes) // 5 Units
                            Type.ProductOption.TEXT -> addText((containerView.context as Activity), o.attributes)
                        }
                    }
                }
                R.layout.layout_tab_innercategory
                // Price
                if (data.discountRate > 0) {
                    binding.textPrice.setText(String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.toInt())))
                    binding.textPriceSalePer.setText(String.format((containerView.context as Activity).getString(R.string.product_price_sale_per), data.discountRate))
                } else {
                    binding.textPrice.setText(String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.toInt())))
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