package io.temco.guhada.view.adapter.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.event.LuckyDrawTitleList
import io.temco.guhada.data.model.event.Status
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.viewmodel.main.LuckyDrawViewModel
import io.temco.guhada.databinding.*
import io.temco.guhada.view.activity.LuckyEventDialogActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.custom.FixedSpeedScroller
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import io.temco.guhada.view.viewpager.InfiniteGeneralFixedPagerAdapter
import io.temco.guhada.view.viewpager.InfiniteViewPager
import kotlinx.coroutines.Runnable
import java.util.*


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 recycler adapter
 *
 *
 * ViewHolder 종류
 * - LuckyDrawTitleViewHolder: 메인 리스트의 더미 화면
 * - LuckyDrawEventViewHolder: 메인 리스트의 더미 화면
 * - LuckyDrawFooterViewHolder: 메인 리스트의 더미 화면
 * - MainEventViewHolder: 메인 리스트의 event viewpager
 * - TermViewHolder: 메인 리스트의 더미 화면
 * - PaddingViewHolder: 메인 리스트의 빈 화면
 */
class LuckyDrawAdapter(private val model: LuckyDrawViewModel, val list: ArrayList<MainBaseModel>) :
        CommonRecyclerAdapter<MainBaseModel, LuckyDrawAdapter.ListViewHolder>(list) {

    var isViewPagerIdle = false
    lateinit var mHandler: Handler
    lateinit var customRunnableMap: WeakHashMap<Int, Runnable>

    // 타일딜 타임 쓰레드 전체 종료
    fun clearRunnable() {
        if (CustomLog.flag) CustomLog.L("LuckyDrawAdapter", "clearRunnable")
        if (::customRunnableMap.isInitialized) {
            for (r in customRunnableMap.values) {
                if (::mHandler.isInitialized) {
                    if (CustomLog.flag) CustomLog.L("LuckyDrawAdapter", "clearRunnable---")
                    mHandler.removeCallbacks(r)
                }
            }
            customRunnableMap.clear()
        }
    }

    /**
     * HomeType 에 따른 item view  TextUtils
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when (items[position].type) {
            HomeType.LuckyDrawTitle -> return R.layout.customlayout_main_item_luckydrawtitle
            HomeType.LuckyDrawEvent -> return R.layout.customlayout_main_item_luckydrawevent
            HomeType.LuckyDrawFooter -> return R.layout.customlayout_main_item_luckydrawfooter
            HomeType.MainEvent -> return R.layout.customlayout_main_item_mainevent
            HomeType.Dummy -> return R.layout.customlayout_main_item_timedaeldummy
            HomeType.Keyword -> return R.layout.customlayout_main_item_keyword
            HomeType.TimeDeal -> return R.layout.customlayout_main_item_timedeal
            HomeType.Footer -> return R.layout.item_terminfo_footer
            else -> return R.layout.customlayout_main_item_padding
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        if (!::mHandler.isInitialized) mHandler = Handler()
        if (!::customRunnableMap.isInitialized) customRunnableMap = WeakHashMap()
        val layoutInflater = LayoutInflater.from(parent.context)
        when (items[viewType].type) {
            HomeType.LuckyDrawTitle -> {
                val binding: CustomlayoutMainItemLuckydrawtitleBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return LuckyDrawTitleViewHolder(binding.root, binding)
            }
            HomeType.LuckyDrawEvent -> {
                val binding: CustomlayoutMainItemLuckydraweventBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return LuckyDrawEventViewHolder(binding.root, binding)
            }
            HomeType.LuckyDrawFooter -> {
                val binding: CustomlayoutMainItemLuckydrawfooterBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return LuckyDrawFooterViewHolder(binding.root, binding)
            }
            HomeType.MainEvent -> {
                val binding: CustomlayoutMainItemMaineventBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return MainEventViewHolder(binding.root, binding)
            }
            HomeType.Footer -> {
                val binding: ItemTerminfoFooterBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return TermViewHolder(binding.root, binding)
            }
            else -> {
                val binding: CustomlayoutMainItemPaddingBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return PaddingViewHolder(binding.root, binding)
            }
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: MainBaseModel, position: Int) {
        viewHolder.bind(model as LuckyDrawViewModel, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size


    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView) {
        abstract fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel)
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    inner class LuckyDrawTitleViewHolder(private val containerView: View, val binding: CustomlayoutMainItemLuckydrawtitleBinding) : ListViewHolder(containerView, binding) {

        var currentAdIndex: Int = -1
        var eventListSize = 0
        private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<LuckyDrawTitleList>? = null

        private lateinit var layoutInflater: LayoutInflater
        private lateinit var imgs: Array<ImageView>

        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}
        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {
            if (item is LuckyDrawTitle) {
                var data = item
                var metrics = DisplayMetrics()
                binding.viewModel = model
                isViewPagerIdle = true

                if (CustomLog.flag) CustomLog.L("LuckyDrawTitleViewHolder", "item-----", item)

                if (item.eventTime.remainedTimeForStart <= 0) {
                    val runnable = customRunnableMap[1]
                    if (runnable != null) mHandler.removeCallbacks(runnable)
                    binding.linearlayoutLuckytitleTimer.visibility = View.VISIBLE
                    val current = Calendar.getInstance().timeInMillis /*- (item.eventTime.now*1000 - Calendar.getInstance().timeInMillis)*/
                    item.displayTime = item.expiredTimeLong - current
                    // 스레드 시작
                    if (!::imgs.isInitialized) imgs = arrayOf(binding.imageviewLuckytitleTimerH0, binding.imageviewLuckytitleTimerH1,
                            binding.imageviewLuckytitleTimerM0, binding.imageviewLuckytitleTimerM1,
                            binding.imageviewLuckytitleTimerS0, binding.imageviewLuckytitleTimerS1)
                    if (runnable == null) customRunnableMap[1] = CustomRunnable(item.displayTime, imgs, mHandler)
                    val startDelayMS: Long = 1000L - Calendar.getInstance().get(Calendar.MILLISECOND) // 시작 delay millisecond Time , 0초에 시작하기 위해
                    mHandler.postDelayed(customRunnableMap[1], startDelayMS)

                } else binding.linearlayoutLuckytitleTimer.visibility = View.GONE

                (containerView.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
                if (infiniteAdapter == null) {
                    infiniteAdapter = object : InfiniteGeneralFixedPagerAdapter<LuckyDrawTitleList>(data.eventTile, true, true) {
                        override fun getPageView(paramViewGroup: ViewGroup, paramInt: Int, item: LuckyDrawTitleList): View {
                            if (!::layoutInflater.isInitialized) layoutInflater = LayoutInflater.from(viewModel.context)
                            val binding: ItemLuckydrawIntroPagerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_luckydraw_intro_pager, paramViewGroup, false)
                            ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageThumb, item.titleImageUrl)
                            binding.textLuckypager1.text = DateUtil.getCalendarToString(Type.DateFormat.TYPE_6, item.requestFromAt)
                            binding.textLuckypager3.text = item.title
                            return binding.root
                        }

                        override fun getPageTitle(position: Int): CharSequence? = ""
                        override fun getPagerIcon(position: Int): Int = 0
                        override fun getPagerIconBackground(position: Int): Int = 0
                    }
                    binding.viewPager.adapter = infiniteAdapter
                    /*binding.viewPager.setOnItemClickListener(object : InfiniteViewPager.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            model.recyclerView.smoothScrollToPosition(position+1)
                        }
                    })*/
                    /*if (currentAdIndex == -1) {
                        eventListSize = binding.viewPager.offsetAmount
                        currentAdIndex = binding.viewPager.currentItem
                    }*/
                    eventListSize = binding.viewPager.offsetAmount
                    currentAdIndex = binding.viewPager.currentItem
                    binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {
                            isViewPagerIdle = state == ViewPager.SCROLL_STATE_IDLE
                        }

                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                        override fun onPageSelected(position: Int) {
                            currentAdIndex = position
                            binding.tabLayout.setScrollPosition(binding.viewPager.realCurrentItem, 0f, true)
                        }
                    })
                    if (!::layoutInflater.isInitialized) layoutInflater = LayoutInflater.from(viewModel.context)
                    for (i in 0 until data.eventTile.size) {
                        val dot = layoutInflater.inflate(R.layout.item_tablayout_dot, null)
                        var tab = binding.tabLayout.newTab().setCustomView(dot)
                        binding.tabLayout.addTab(tab)
                        if (i == 0) tab.select()
                    }

                    var mScroller = ViewPager::class.java.getDeclaredField("mScroller")
                    mScroller.isAccessible = true
                    var sInterpolator = OvershootInterpolator()
                    var scroller = FixedSpeedScroller(binding.viewPager.getContext(), sInterpolator)
                    mScroller.set(binding.viewPager, scroller)
                    binding.viewPager.setPageTransformer(false, object : ViewPager.PageTransformer {
                        override fun transformPage(page: View, position: Float) {
                            if (position <= -1.0F || position >= 1.0F) {
                                //page.translationX = page.width * position
                                page.alpha = 0.0F
                                /*if (position < -1) {
                                    page.translationX = page.width * position
                                } else if (position > 0 && position <= 1) {
                                    page.translationX = -page.width * position
                                }*/
                            } else if (position == 0.0F) {
                                //page.translationX = (page.width * position)
                                page.alpha = 1.0F
                            } else {
                                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                                //page.translationX = (page.width * position)
                                page.alpha = (1.0F - (Math.abs(position)))
                                //page.translationX = page.width * position
                            }
                            /*page.translationX = page.width * -position
                            if(position <= -1.0F || position >= 1.0F) {
                                page.alpha = 0.0F
                            } else if( position == 0.0F ) {
                                page.alpha = 1.0F
                            } else {
                                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                                page.alpha = 1.0F - Math.abs(position)
                            }*/
                        }
                    })
                }
                if (position == 0 && item.eventTile.size > 1) {
                    homeRolling()
                }
            }
        }

        var homeAdRolling = Runnable {
            try {
                if (mHandler != null && binding.viewPager != null && isViewPagerIdle) {
                    if (currentAdIndex > (eventListSize * 1000) - 100) currentAdIndex = (eventListSize * 1000) / 2
                    binding.viewPager.setCurrentItemSmooth(currentAdIndex + 1)
                }
            } catch (e: Exception) {
                if (CustomLog.flag) CustomLog.E(e)
            }
            homeRolling()
        }

        fun homeRolling() {
            try {
                mHandler.removeCallbacks(homeAdRolling)
                mHandler.postDelayed(homeAdRolling, 5000)
                customRunnableMap.put(0, homeAdRolling)
            } catch (e: Exception) {
                mHandler.removeCallbacks(homeAdRolling)
                if (CustomLog.flag) CustomLog.E(e)
            }
        }

    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class LuckyDrawEventViewHolder(private val containerView: View, val binding: CustomlayoutMainItemLuckydraweventBinding) : ListViewHolder(containerView, binding) {
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}
        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {
            if (item is LuckyDrawEvent) {
                binding.viewModel = viewModel
                binding.luckydraw = item.eventData
                binding.index = position
                if (TextUtils.isEmpty(binding.imageThumb.contentDescription) || binding.imageThumb.contentDescription.toString() != item.eventData.imageUrl) {
                    ImageUtil.loadImage(containerView.context, binding.imageThumb, item.eventData.imageUrl, CommonViewUtil.convertDpToPixel(240, containerView.context), CommonViewUtil.convertDpToPixel(240, containerView.context))
                    binding.imageThumb.contentDescription = item.eventData.imageUrl
                }

                binding.textLuckydrawInfoDesc1.text = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, item.eventData.requestFromAt) + " - " +
                        DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, item.eventData.requestToAt))
                binding.textLuckydrawInfoDesc2.text = DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, item.eventData.winnerAnnouncementAt)
                binding.textLuckydrawInfoDesc3.text = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, item.eventData.winnerBuyFromAt) + " - " +
                        DateUtil.getCalendarToString(Type.DateFormat.TYPE_7, item.eventData.winnerBuyToAt))


                binding.layoutLuckydraw.setOnClickListener {
                    item.eventData.isDetailShow = !item.eventData.isDetailShow
                    if (item.eventData.isDetailShow) {
                        binding.layoutLuckydrawInfo.visibility = View.VISIBLE
                        binding.imageviewLuckydrawInfoarrow.setImageResource(R.drawable.detail_icon_arrow_close)
                    } else {
                        binding.layoutLuckydrawInfo.visibility = View.GONE
                        binding.imageviewLuckydrawInfoarrow.setImageResource(R.drawable.detail_icon_arrow_open)
                    }
                    //viewModel.getListAdapter().notifyItemChanged(position)
                }

                if (position == 1) {
                    binding.relativeLuckydrawRibbon.visibility = View.VISIBLE
                    binding.textLuckydrawRibbon.text = DateUtil.getCalendarToString(Type.DateFormat.TYPE_6, item.eventData.requestFromAt)
                } else {
                    binding.relativeLuckydrawRibbon.visibility = View.GONE
                }

                binding.textStatus.setOnClickListener(null)
                when (item.eventData.statusCode) {
                    Status.START.code -> {
                        binding.textStatus.setBackgroundResource(R.drawable.background_color_round_f43143)
                        binding.textStatus.setTextColor(Color.parseColor("#ffffff"))
                        binding.textStatus.setOnClickListener {
                            var intent = Intent(viewModel.context as Activity, LuckyEventDialogActivity::class.java)
                            intent.putExtra("eventData", item.eventData)
                            (viewModel.context as Activity).startActivityForResult(intent, Flag.RequestCode.LUCKY_DRAW_EVENT)
                        }
                    }
                    Status.NORMAL.code, Status.READY.code -> {
                        binding.textStatus.setBackgroundResource(R.drawable.background_color_round_border_f43143)
                        binding.textStatus.setTextColor(Color.parseColor("#f43143"))
                    }
                    Status.OUT_OF_TIME.code -> {
                        binding.textStatus.setBackgroundResource(R.drawable.background_color_round_c9c9c9)
                        binding.textStatus.setTextColor(Color.parseColor("#ffffff"))
                    }
                    Status.WINNER_ANNOUNCEMENT.code -> {
                        binding.textStatus.setBackgroundResource(R.drawable.background_color_round_13182e)
                        binding.textStatus.setTextColor(Color.parseColor("#ffffff"))
                        binding.textStatus.setOnClickListener {
                            var intent = Intent(viewModel.context as Activity, LuckyEventDialogActivity::class.java)
                            intent.putExtra("eventData", item.eventData)
                            (viewModel.context as Activity).startActivityForResult(intent, Flag.RequestCode.LUCKY_DRAW_EVENT)
                        }
                    }
                    Status.REQUESTED.code -> {
                        binding.textStatus.setBackgroundResource(R.drawable.background_color_round_c9c9c9)
                        binding.textStatus.setTextColor(Color.parseColor("#ffffff"))
                    }
                }
            }
        }
    }

    // 럭키드로우
    override fun onViewRecycled(holder: ListViewHolder) {
        super.onViewRecycled(holder)

//        if(holder.binding is CustomlayoutMainItemLuckydraweventBinding)
//            ImageUtil.clearGlide(holder.binding.root.context, (holder.binding as CustomlayoutMainItemLuckydraweventBinding).imageThumb)
    }

    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class LuckyDrawFooterViewHolder(private val containerView: View, val binding: CustomlayoutMainItemLuckydrawfooterBinding) : ListViewHolder(containerView, binding) {
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}
        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {}
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PaddingViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPaddingBinding) : ListViewHolder(containerView, binding) {
        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {}
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class MainEventViewHolder(private val containerView: View, val binding: CustomlayoutMainItemMaineventBinding) : ListViewHolder(containerView, binding) {
        var currentAdIndex: Int = -1
        val mHandler: Handler = Handler((containerView.context as Activity).mainLooper)
        var eventListSize = 0

        private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<EventData>? = null
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}

        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {
            if (item is MainEvent) {
                var data = item
                if (infiniteAdapter == null) {
                    infiniteAdapter = object : InfiniteGeneralFixedPagerAdapter<EventData>(data.eventList, true, true) {
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

                    if (currentAdIndex == -1) {
                        eventListSize = binding.viewPager.offsetAmount
                        currentAdIndex = binding.viewPager.currentItem
                    }
                    binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {
                            if (state == ViewPager.SCROLL_STATE_IDLE) {
                                homeRolling()
                            } else {
                                mHandler.removeCallbacks(homeAdRolling)
                            }
                        }

                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                        override fun onPageSelected(position: Int) {
                            currentAdIndex = position
                        }
                    })
                    if (position == 0) {
                        homeRolling()
                    }
                }
            }
        }

        var homeAdRolling = Runnable {
            try {
                if (mHandler != null && binding.viewPager != null) {
                    if (currentAdIndex > (eventListSize * 1000) - 100) currentAdIndex = (eventListSize * 1000) / 2
                    binding.viewPager.setCurrentItemSmooth(currentAdIndex + 1)
                }
            } catch (e: Exception) {
                if (CustomLog.flag) CustomLog.E(e)
            }
            homeRolling()
        }

        fun homeRolling() {
            try {
                mHandler.removeCallbacks(homeAdRolling)
                mHandler.postDelayed(homeAdRolling, 5000)
            } catch (e: Exception) {
                mHandler.removeCallbacks(homeAdRolling)
                if (CustomLog.flag) CustomLog.E(e)
            }
        }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class TermViewHolder(private val containerView: View, val binding: ItemTerminfoFooterBinding) : ListViewHolder(containerView, binding) {
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}
        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {
            binding.setOnClickListener {
                when (it.id) {
                    R.id.textview_term_claim -> {
                        CommonUtilKotlin.startActivityUserClaimGuhada(containerView.context as AppCompatActivity)
                    }
                    R.id.textview_term_terms -> {
                        CommonUtilKotlin.startTermsPurchase(containerView.context as Activity)
                    }
                    R.id.textview_term_advise -> {
                    }
                    R.id.textview_term_privacy_terms -> {
                        CommonUtilKotlin.startTermsPersonal(containerView.context as Activity)
                    }
                    R.id.textview_term_guarantee -> {
                        CommonUtilKotlin.startTermsGuarantee(containerView.context as Activity)
                    }
                    R.id.textview_term_company -> {
                        CommonUtilKotlin.startTermsCompany(containerView.context as Activity)
                    }
                    R.id.textview_term_call -> {
                        val phone = (itemView.context as Activity).resources.getString(R.string.information_company_call)
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                        (itemView.context as Activity).startActivity(intent)
                    }
                    R.id.textview_term_partner, R.id.textview_term_email -> {
                        var email = Intent(Intent.ACTION_SEND).apply {
                            type = "plain/Text"
                            var email: Array<String> = arrayOf("help@guhada.com")
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
    class DummyViewHolder(private val containerView: View, val binding: CustomlayoutMainItemTimedaeldummyBinding) : ListViewHolder(containerView, binding) {
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position: Int) {}
        override fun bind(viewModel: LuckyDrawViewModel, position: Int, item: MainBaseModel) {
            if (item is DummyImage) {
                binding.imageDummy.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, CommonViewUtil.dipToPixel(itemView.context, item.imageHeight))
                binding.imageDummy.setBackgroundColor(Color.TRANSPARENT)
                //binding.imageDummy.setBackgroundResource(item.imageRes)
            }
        }
    }

    /**
     * 타임딜 타이머
     * remainEndAt: ms
     * @author Hyeyeon Park
     * @since 2019.10.24
     */
    class CustomRunnable(var remainEndAt: Long, var image: Array<ImageView>, var handler: Handler) : Runnable {
        val imgRes = arrayListOf(R.drawable.number_0, R.drawable.number_1, R.drawable.number_2, R.drawable.number_3,
                R.drawable.number_4, R.drawable.number_5, R.drawable.number_6, R.drawable.number_7, R.drawable.number_8, R.drawable.number_9)

        override fun run() {
            var time = DateUtil.getTimerText(remainEndAt).replace(":", "")
            image[0].setBackgroundResource(imgRes[time.substring(0, 1).toInt()])
            image[1].setBackgroundResource(imgRes[time.substring(1, 2).toInt()])

            image[2].setBackgroundResource(imgRes[time.substring(2, 3).toInt()])
            image[3].setBackgroundResource(imgRes[time.substring(3, 4).toInt()])

            image[4].setBackgroundResource(imgRes[time.substring(4, 5).toInt()])
            image[5].setBackgroundResource(imgRes[time.substring(5, 6).toInt()])
            var startDelayMS: Long = 1000L - Calendar.getInstance().get(Calendar.MILLISECOND) // 시작 delay millisecond Time , 0초에 시작하기 위해
            if (startDelayMS <= 10L) startDelayMS = 1000L
            remainEndAt -= 1000
            if (remainEndAt > 0) handler.postDelayed(this, startDelayMS)
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("cancelLine")
        fun TextView.bindCancelLine(addCancelLine: Boolean) {
            if (addCancelLine)
                this.paintFlags = (this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
        }

    }

}


