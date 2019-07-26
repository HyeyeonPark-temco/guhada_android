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
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.ProductBridge
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.common.util.TextUtil
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.mypage.MyPageRecentViewModel
import io.temco.guhada.databinding.ItemMypageProductListTwoBinding
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.holder.base.BaseProductViewHolder
/**
 * @author park jungho
 * 19.07.26
 *
 * 마이페이지 최근본상품 list adapter
 */
class MyPageProductListAdapter (private val model : ViewModel, list : ArrayList<Product>) :
        CommonRecyclerAdapter<Product, MyPageProductListAdapter.MyPageProductListViewHolder>(list) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageProductListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ItemMypageProductListTwoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_mypage_product_list_two, parent, false)
        return MyPageProductListViewHolder(binding.root, binding)
    }

    override fun setOnBindViewHolder(viewHolder: MyPageProductListViewHolder, item: Product, position: Int) {
        viewHolder.bind(model as MyPageRecentViewModel, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 메인 리스트에 사용할 base view holder
     */
    inner class MyPageProductListViewHolder(val containerView: View, val binding: ItemMypageProductListTwoBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
        fun bind(model : MyPageRecentViewModel, position : Int, data : Product){
            // Thumbnail
            if(data != null){
                binding.linearlayoutMypageproductlistadapterItemlayout.tag = data.dealId.toString()
                binding.linearlayoutMypageproductlistadapterItemlayout.setOnClickListener{
                    var id = it.tag.toString().toLong()
                    ProductBridge.mainActivity.addProductDetailView(id)
                }
                binding.linearlayoutMypageproductlistadapterItemlayout.visibility = View.VISIBLE
                var url = data.imageUrls!![0]
                ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageThumb, url)

                // Brand
                binding.textBrand.setText(data.brandName)

                // Season
                binding.textSeason.setText(data.season)

                // Title
                binding.textTitle.setText(data.name)

                // Option
                if (binding.layoutColor.getChildCount() > 0)binding.layoutColor.removeAllViews()

                if (data.options != null && data!!.options!!.size > 0) {
                    for (o in data!!.options!!) {
                        when (Type.ProductOption.getType(o!!.type)) {
                            Type.ProductOption.COLOR -> addColor((containerView.context as Activity), binding.layoutColor, 5, o.attributes.toTypedArray()) // 5 Units
                            Type.ProductOption.RGB -> addColor((containerView.context as Activity), binding.layoutColor, 5, o.attributes.toTypedArray()) // 5 Units
                            Type.ProductOption.TEXT -> addText((containerView.context as Activity), o.attributes.toTypedArray())
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
                binding.textShipFree.setVisibility(if (data.shipping!!.isFreeShipping()) View.VISIBLE else View.GONE)

                binding.btnDelete.tag = data.dealId.toString()
                binding.btnDelete.contentDescription = position.toString()
                binding.btnDelete.setOnClickListener {
                    var id = it.tag.toString().toLong()
                    var position = it.contentDescription.toString().toInt()
                    model.getListAdapter().items.removeAt(position)
                    model.mDisposable.add(Observable.fromCallable {
                            model.db.recentDealDao().delete(id)
                        }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { result ->
                                model.totalItemSize.value = model.getListAdapter().itemCount
                                model.getListAdapter().notifyDataSetChanged()
                            }
                    )
                }
            }else{
                binding.linearlayoutMypageproductlistadapterItemlayout.visibility = View.INVISIBLE
                binding.linearlayoutMypageproductlistadapterItemlayout.setOnClickListener(null)
            }

        }
    }

}