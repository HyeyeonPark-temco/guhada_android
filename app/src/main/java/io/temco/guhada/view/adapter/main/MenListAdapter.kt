package io.temco.guhada.view.adapter.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.viewmodel.main.MenListViewModel
import io.temco.guhada.databinding.*
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.activity.ProductFilterListActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import io.temco.guhada.view.viewpager.InfiniteGeneralFixedPagerAdapter

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 recycler adapter
 */
class MenListAdapter(private val model : MenListViewModel, list : ArrayList<MainBaseModel>) :
        CommonRecyclerAdapter<MainBaseModel, MenListAdapter.ListViewHolder>(list){
    /**
     * HomeType 에 따른 item view
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when(items[position].type){
            HomeType.SubTitleList->return R.layout.customlayout_main_item_subtitlelist
            HomeType.SubTitleLayout->return R.layout.customlayout_main_item_subtitle
            HomeType.DealItemOne->return R.layout.customlayout_main_item_dealone
            HomeType.ViewMoreLayout->return R.layout.customlayout_main_item_viewmore
            HomeType.SubTitleList->return R.layout.customlayout_main_item_subtitlelist
            HomeType.Footer->return R.layout.item_terminfo_footer
            HomeType.Keyword->return R.layout.customlayout_main_item_keyword
            HomeType.Dummy->return R.layout.customlayout_main_item_dummy
            else ->return R.layout.customlayout_main_item_padding
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(items[viewType].type){
            HomeType.SubTitleLayout->{
                val binding : CustomlayoutMainItemSubtitleBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SubTitleLayoutViewHolder(binding.root, binding)
            }
            HomeType.DealItemOne->{
                val binding : CustomlayoutMainItemDealoneBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return DealOneViewHolder(binding.root, binding)
            }
            HomeType.ViewMoreLayout->{
                val binding : CustomlayoutMainItemViewmoreBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return ViewMoreViewHolder(binding.root, binding)
            }
            HomeType.SubTitleList->{
                val binding : CustomlayoutMainItemSubtitlelistBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SubTitleViewHolder(binding.root, binding)
            }
            HomeType.Dummy->{
                val binding : CustomlayoutMainItemDummyBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return DummyViewHolder(binding.root, binding)
            }
            HomeType.Keyword->{
                val binding : CustomlayoutMainItemKeywordBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return KeywordViewHolder(binding.root, binding)
            }
            HomeType.Footer->{
                val binding : ItemTerminfoFooterBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return TermViewHolder(binding.root, binding)
            }
            else ->{
                val binding : CustomlayoutMainItemPaddingBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PaddingViewHolder(binding.root, binding)
            }
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: MainBaseModel, position: Int) {
        viewHolder.bind(model as MenListViewModel, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size


    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : MenListViewModel, position : Int, item : MainBaseModel)
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PaddingViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPaddingBinding) : ListViewHolder(containerView, binding){
        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) { }
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class SubTitleLayoutViewHolder(private val containerView: View, val binding: CustomlayoutMainItemSubtitleBinding) : ListViewHolder(containerView, binding){
        var tabImg = arrayListOf(binding.tabImg0,binding.tabImg1,binding.tabImg2,binding.tabImg3)
        var tabTitle = arrayListOf(binding.tabTitle0,binding.tabTitle1,binding.tabTitle2,binding.tabTitle3)

        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(model: MenListViewModel, position: Int, item: MainBaseModel) {
            if(item is SubTitleLayout){
                binding.subtitle.visibility = View.GONE
                binding.title.text = item.title
            }
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
        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) {
            if(item is DealItem){
                if (width == 0) {
                    val matrix = DisplayMetrics()
                    (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(viewModel.context, 24)) / 2
                    margin = CommonViewUtil.dipToPixel(viewModel.context, 8)
                }

                val param = LinearLayout.LayoutParams(width, width)
                if (position % 2 == 0) {
                    param.leftMargin = margin/2
                    param.rightMargin = margin
                } else {
                    param.leftMargin = margin
                    param.rightMargin = margin/2
                }
                binding.relativeImageLayout.setLayoutParams(param)

                val imageParams = RelativeLayout.LayoutParams(width, width)
                binding.imageThumb.setLayoutParams(imageParams)

                itemView.setOnClickListener{
                    CommonUtil.startProductActivity(viewModel.context as Activity, item.deal.dealId.toLong())
                }

                // Brand
                binding.textBrand.setText(item.deal.brandName)

                // Season
                binding.textSeason.setText(item.deal.productSeason)

                // Title
                binding.textTitle.setText(item.deal.productName)

                if(!::request.isInitialized){
                    request = RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(width,width)
                            .downsample(DownsampleStrategy.AT_MOST)
                }
                // Thumbnail
                ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageThumb, item.deal.productImage.url,request)

                // Option
                if (binding.layoutColor.getChildCount() > 0) {
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
                    binding.textPrice.setText(TextUtil.getDecimalFormat(item.deal.discountPrice.toInt()))
                    binding.textPriceSalePer.setVisibility(View.VISIBLE)
                    binding.textPriceSalePer.setText(String.format(viewModel.context.getString(R.string.product_price_sale_per), item.deal.discountRate))
                    binding.textPricediscount.setVisibility(View.VISIBLE)
                    binding.textPricediscount.setPaintFlags(binding.textPricediscount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    binding.textPricediscount.setText(TextUtil.getDecimalFormat(item.deal.sellPrice.toInt()))
                } else {
                    binding.textPrice.setText(TextUtil.getDecimalFormat(item.deal.sellPrice.toInt()))
                    binding.textPriceSalePer.setVisibility(View.GONE)
                    binding.textPricediscount.setVisibility(View.GONE)
                }
                // Ship
                binding.textShipFree.setVisibility(if (item.deal.freeShipping) View.VISIBLE else View.GONE)
                binding.executePendingBindings()
            }
        }
    }



    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class ViewMoreViewHolder(private val containerView: View, val binding: CustomlayoutMainItemViewmoreBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) {
            if(item is ViewMoreLayout){
                binding.setClickListener {
                    var intent = Intent(itemView.context as MainActivity, ProductFilterListActivity::class.java)
                    intent.putExtra("type", Type.ProductListViewType.VIEW_MORE)
                    intent.putExtra("search_word", item.moreType)
                    intent.putExtra("search_Category", if(item.currentSubTitleIndex==0) "" else item.currentSubTitleIndex.toString())
                    (itemView.context as MainActivity).startActivityForResult(intent, Flag.RequestCode.BASE)
                }
            }
        }
    }

    /**
     * 메인 리스트에 전체,여성,남성,키드 하위 탭이 있는 view holder
     *
     * 내부 데이터 연동은 ProductListFragment -> ProductListAdapter -> ProductTwoViewHolder.init 을 가져와 사용
     */
    inner class SubTitleViewHolder(private val containerView: View, val binding: CustomlayoutMainItemSubtitlelistBinding) : ListViewHolder(containerView, binding){
        var tabImg = arrayListOf(binding.tabImg0,binding.tabImg1,binding.tabImg2,binding.tabImg3)
        var tabTitle = arrayListOf(binding.tabTitle0,binding.tabTitle1,binding.tabTitle2,binding.tabTitle3)
        var itemlayout = arrayListOf(binding.itemLayout0,binding.itemLayout1,binding.itemLayout2,binding.itemLayout3,binding.itemLayout4,binding.itemLayout5)
        var itemrelaytivelayout = arrayListOf(binding.itemRealativelayout0,binding.itemRealativelayout1,binding.itemRealativelayout2,binding.itemRealativelayout3,binding.itemRealativelayout4,binding.itemRealativelayout5)
        var imageThumb = arrayListOf(binding.imageThumb0,binding.imageThumb1,binding.imageThumb2,binding.imageThumb3,binding.imageThumb4,binding.imageThumb5)
        var textBrand = arrayListOf(binding.textBrand0,binding.textBrand1,binding.textBrand2,binding.textBrand3,binding.textBrand4,binding.textBrand5)
        var textSeason = arrayListOf(binding.textSeason0,binding.textSeason1,binding.textSeason2,binding.textSeason3,binding.textSeason4,binding.textSeason5)
        var textTitle = arrayListOf(binding.textTitle0,binding.textTitle1,binding.textTitle2,binding.textTitle3,binding.textTitle4,binding.textTitle5)
        var layoutColor = arrayListOf(binding.layoutColor0,binding.layoutColor1,binding.layoutColor2,binding.layoutColor3,binding.layoutColor4,binding.layoutColor5)
        var textPrice = arrayListOf(binding.textPrice0,binding.textPrice1,binding.textPrice2,binding.textPrice3,binding.textPrice4,binding.textPrice5)
        var text_pricediscount = arrayListOf(binding.textPricediscount0,binding.textPricediscount1,binding.textPricediscount2,binding.textPricediscount3,binding.textPricediscount4,binding.textPricediscount5)
        var textPriceSalePer = arrayListOf(binding.textPriceSalePer0,binding.textPriceSalePer1,binding.textPriceSalePer2,binding.textPriceSalePer3,binding.textPriceSalePer4,binding.textPriceSalePer5)
        var textShipFree = arrayListOf(binding.textShipFree0,binding.textShipFree1,binding.textShipFree2,binding.textShipFree3,binding.textShipFree4,binding.textShipFree5)
        var textSellerRate = arrayListOf(binding.textSellerRate0,binding.textSellerRate1,binding.textSellerRate2,binding.textSellerRate3,binding.textSellerRate4,binding.textSellerRate5)
        var textSellerName = arrayListOf(binding.textSellerName0,binding.textSellerName1,binding.textSellerName2,binding.textSellerName3,binding.textSellerName4,binding.textSellerName5)


        var width = 0
        var height = 0
        var layoutHeight = 0
        var margin = 0

        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }

        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) {
            if(item is SubTitleItemList){
                var homeDeal = item.data as HomeDeal
                if(width == 0){
                    val matrix = DisplayMetrics()
                    (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(viewModel.context, 13)) / 2
                    height = width
                    margin = CommonViewUtil.dipToPixel(viewModel.context, 3)
                    layoutHeight = height + CommonViewUtil.dipToPixel(viewModel.context, 126)
                }

                var searchCondition = ""
                when(item.title){
                    "BEST ITEM" -> searchCondition = Type.SerchFilterCondition.BEST.name
                    "NEW IN" -> searchCondition = Type.SerchFilterCondition.NEW.name
                    "PREMIUM ITEM" -> searchCondition = Type.SerchFilterCondition.PLUS.name
                }
                binding.isViewMore = !TextUtils.isEmpty(searchCondition)
                binding.setClickListener {
                    var intent = Intent(itemView.context as MainActivity, ProductFilterListActivity::class.java)
                    intent.putExtra("type", Type.ProductListViewType.VIEW_MORE)
                    intent.putExtra("search_word", searchCondition)
                    intent.putExtra("search_Category", "2")
                    (itemView.context as MainActivity).startActivityForResult(intent, Flag.RequestCode.BASE)
                }

                // Thumbnail
                var size = if(item.listSize[item.currentSubTitleIndex] - 1 > 6) 5 else item.listSize[item.currentSubTitleIndex] - 1
                binding.itemLayoutContent0.visibility = View.GONE
                binding.itemLayoutContent1.visibility = View.GONE
                binding.itemLayoutContent2.visibility = View.GONE

                if(size >= 0) binding.itemLayoutContent0.visibility = View.VISIBLE
                if(size >= 2) binding.itemLayoutContent1.visibility = View.VISIBLE
                if(size >= 4) binding.itemLayoutContent2.visibility = View.VISIBLE

                binding.title.text = item.title
                setTabLayout(position)
                val model : MenListViewModel = this@MenListAdapter.model as MenListViewModel
                binding.tab0.setOnClickListener{
                    if((model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex != 0){
                        (model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex = 0
                        model.getListAdapter().notifyItemChanged(position)
                    }
                }
                binding.tab1.setOnClickListener{
                    if((model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex != 1){
                        (model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex = 1
                        model.getListAdapter().notifyItemChanged(position)
                    }
                }
                binding.tab2.setOnClickListener{
                    if((model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex != 2){
                        (model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex = 2
                        model.getListAdapter().notifyItemChanged(position)
                    }
                }
                binding.tab3.setOnClickListener{
                    if((model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex != 3){
                        (model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex = 3
                        model.getListAdapter().notifyItemChanged(position)
                    }
                }
                for (i in 0..5){
                    var data : Deal?  = when(item.currentSubTitleIndex){
                        0->{
                            if(homeDeal.allList!!.size <= i) null
                            else homeDeal.allList!![i]
                        }
                        1->{
                            if(homeDeal.womenList!!.size <= i) null
                            else homeDeal.womenList!![i]
                        }
                        2->{
                            if(homeDeal.menList!!.size <= i) null
                            else homeDeal.menList!![i]
                        }
                        3->{
                            if(homeDeal.kidsList!!.size <= i) null
                            else homeDeal.kidsList!![i]
                        }
                        else -> {
                            if(homeDeal.allList!!.size <= i) null
                            else homeDeal.allList!![i]
                        }
                    }

                    itemrelaytivelayout[i].layoutParams = LinearLayout.LayoutParams(width,height)
                    itemlayout[i].layoutParams = LinearLayout.LayoutParams(width,layoutHeight).apply {
                        if(i % 2 == 1) {
                            leftMargin = margin
                            rightMargin = 0
                        }  else{
                            leftMargin = 0
                            rightMargin = margin
                        }
                    }

                    if(data != null){
                        itemlayout[i].contentDescription = data.dealId.toString()
                        itemlayout[i].setOnClickListener{
                            var id = it.contentDescription.toString().toLong()
                            CommonUtil.startProductActivity(viewModel.context as Activity, id)
                        }
                        itemlayout[i].visibility = View.VISIBLE
                        ImageUtil.loadImage(Glide.with(containerView.context as Activity), imageThumb[i], data.productImage.url)

                        // Brand
                        textBrand[i].setText(data.brandName)

                        // Season
                        textSeason[i].setText(data.productSeason)

                        // Title
                        textTitle[i].setText(data.dealName)
                        if(data.isBoldName){
                            textTitle[i].setTypeface(null, Typeface.BOLD)
                        }else{
                            textTitle[i].setTypeface(null, Typeface.NORMAL)
                        }


                        // Seller Name
                        textSellerName[i].setText(data.sellerName)

                        // Option
                        if (layoutColor[i].getChildCount() > 0)layoutColor[i].removeAllViews()

                        if (data.options != null && data.options.size > 0) {
                            for (o in data.options) {
                                when (Type.ProductOption.getType(o.type)) {
                                    Type.ProductOption.COLOR -> addColor((containerView.context as Activity), layoutColor[i], 5, o.attributes) // 5 Units
                                    Type.ProductOption.RGB -> addColor((containerView.context as Activity), layoutColor[i], 5, o.attributes) // 5 Units
                                    Type.ProductOption.TEXT -> addText((containerView.context as Activity), o.attributes)
                                }
                            }
                        }
                        R.layout.layout_tab_innercategory
                        // Price
                        if (data.discountRate > 0) {
                            textPrice[i].text = TextUtil.getDecimalFormat(data.discountPrice.toInt())
                            //textPrice[i].text = String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.toInt()))
                            textPriceSalePer[i].text = String.format((containerView.context as Activity).getString(R.string.product_price_sale_per), data.discountRate)
                            text_pricediscount[i].visibility = View.VISIBLE
                            text_pricediscount[i].paintFlags = (text_pricediscount[i].paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
                            text_pricediscount[i].text = TextUtil.getDecimalFormat(data.sellPrice.toInt())
                            //text_pricediscount[i].text = String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.toInt()))
                        } else {
                            textPrice[i].text = TextUtil.getDecimalFormat(data.sellPrice.toInt())
                            //textPrice[i].text = String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.toInt()))
                            text_pricediscount[i].visibility = View.GONE
                        }
                        // Ship
                        //if(CustomLog.flag)CustomLog.L("MenListAdapter",item.title,"SubTitleViewHolder textShipFree","data.freeShipping",data.freeShipping)
                        textShipFree[i].visibility = (if (data.isFreeShipping) View.VISIBLE else View.GONE)
                    }else{
                        itemlayout[i].visibility = View.INVISIBLE
                        itemlayout[i].setOnClickListener(null)
                    }

                }
            }
        }
        private fun setTabLayout(position : Int) {
            val size = tabImg.size-1
            val model : MenListViewModel = this@MenListAdapter.model as MenListViewModel
            for (i in 0..size){
                if((model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex == i){
                    tabImg[i].setBackgroundResource(R.color.black_four)
                    tabTitle[i].setTextColor((containerView.context as Activity).resources.getColor(R.color.black_four))
                }else{
                    tabImg[i].setBackgroundResource(R.color.common_white)
                    tabTitle[i].setTextColor((containerView.context as Activity).resources.getColor(R.color.warm_grey_six))
                }
                /*tab[i].setOnClickListener{
                    var index = it.tag.toString().toInt()
                    if((model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex != index){
                        (model.getListAdapter().items[position] as SubTitleItemList).currentSubTitleIndex = index
                        model.getListAdapter().notifyItemChanged(position)
                    }
                }*/
            }
        }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class KeywordViewHolder(private val containerView: View, val binding: CustomlayoutMainItemKeywordBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) {
            if(item is KeywordMain){
                binding.title = item.title
                if (binding.listKeyowrd.adapter == null) {
                    binding.listKeyowrd.adapter = MainKeywordListAdapter().apply { mList = item.listKeyword!! }
                } else {
                    (binding.listKeyowrd.adapter as MainKeywordListAdapter).setItems(item.listKeyword!!)
                }
                binding.executePendingBindings()
            }
        }
    }

    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class TermViewHolder(private val containerView: View, val binding: ItemTerminfoFooterBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) {
            binding.setOnClickListener {
                when(it.id){
                    R.id.textview_term_claim ->{ CommonUtilKotlin.startActivityUserClaimGuhada(containerView.context as AppCompatActivity) }
                    R.id.textview_term_terms ->{ CommonUtilKotlin.startTermsPurchase(containerView.context as Activity) }
                    R.id.textview_term_advise ->{}
                    R.id.textview_term_privacy_terms ->{CommonUtilKotlin.startTermsPersonal(containerView.context as Activity) }
                    R.id.textview_term_guarantee ->{CommonUtilKotlin.startTermsGuarantee(containerView.context as Activity) }
                    R.id.textview_term_company ->{CommonUtilKotlin.startTermsCompany(containerView.context as Activity) }
                    R.id.textview_term_call ->{
                        val phone = (itemView.context as Activity).resources.getString(R.string.information_company_call)
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                        (itemView.context as Activity).startActivity(intent)
                    }
                    R.id.textview_term_partner, R.id.textview_term_email ->{
                        var email = Intent(Intent.ACTION_SEND).apply {
                            type = "plain/Text"
                            var email : Array<String> = arrayOf("help@guhada.com")
                            putExtra(Intent.EXTRA_EMAIL, email)
                            putExtra(Intent.EXTRA_SUBJECT, "")
                            putExtra(Intent.EXTRA_TEXT, "")
                        }
                        (itemView.context as Activity).startActivity(email)
                    }
                }
            }
        }
    }

    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class DummyViewHolder(private val containerView: View, val binding: CustomlayoutMainItemDummyBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: MenListViewModel, position: Int, item: MainBaseModel) {
            if(item is DummyImage){
                var metrics = DisplayMetrics()
                (containerView.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
                binding.heightLayout.setmHeight((item.imageHeight * metrics.density).toInt())
                binding.heightLayout.setmWidth((360 * metrics.density).toInt())
                binding.imageDummy.setBackgroundResource(item.imageRes)
            }
        }
    }


}

