package io.temco.guhada.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.*
import io.temco.guhada.data.viewmodel.PlanningDealDetailViewModel
import io.temco.guhada.databinding.*
import io.temco.guhada.view.activity.base.BaseActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.fragment.ListBottomSheetFragment
import io.temco.guhada.view.holder.base.BaseProductViewHolder


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 recycler adapter
 */
class PlanningDealDetailListAdapter(private val model : PlanningDealDetailViewModel, list : ArrayList<PlanningDealBase>) :
        CommonRecyclerAdapter<PlanningDealBase, PlanningDealDetailListAdapter.ListViewHolder>(list){

    /**
     * HomeType 에 따른 item view  TextUtils
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when(items[position].type){
            PlanningDealType.Title->return R.layout.customlayout_planningdeal_detail_title
            PlanningDealType.ImageBanner->return R.layout.customlayout_planningdeal_detail_imgbanner
            PlanningDealType.SubTitle->return R.layout.customlayout_planningdeal_detail_subtitle
            PlanningDealType.Deal->return R.layout.customlayout_main_item_dealone
            PlanningDealType.Loading->return R.layout.customlayout_main_item_loading
            else ->return R.layout.customlayout_main_item_padding
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(items[viewType].type){
            PlanningDealType.Title->{
                val binding : CustomlayoutPlanningdealDetailTitleBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PlanningDealDetailTitleViewHolder(binding.root, binding)
            }
            PlanningDealType.ImageBanner->{
                val binding : CustomlayoutPlanningdealDetailImgbannerBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PlanningDealDetailImageBannerViewHolder(binding.root, binding)
            }
            PlanningDealType.SubTitle->{
                val binding : CustomlayoutPlanningdealDetailSubtitleBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PlanningDealDetailSubTitleViewHolder(binding.root, binding)
            }
            PlanningDealType.Deal->{
                val binding : CustomlayoutMainItemDealoneBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return DealOneViewHolder(binding.root, binding)
            }
            PlanningDealType.Loading->{
                val binding : CustomlayoutMainItemLoadingBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return ViewLoadingViewHolder(binding.root, binding)
            }
            else ->{
                val binding : CustomlayoutMainItemPaddingBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PaddingViewHolder(binding.root, binding)
            }
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: PlanningDealBase, position: Int) {
        viewHolder.bind(model, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    fun setItems(list : List<PlanningDealBase>){
        items.addAll(list)
    }



    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : PlanningDealDetailViewModel, position : Int, item : PlanningDealBase)
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class ViewLoadingViewHolder(private val containerView: View, val binding: CustomlayoutMainItemLoadingBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealDetailViewModel, position: Int, item: PlanningDealBase) {
            binding.progressbarLoadingdialog.isIndeterminate = true
            binding.progressbarLoadingdialog.indeterminateDrawable.setColorFilter(viewModel.context.resources.getColor(R.color.common_white), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class DealOneViewHolder(private val containerView: View, val binding: CustomlayoutMainItemDealoneBinding) : ListViewHolder(containerView, binding){
        internal var width = 0
        internal var margin = 0
        internal lateinit var request : RequestOptions

        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealDetailViewModel, position: Int, item: PlanningDealBase) {
            if(item is PlanningDealData){
                if (width == 0) {
                    val matrix = DisplayMetrics()
                    (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(viewModel.context, 24)) / 2
                    margin = CommonViewUtil.dipToPixel(viewModel.context, 8)
                }

                val param = LinearLayout.LayoutParams(width, width)
                if (position % 2 == 1) {
                    param.leftMargin = margin
                    param.rightMargin = margin/2
                } else {
                    param.leftMargin = margin/2
                    param.rightMargin = margin
                }
                binding.relativeImageLayout.setLayoutParams(param)
                binding.item = item.deal

                itemView.setOnClickListener{
                    CommonUtil.startProductActivity(viewModel.context as Activity, item.deal.dealId.toLong())
                }
                binding.imageThumb.layoutParams = RelativeLayout.LayoutParams(width, width)

                // Brand
                binding.textBrand.text = item.deal.brandName

                // Season
                binding.textSeason.text = item.deal.productSeason

                // Title
                binding.textTitle.text = item.deal.productName

                if(!::request.isInitialized){
                    request = RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(width,width)
                            .downsample(DownsampleStrategy.AT_MOST)
                }
                if(binding.imageThumb.contentDescription == null || binding.imageThumb.contentDescription.toString() != item.deal.productImage.url){
                    // Thumbnail
                    ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageThumb, item.deal.productImage.url ,request)
                    binding.imageThumb.contentDescription = item.deal.productImage.url
                }

                // Option
                if (binding.layoutColor.childCount > 0) {
                    binding.layoutColor.removeAllViews()
                }
                if (item.deal.options != null && item.deal.options.size > 0) {
                    for (o in item.deal.options) {
                        when (Type.ProductOption.getType(o.type)) {
                            Type.ProductOption.RGB, Type.ProductOption.COLOR -> addColor(viewModel.context, binding.layoutColor, 5, o.attributes) // 5 Units
                            Type.ProductOption.TEXT -> addText(viewModel.context, o.attributes)
                        }
                    }
                }

                binding.textSellerName.setText(item.deal.sellerName)

                // Price
                if (item.deal.discountRate > 0) {
                    binding.textPrice.text = TextUtil.getDecimalFormat(item.deal.discountPrice.toInt())
                    binding.textPriceSalePer.visibility = View.VISIBLE
                    binding.textPriceSalePer.text = String.format(viewModel.context.getString(R.string.product_price_sale_per), item.deal.discountRate)
                    binding.textPricediscount.visibility = View.VISIBLE
                    binding.textPricediscount.setPaintFlags(binding.textPricediscount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    binding.textPricediscount.text = TextUtil.getDecimalFormat(item.deal.sellPrice.toInt())
                } else {
                    binding.textPrice.text = TextUtil.getDecimalFormat(item.deal.sellPrice.toInt())
                    binding.textPriceSalePer.visibility = View.GONE
                    binding.textPricediscount.visibility = View.GONE
                }

                // Ship
                binding.textShipFree.visibility = if (item.deal.freeShipping) View.VISIBLE else View.GONE
                binding.executePendingBindings()
            }
        }
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PlanningDealDetailTitleViewHolder(private val containerView: View, val binding: CustomlayoutPlanningdealDetailTitleBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealDetailViewModel, position: Int, item: PlanningDealBase) {
            if(item is PlanningDealTitle){
                binding.title = item.title
                binding.endDate = item.strDate
                binding.setClickShareListener{
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, item.link)
                        type = "text/plain"
                    }
                    (viewModel.context as Activity).startActivity(Intent.createChooser(sendIntent, "공유"))
                }
            }
        }
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PlanningDealDetailSubTitleViewHolder(private val containerView: View, val binding: CustomlayoutPlanningdealDetailSubtitleBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealDetailViewModel, position: Int, item: PlanningDealBase) {
            binding.viewModel = viewModel
            binding.setClickSortListener{
                val bottomSheet = ListBottomSheetFragment(viewModel.context).apply {
                    this.mList = viewModel.mSortFilterLabel
                    this.mTitle = "정렬 선택"
                    this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            viewModel.mFilterIndex = position
                            viewModel.planningDealSortType.set(viewModel.mSortFilterType[viewModel.mFilterIndex])
                            //viewModel.getDealListData(true, true)
                        }
                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }
                }
                if ((viewModel.context as BaseActivity).supportFragmentManager != null)
                    bottomSheet.show((viewModel.context as BaseActivity).supportFragmentManager!!, "")
            }
        }
    }
    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PlanningDealDetailImageBannerViewHolder(private val containerView: View, val binding: CustomlayoutPlanningdealDetailImgbannerBinding) : ListViewHolder(containerView, binding){
        internal var width = 0
        internal var height = 0
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealDetailViewModel, position: Int, item: PlanningDealBase) {
            if(item is PlanningDealImageBanner){
                Glide.with(viewModel.context).load(item.imgPath)
                        .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888).downsample(DownsampleStrategy.AT_MOST))
                        .into(object : SimpleTarget<Drawable>(){
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                if(width == 0){
                                    val matrix = DisplayMetrics()
                                    (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                                    width = matrix.widthPixels
                                    var ra : Float = width / resource.minimumWidth.toFloat()
                                    height  = (ra * resource.minimumHeight.toFloat()).toInt()
                                    binding.imageBanner.layoutParams = RelativeLayout.LayoutParams(width,height)
                                }
                                binding.imageBanner.setImageDrawable(resource)
                            }
                        }
                )
            }
        }
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PaddingViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPaddingBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealDetailViewModel, position: Int, item: PlanningDealBase) { }
    }


}

