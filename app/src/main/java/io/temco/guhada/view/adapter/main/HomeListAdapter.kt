package io.temco.guhada.view.adapter.main

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.viewmodel.main.HomeListViewModel
import io.temco.guhada.databinding.CustomlayoutMainItemDummyBinding
import io.temco.guhada.databinding.CustomlayoutMainItemMaineventBinding
import io.temco.guhada.databinding.CustomlayoutMainItemPaddingBinding
import io.temco.guhada.databinding.CustomlayoutMainItemSubtitlelistBinding
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import io.temco.guhada.view.viewpager.InfiniteGeneralFixedPagerAdapter

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 recycler adapter
 */
class HomeListAdapter(private val model : ViewModel, list : ArrayList<MainBaseModel>) :
        CommonRecyclerAdapter<MainBaseModel, HomeListAdapter.ListViewHolder>(list){
    /**
     * HomeType 에 따른 item view
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when(items[position].type){
            HomeType.MainEvent->return R.layout.customlayout_main_item_mainevent
            HomeType.SubTitleList->return R.layout.customlayout_main_item_subtitlelist
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
            HomeType.MainEvent->{
                val binding : CustomlayoutMainItemMaineventBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return MainEventViewHolder(binding.root, binding)
            }
            HomeType.SubTitleList->{
                val binding : CustomlayoutMainItemSubtitlelistBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SubTitleViewHolder(binding.root, binding)
            }
            HomeType.Dummy->{
                val binding : CustomlayoutMainItemDummyBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return DummyViewHolder(binding.root, binding)
            }
            else ->{
                val binding : CustomlayoutMainItemPaddingBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PaddingViewHolder(binding.root, binding)
            }
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: MainBaseModel, position: Int) {
        viewHolder.bind(model as HomeListViewModel, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size


    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : HomeListViewModel, position : Int, item : MainBaseModel)
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PaddingViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPaddingBinding) : ListViewHolder(containerView, binding){
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) { }
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class MainEventViewHolder(private val containerView: View, val binding: CustomlayoutMainItemMaineventBinding) : ListViewHolder(containerView, binding){
        var currentAdIndex : Int = -1
        val mHandler : Handler = Handler((containerView.context as Activity).mainLooper)
        var eventListSize = 0

        private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<EventData>? = null
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) {
        }
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is MainEvent){
                var data = item
                if(infiniteAdapter == null){
                    infiniteAdapter = object : InfiniteGeneralFixedPagerAdapter<EventData>(data.eventList, true, true){
                        override fun getPageView(paramViewGroup: ViewGroup, paramInt: Int, item: EventData): View {
                            val imageView = ImageView(paramViewGroup.context)
                            imageView.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
                            imageView.setBackgroundResource(data.eventList[paramInt].imgRes)
                            //ImageUtil.loadImage(Glide.with(containerView.context as Activity),imageView, data.eventList[paramInt].imgPath)
                            return imageView
                        }
                        override fun getPageTitle(position: Int): CharSequence? = ""
                        override fun getPagerIcon(position: Int): Int = 0
                        override fun getPagerIconBackground(position: Int): Int = 0
                    }
                    binding.viewPager.adapter = infiniteAdapter

                    if(currentAdIndex == -1){
                        eventListSize = binding.viewPager.offsetAmount
                        currentAdIndex = binding.viewPager.currentItem
                    }
                    binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrollStateChanged(state: Int) {
                            if(state == ViewPager.SCROLL_STATE_IDLE){
                                homeRolling()
                            }else{
                                mHandler.removeCallbacks(homeAdRolling)
                            }
                        }
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }
                        override fun onPageSelected(position: Int) {
                            currentAdIndex = position
                        }
                    })
                    if(position == 0){
                        homeRolling()
                    }
                }
            }
        }
        var homeAdRolling =  Runnable {
            try{
                if(mHandler != null && binding.viewPager!=null){
                    if(currentAdIndex > (eventListSize * 1000) -100) currentAdIndex = (eventListSize*1000) / 2
                    binding.viewPager.setCurrentItemSmooth(currentAdIndex+1)
                }
            }catch (e:Exception){
                if(CustomLog.flag)CustomLog.E(e)
            }
            homeRolling()
         }

        fun homeRolling(){
            try{
                mHandler.removeCallbacks(homeAdRolling)
                mHandler.postDelayed(homeAdRolling,5000)
            }catch (e:Exception){
                mHandler.removeCallbacks(homeAdRolling)
                if(CustomLog.flag)CustomLog.E(e)
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

        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }

        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is SubTitleItemList){
                if(width == 0){
                    val matrix = DisplayMetrics()
                    (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(viewModel.context, 13)) / 2
                    height = width
                    margin = CommonViewUtil.dipToPixel(viewModel.context, 3)
                    layoutHeight = height + CommonViewUtil.dipToPixel(viewModel.context, 120)
                }
                // Thumbnail
                var size = item.listSize[item.currentSubTitleIndex] - 1
                binding.title.text = item.title
                setTabLayout(position)
                val model : HomeListViewModel = this@HomeListAdapter.model as HomeListViewModel
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
                for (i in 0..size){
                    var data : Deal?  = when(item.currentSubTitleIndex){
                        0->{
                            if(item.homeDeal.allList!!.size <= i) null
                            else item.homeDeal.allList!![i]
                        }
                        1->{
                            if(item.homeDeal.womenList!!.size <= i) null
                            else item.homeDeal.womenList!![i]
                        }
                        2->{
                            if(item.homeDeal.menList!!.size <= i) null
                            else item.homeDeal.menList!![i]
                        }
                        3->{
                            if(item.homeDeal.kidsList!!.size <= i) null
                            else item.homeDeal.kidsList!![i]
                        }
                        else -> {
                            if(item.homeDeal.allList!!.size <= i) null
                            else item.homeDeal.allList!![i]
                        }
                    }

                    if(data != null){
                        itemlayout[i].contentDescription = data.dealId.toString()
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
                            textPrice[i].text = String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.toInt()))
                            textPriceSalePer[i].text = String.format((containerView.context as Activity).getString(R.string.product_price_sale_per), data.discountRate)
                            text_pricediscount[i].visibility = View.VISIBLE
                            text_pricediscount[i].paintFlags = (text_pricediscount[i].paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
                            text_pricediscount[i].text = String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.toInt()))
                        } else {
                            textPrice[i].text = String.format((containerView.context as Activity).getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.toInt()))
                            text_pricediscount[i].visibility = View.GONE
                        }
                        // Ship
                        //if(CustomLog.flag)CustomLog.L("HomeListAdapter",item.title,"SubTitleViewHolder textShipFree","data.freeShipping",data.freeShipping)
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
            val model : HomeListViewModel = this@HomeListAdapter.model as HomeListViewModel
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
    class DummyViewHolder(private val containerView: View, val binding: CustomlayoutMainItemDummyBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is DummyImage){
                var metrics = DisplayMetrics()
                (containerView.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
                binding.heightLayout.setmHeight((item.imageHeight * metrics.density).toInt())
                binding.heightLayout.setmWidth((360 * metrics.density).toInt())
                binding.heightLayout.setBackgroundResource(item.imageRes)

            }
        }
    }


}

