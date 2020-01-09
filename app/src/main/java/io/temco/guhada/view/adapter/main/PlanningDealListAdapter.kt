 package io.temco.guhada.view.adapter.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.viewmodel.main.PlanningDealListViewModel
import io.temco.guhada.databinding.*
import io.temco.guhada.view.activity.base.BaseActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.fragment.ListBottomSheetFragment
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import io.temco.guhada.view.viewpager.InfiniteGeneralFixedPagerAdapter


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 recycler adapter
 */
class PlanningDealListAdapter(private val model : PlanningDealListViewModel, list : ArrayList<MainBaseModel>) :
        CommonRecyclerAdapter<MainBaseModel, PlanningDealListAdapter.ListViewHolder>(list){
    /**
     * HomeType 에 따른 item view  TextUtils
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when(items[position].type){
            HomeType.PlanningHeader->return R.layout.customlayout_main_item_planningheader
            HomeType.PlanningList->return R.layout.customlayout_main_item_planning
            HomeType.MainEvent->return R.layout.customlayout_main_item_mainevent
            HomeType.Footer->return R.layout.item_terminfo_footer
            else ->return R.layout.customlayout_main_item_padding
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(items[viewType].type){
            HomeType.PlanningHeader->{
                val binding : CustomlayoutMainItemPlanningheaderBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return EventHeaderViewHolder(binding.root, binding)
            }
            HomeType.PlanningList->{
                val binding : CustomlayoutMainItemPlanningBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return EventListViewHolder(binding.root, binding)
            }
            HomeType.MainEvent->{
                val binding : CustomlayoutMainItemMaineventBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return MainEventViewHolder(binding.root, binding)
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
        viewHolder.bind(model as PlanningDealListViewModel, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size



    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class EventHeaderViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPlanningheaderBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) {
            if(item is PlanningHeader){
                binding.textMaineventTotal.text = item.planningHeader.count.toString()
                val selectedStr = viewModel.mSortFilterLabel[viewModel.mFilterIndex]
                binding.textMaineventFilter.text = selectedStr
                binding.setClickListener {
                    val bottomSheet = ListBottomSheetFragment(binding.root.context).apply {
                        this.mList = viewModel.mSortFilterLabel
                        this.mTitle = "정렬 선택"
                        this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                            override fun onItemClick(position: Int) {
                                viewModel.mFilterIndex = position
                                viewModel.getPlanningList()
                            }
                            override fun onClickClose() {
                                this@apply.dismiss()
                            }
                        }
                    }
                    if ((viewModel.context as BaseActivity).supportFragmentManager != null) bottomSheet.show((viewModel.context as BaseActivity).supportFragmentManager!!, "")
                }
            }
        }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class EventListViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPlanningBinding) : ListViewHolder(containerView, binding){
        internal var width = 0
        internal var height = 0
        internal lateinit var request : RequestOptions

        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) {
            if(item is PlanningList){
                if(!::request.isInitialized){
                    request = RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(width,width)
                            .downsample(DownsampleStrategy.AT_MOST)
                }
                if(width == 0 && height == 0){
                    Glide.with(viewModel.context).load(item.planningData.imgUrlM)
                            .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888).downsample(DownsampleStrategy.AT_MOST))
                            .into(object : SimpleTarget<Drawable>(){
                                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                    if(width == 0){
                                        val matrix = DisplayMetrics()
                                        (viewModel.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                                        width = matrix.widthPixels
                                        var ra : Float = width / resource.minimumWidth.toFloat()
                                        height  = (ra * resource.minimumHeight.toFloat()).toInt()
                                        binding.imageviewMaineventEvent.layoutParams = LinearLayout.LayoutParams(width,height)
                                    }
                                    //binding.imageBanner.setImageDrawable(resource)
                                    setImage(binding.imageviewMaineventEvent, item.planningData.imgUrlM)
                                }
                            }
                    )
                }else{
                    binding.imageviewMaineventEvent.layoutParams = LinearLayout.LayoutParams(width,height)
                    setImage(binding.imageviewMaineventEvent, item.planningData.imgUrlM)
                }
                /*if(TextUtils.isEmpty(item.planningData.bgColor)){
                    binding.imageviewMaineventEvent.scaleType = ImageView.ScaleType.FIT_XY
                }else {
                    binding.imageviewMaineventEvent.scaleType = ImageView.ScaleType.CENTER
                }
                ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageviewMaineventEvent, item.planningData.imgUrlM)*/
                binding.textviewMaineventTitle.text = item.planningData.eventTitle
                binding.textviewMaineventDate.text = (item.planningData.eventStartDate.split(" ")[0] + " ~ " +item.planningData.eventEndDate.split(" ")[0])
                binding.layoutEventContent.setOnClickListener {
                    if(!TextUtils.isEmpty(item.planningData.eventProgress) && "진행중" == item.planningData.eventProgress){
                        CommonUtilKotlin.movePlanningDealDetail(viewModel.context as Activity,item.planningData.id,"")
                    }else if(!TextUtils.isEmpty(item.planningData.eventProgress) && "종료" == item.planningData.eventProgress){
                        CommonViewUtil.showDialog(viewModel.context as BaseActivity,"종료된 기획전 입니다.",isCancelBtn = false,isFinish = false)
                    }
                }
                try{
                    if(!TextUtils.isEmpty(item.planningData.bgColor))
                        binding.imageviewMaineventEvent.setBackgroundColor(Color.parseColor(item.planningData.bgColor))
                }catch (e : Exception){
                    if(CustomLog.flag)CustomLog.E(e)
                }
            }
        }

        private fun setImage(imageView : ImageView, imgUrl : String){
            ImageUtil.loadImage(Glide.with(containerView.context as Activity), imageView, imgUrl ,request)
        }
    }
    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : PlanningDealListViewModel, position : Int, item : MainBaseModel)
    }

    /**
     * 메인 리스트에 빈 화면 view holder
     */
    class PaddingViewHolder(private val containerView: View, val binding: CustomlayoutMainItemPaddingBinding) : ListViewHolder(containerView, binding){
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) { }
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class MainEventViewHolder(private val containerView: View, val binding: CustomlayoutMainItemMaineventBinding) : ListViewHolder(containerView, binding){
        var currentAdIndex : Int = -1
        var eventListSize = 0

        private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<EventData>? = null
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) {
        }
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) {

        }
    }


    /**
     * 메인 리스트에 더미 화면 view holder
     */
    class KeywordViewHolder(private val containerView: View, val binding: CustomlayoutMainItemKeywordBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) {
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
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) {
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
        override fun bind(viewModel: PlanningDealListViewModel, position: Int, item: MainBaseModel) {
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

