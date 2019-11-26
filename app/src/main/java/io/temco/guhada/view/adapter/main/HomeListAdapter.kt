package io.temco.guhada.view.adapter.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.viewmodel.main.HomeListViewModel
import io.temco.guhada.databinding.*
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.activity.ProductFilterListActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import io.temco.guhada.view.viewpager.InfiniteGeneralFixedPagerAdapter
import io.temco.guhada.view.viewpager.InfiniteViewPager
import java.lang.ref.WeakReference


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 recycler adapter
 */
class HomeListAdapter(private val model : HomeListViewModel, list : ArrayList<MainBaseModel>) :
        CommonRecyclerAdapter<MainBaseModel, HomeListAdapter.ListViewHolder>(list){

    var isViewPagerIdle = false
    lateinit var mHandler: Handler
    lateinit var customRunnableMap: WeakReference<Runnable>

    // 쓰레드 전체 종료
    fun clearRunnable() {
        if (CustomLog.flag) CustomLog.L("HomeListAdapter", "clearRunnable")
        if (::customRunnableMap.isInitialized) {
            if (::mHandler.isInitialized) {
                if (CustomLog.flag) CustomLog.L("HomeListAdapter", "clearRunnable---")
                mHandler.removeCallbacks(customRunnableMap.get())
            }
            customRunnableMap.clear()
        }
    }
    /**
     * HomeType 에 따른 item view  TextUtils
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when(items[position].type){
            HomeType.MainEvent->return R.layout.customlayout_main_item_mainevent
            HomeType.MainBanner->return R.layout.customlayout_main_item_mainevent
            HomeType.SubTitleLayout->return R.layout.customlayout_main_item_subtitle
            HomeType.DealItemOne->return R.layout.customlayout_main_item_dealone
            HomeType.ViewMoreLayout->return R.layout.customlayout_main_item_viewmore
            HomeType.Dummy->return R.layout.customlayout_main_item_dummy
            HomeType.Keyword->return R.layout.customlayout_main_item_keyword
            HomeType.Footer->return R.layout.item_terminfo_footer
            else ->return R.layout.customlayout_main_item_padding
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        if (!::mHandler.isInitialized) mHandler = Handler((parent.context as Activity).mainLooper)
        val layoutInflater = LayoutInflater.from(parent.context)
        when(items[viewType].type){
            HomeType.MainEvent->{
                val binding : CustomlayoutMainItemMaineventBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return MainEventViewHolder(binding.root, binding)
            }
            HomeType.MainBanner->{
                val binding : CustomlayoutMainItemMaineventBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return MainBannerViewHolder(binding.root, binding)
            }
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
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class SubTitleLayoutViewHolder(private val containerView: View, val binding: CustomlayoutMainItemSubtitleBinding) : ListViewHolder(containerView, binding){
        var tabImg = arrayListOf(binding.tabImg0,binding.tabImg1,binding.tabImg2,binding.tabImg3)
        var tabTitle = arrayListOf(binding.tabTitle0,binding.tabTitle1,binding.tabTitle2,binding.tabTitle3)

        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(model: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is SubTitleLayout){
                binding.subtitle.visibility = View.VISIBLE
                binding.title.text = item.title
                setTabLayout(model, position)
                if(CustomLog.flag)CustomLog.L("SubTitleLayoutViewHolder","item index",item.index, "item currentSubTitleIndex",
                        item.currentSubTitleIndex, "subTitleIndex",item.subTitleIndex, "listSize",item.listSize[item.currentSubTitleIndex])
                binding.setClickTabListener {
                    var tabIndex = when(it.id){
                        R.id.tab_1->1
                        R.id.tab_2->2
                        R.id.tab_3->3
                        else -> 0
                    }
                    if(model.currentSubTitleIndexArray[item.subTitleIndex] != tabIndex){
                        if(CustomLog.flag)CustomLog.L("SubTitleLayoutViewHolder "+position,"item index",item.index,
                                "item currentSubTitleIndex",item.currentSubTitleIndex, "subTitleIndex",item.subTitleIndex, "listSize 0",item.listSize[tabIndex])

                        model.currentSubTitleIndexArray[item.subTitleIndex] = tabIndex
                        (model.getListAdapter().items[position] as SubTitleLayout).currentSubTitleIndex = tabIndex
                        var homeDeal = when(item.subTitleIndex){
                            1->model.bestData
                            2->model.newInData
                            else -> model.premiumData
                        }
                        setTabItems(position, tabIndex, item, homeDeal, model)
                    }
                }
            }
        }

        private fun setTabLayout(model: HomeListViewModel, position : Int) {
            val size = tabImg.size-1
            for (i in 0..size){
                if((model.getListAdapter().items[position] as SubTitleLayout).currentSubTitleIndex == i){
                    tabImg[i].setBackgroundResource(R.color.black_four)
                    tabTitle[i].setTextColor((containerView.context as Activity).resources.getColor(R.color.black_four))
                }else{
                    tabImg[i].setBackgroundResource(R.color.common_white)
                    tabTitle[i].setTextColor((containerView.context as Activity).resources.getColor(R.color.warm_grey_six))
                }
            }
        }

        private fun setTabItems(position : Int,currentSubTitleIndex : Int, item : SubTitleLayout, homeDeal: HomeDeal, model: HomeListViewModel){
            for(i in 0 until item.listSize[item.currentSubTitleIndex]){
                if(CustomLog.flag)CustomLog.L("SubTitleLayoutViewHolder "+position, "downTo index",i)
                model.getListAdapter().items.removeAt(position+1)
            }
            var dealList = when(currentSubTitleIndex){
                1-> homeDeal.womenList!!
                2-> homeDeal.menList!!
                3-> homeDeal.kidsList!!
                else->homeDeal.allList!!
            }
            var list = arrayListOf<MainBaseModel>()
            for (deal in dealList){
                var dealItem = DealItem(model.getListAdapter().items.size, HomeType.DealItemOne, deal)
                list.add(dealItem)
            }
            model.getListAdapter().items.addAll(position+1, list)
            model.getListAdapter().notifyItemRangeChanged(position, dealList.size+1)
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
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is DealItem){
                if (width == 0) {
                    val matrix = DisplayMetrics()
                    (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(viewModel.context, 24)) / 2
                    margin = CommonViewUtil.dipToPixel(viewModel.context, 8)
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

                itemView.setOnClickListener{
                    CommonUtil.startProductActivity(viewModel.context as Activity, item.deal.dealId.toLong())
                }
                val imageParams = RelativeLayout.LayoutParams(width, width)
                binding.imageThumb.setLayoutParams(imageParams)

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
                ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageThumb, item.deal.productImage.url ,request)

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
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
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
     * 메인 리스트에 event viewpager view holder
     */
    inner class MainEventViewHolder(private val containerView: View, val binding: CustomlayoutMainItemMaineventBinding) : ListViewHolder(containerView, binding){
        var currentAdIndex : Int = -1
        var eventListSize = 0
        lateinit var views : ArrayList<View>

        private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<EventData>? = null
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is MainEvent){
                var data = item
                var metrics = DisplayMetrics()
                binding.viewModel = model
                isViewPagerIdle = true
                (containerView.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
                binding.heightLayout.setmHeight((384 * metrics.density).toInt())
                binding.heightLayout.setmWidth((360 * metrics.density).toInt())
                if(infiniteAdapter == null){
                    views = arrayListOf()
                    infiniteAdapter = object : InfiniteGeneralFixedPagerAdapter<EventData>(data.eventList, true, true){
                        override fun getPageView(paramViewGroup: ViewGroup, paramInt: Int, item: EventData): View {
                            val imageView = ImageView(paramViewGroup.context)
                            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                            try {
                                imageView.setBackgroundResource(data.eventList[paramInt].imgRes)
                            } catch (e: OutOfMemoryError) {
                                imageView.setBackgroundResource(R.drawable.background_color_pinkishgrey)
                            }
                            //ImageUtil.loadImage(Glide.with(containerView.context as Activity),imageView, data.eventList[paramInt].imgPath)
                            return imageView
                        }
                        override fun getPageTitle(position: Int): CharSequence? = ""
                        override fun getPagerIcon(position: Int): Int = 0
                        override fun getPagerIconBackground(position: Int): Int = 0
                    }
                    model.mainHomeEventViewIndex.set(0)
                    binding.activityViewPagerIndicatorUnderline.removeAllViews()
                    var param = LinearLayout.LayoutParams(0,CommonViewUtil.dipToPixel(model.context,2), data.eventList.size.toFloat())
                    param.weight = 1f
                    param.rightMargin = CommonViewUtil.dipToPixel(model.context,18)
                    binding.activityViewPagerIndicatorUnderline.layoutParams = param
                    binding.activityViewPagerIndicatorUnderline.orientation = LinearLayout.HORIZONTAL
                    for (i in 0 until data.eventList.size){
                        val view = View(model.context)
                        var param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        param.weight = 1f
                        view.layoutParams = param
                        if(binding.viewPager.realCurrentItem == i) view.setBackgroundColor(Color.WHITE)
                        else view.setBackgroundColor(Color.TRANSPARENT)

                        views.add(view)
                        binding.activityViewPagerIndicatorUnderline.addView(view,LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
                    }
                    binding.viewPager.adapter = infiniteAdapter

                    if(currentAdIndex == -1){
                        eventListSize = binding.viewPager.offsetAmount
                        currentAdIndex = binding.viewPager.currentItem
                    }
                    binding.viewPager.setOnItemClickListener(object : InfiniteViewPager.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            if(CustomLog.flag)CustomLog.L("MainEventViewHolder","itemView setOnClickListener",binding.viewPager.realCurrentItem,"position",position)
                            if(!TextUtils.isEmpty(data.eventList[position].eventData)){
                                var index = data.eventList[position].eventType
                                EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.HOME_MOVE, index))
                            }
                        }
                    })
                    binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrollStateChanged(state: Int) {
                            isViewPagerIdle = state == ViewPager.SCROLL_STATE_IDLE
                        }
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }
                        override fun onPageSelected(position: Int) {
                            currentAdIndex = position
                            model.mainHomeEventViewIndex.set(binding.viewPager.realCurrentItem)
                            for (i in 0 until data.eventList.size){
                                if(binding.viewPager.realCurrentItem == i) views[i].setBackgroundColor(Color.WHITE)
                                else views[i].setBackgroundColor(Color.TRANSPARENT)
                            }
                            binding.eventIndex = (binding.viewPager.realCurrentItem+1).toString()+"/"+data.eventList.size.toString()
                        }
                    })
                }
                binding.eventIndex = (binding.viewPager.realCurrentItem+1).toString()+"/"+data.eventList.size.toString()
                if(position == 0){
                    homeRolling()
                }
            }
        }
        var homeAdRolling =  Runnable {
            try{
                if(mHandler != null && binding.viewPager!=null && isViewPagerIdle){
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
                customRunnableMap = WeakReference(homeAdRolling)
            }catch (e:Exception){
                mHandler.removeCallbacks(homeAdRolling)
                if(CustomLog.flag)CustomLog.E(e)
            }
        }

    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class MainBannerViewHolder(private val containerView: View, val binding: CustomlayoutMainItemMaineventBinding) : ListViewHolder(containerView, binding){
        var currentAdIndex : Int = -1
        var eventListSize = 0
        lateinit var views : ArrayList<View>
        internal lateinit var request : RequestOptions

        private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<MainBanner>? = null
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
            if(item is MainBannerEvent){
                var data = item
                var metrics = DisplayMetrics()
                binding.viewModel = model
                isViewPagerIdle = true
                (containerView.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
                binding.heightLayout.setmHeight((384 * metrics.density).toInt())
                binding.heightLayout.setmWidth((360 * metrics.density).toInt())
                if(infiniteAdapter == null){
                    views = arrayListOf()
                    infiniteAdapter = object : InfiniteGeneralFixedPagerAdapter<MainBanner>(data.eventList, true, true){
                        override fun getPageView(paramViewGroup: ViewGroup, paramInt: Int, item: MainBanner): View {
                            val imageView = ImageView(paramViewGroup.context)
                            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            try {
                                if(!::request.isInitialized){
                                    request = RequestOptions()
                                            .fitCenter()
                                            .format(DecodeFormat.PREFER_ARGB_8888)
                                            .downsample(DownsampleStrategy.AT_MOST)
                                }
                                // Thumbnail
                                ImageUtil.loadImage(Glide.with(containerView.context as Activity), imageView, data.eventList[paramInt].mobileImageUrl ,request)
                            } catch (e: OutOfMemoryError) {
                                imageView.setBackgroundResource(R.drawable.background_color_pinkishgrey)
                            }
                            if(!TextUtils.isEmpty(data.eventList[paramInt].backgroundColor)){
                                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                                imageView.setBackgroundColor(Color.parseColor(data.eventList[paramInt].backgroundColor))
                            }else{
                                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                            //ImageUtil.loadImage(Glide.with(containerView.context as Activity),imageView, data.eventList[paramInt].imgPath)
                            return imageView
                        }
                        override fun getPageTitle(position: Int): CharSequence? = ""
                        override fun getPagerIcon(position: Int): Int = 0
                        override fun getPagerIconBackground(position: Int): Int = 0
                    }
                    model.mainHomeEventViewIndex.set(0)
                    binding.activityViewPagerIndicatorUnderline.removeAllViews()
                    var param = LinearLayout.LayoutParams(0,CommonViewUtil.dipToPixel(model.context,2), data.eventList.size.toFloat())
                    param.weight = 1f
                    param.rightMargin = CommonViewUtil.dipToPixel(model.context,18)
                    binding.activityViewPagerIndicatorUnderline.layoutParams = param
                    binding.activityViewPagerIndicatorUnderline.orientation = LinearLayout.HORIZONTAL
                    for (i in 0 until data.eventList.size){
                        val view = View(model.context)
                        var param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        param.weight = 1f
                        view.layoutParams = param
                        if(binding.viewPager.realCurrentItem == i) view.setBackgroundColor(Color.WHITE)
                        else view.setBackgroundColor(Color.TRANSPARENT)

                        views.add(view)
                        binding.activityViewPagerIndicatorUnderline.addView(view,LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
                    }
                    binding.viewPager.adapter = infiniteAdapter

                    if(currentAdIndex == -1){
                        eventListSize = binding.viewPager.offsetAmount
                        currentAdIndex = binding.viewPager.currentItem
                    }
                    binding.viewPager.setOnItemClickListener(object : InfiniteViewPager.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            if(CustomLog.flag)CustomLog.L("MainEventViewHolder","itemView setOnClickListener",binding.viewPager.realCurrentItem,"position",position)
                            if(!TextUtils.isEmpty(data.eventList[position].link) && "/" != data.eventList[position].link){
                                var link : String = data.eventList[position].mobileAppLink
                                if (CustomLog.flag) CustomLog.L("SchemeActivity", "link", link)
                                //link = "guhada://client?pg_state=search&arg1=2019%20%EA%B8%B0%ED%9A%8D%EC%A0%84%20%ED%8C%A8%EB%94%A9"
                                if (CustomLog.flag) CustomLog.L("SchemeActivity", "link", link)
                                if(link.startsWith("guhada://client",true)){
                                    val uriData : Uri = Uri.parse(link)
                                    val pgState = uriData.getQueryParameter("pg_state")
                                    val arg1 = uriData.getQueryParameter("arg1")?:""
                                    val arg2 = uriData.getQueryParameter("arg2")?:""
                                    if(!TextUtils.isEmpty(pgState)) {
                                        if (CustomLog.flag) CustomLog.L("SchemeActivity", "pgState", pgState)
                                        if (CustomLog.flag) CustomLog.L("SchemeActivity", "arg1", arg1)
                                        if (CustomLog.flag) CustomLog.L("SchemeActivity", "arg2", arg2)
                                    }
                                    CommonUtilKotlin.moveEventPage(model.context as Activity, pgState,arg1,true,false)
                                }else{
                                    CommonUtilKotlin.moveEventPage(model.context as Activity, link,"",true,false)
                                }
                                //EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.HOME_MOVE, index))
                            }
                        }
                    })
                    binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrollStateChanged(state: Int) {
                            isViewPagerIdle = state == ViewPager.SCROLL_STATE_IDLE
                        }
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }
                        override fun onPageSelected(position: Int) {
                            currentAdIndex = position
                            model.mainHomeEventViewIndex.set(binding.viewPager.realCurrentItem)
                            for (i in 0 until data.eventList.size){
                                if(binding.viewPager.realCurrentItem == i) views[i].setBackgroundColor(Color.WHITE)
                                else views[i].setBackgroundColor(Color.TRANSPARENT)
                            }
                            binding.eventIndex = (binding.viewPager.realCurrentItem+1).toString()+"/"+data.eventList.size.toString()
                        }
                    })
                }
                binding.eventIndex = (binding.viewPager.realCurrentItem+1).toString()+"/"+data.eventList.size.toString()
                if(position == 0){
                    homeRolling()
                }
            }
        }
        var homeAdRolling =  Runnable {
            try{
                if(mHandler != null && binding.viewPager!=null && isViewPagerIdle){
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
                customRunnableMap = WeakReference(homeAdRolling)
            }catch (e:Exception){
                mHandler.removeCallbacks(homeAdRolling)
                if(CustomLog.flag)CustomLog.E(e)
            }
        }

    }

    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class KeywordViewHolder(private val containerView: View, val binding: CustomlayoutMainItemKeywordBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
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
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
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
        override fun bind(viewModel: HomeListViewModel, position: Int, item: MainBaseModel) {
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

