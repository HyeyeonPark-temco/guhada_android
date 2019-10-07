package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Inquiry
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimListItemViewModel
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimViewModel
import io.temco.guhada.databinding.ItemMoreListBinding
import io.temco.guhada.databinding.ItemMypageClaimListBinding
import io.temco.guhada.view.activity.WriteClaimActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import java.util.*
import kotlin.collections.ArrayList

class MyPageClaimAdapter(private val model: ViewModel, list: ArrayList<MyPageClaim.Content>) :
        CommonRecyclerAdapter<MyPageClaim.Content, CommonRecyclerAdapter.ListViewHolder<MyPageClaim.Content>>(list) {

    override fun getItemViewType(position: Int): Int {
        if(items[position].inquiry.id != 0L){
            return 1
        }else{
            return 0
        }
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<MyPageClaim.Content> {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(viewType){
            1 -> {
                var res = R.layout.item_mypage_claim_list
                val binding: ItemMypageClaimListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
                var viewModel = MyPageClaimListItemViewModel((model as MyPageClaimViewModel).context)
                binding.item = viewModel
                return MyPageClaimListViewHolder(binding.root, binding)
            }
            else -> {
                var res = R.layout.item_more_list
                val binding: ItemMoreListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
                return MyPageMoreListViewHolder(binding.root, binding)
            }
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder<MyPageClaim.Content>, item: MyPageClaim.Content, position: Int) {
        viewHolder.bind(model, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 마이페이지 상품문의 의 리스트에 사용할 viewholder
     */
    inner class MyPageClaimListViewHolder(val containerView: View, val binding: ItemMypageClaimListBinding) : ListViewHolder<MyPageClaim.Content>(containerView, binding) {
        override fun bind(model: ViewModel, position: Int, data: MyPageClaim.Content) {
            // init
            binding.linearlayoutMypageclaimlistAnswer1.visibility = View.VISIBLE
            binding.linearlayoutMypageclaimlistAnswer2.visibility = View.GONE
            binding.linearlayoutMypageclaimlistAnswer2.setOnClickListener(null)
            binding.textviewMypageclaimlistAsk1.maxLines = 1
            binding.imageviewMypageclaimlistArrow1.setImageResource(R.drawable.detail_icon_arrow_open)
            binding.buttonMypageclaimlistModify.visibility = View.GONE
            binding.buttonMypageclaimlistDelete.visibility = View.VISIBLE

            /**
             * 상품 문의 수정
             * @author Hyeyeon Park
             * @since 2019.07.30
             */
            binding.buttonMypageclaimlistModify.setOnClickListener {
                val context = (model as MyPageClaimViewModel).context as AppCompatActivity
                val intent = Intent(context, WriteClaimActivity::class.java)
                val claimContent = model.listData1.value?.get(adapterPosition)

                val inquiry = Inquiry().apply {
                    content = claimContent?.inquiry?.inquiry ?: ""
                    privateInquiry = claimContent?.inquiry?.private ?: false
                    inquiryId = claimContent?.inquiry?.id?.toInt()
                }
                model.selectedIndex = adapterPosition
                intent.putExtra("inquiry", inquiry)
                intent.putExtra("productId", claimContent?.inquiry?.productId)
                context.startActivityForResult(intent, Flag.RequestCode.MODIFY_CLAIM)
            }
            when {
                position == 0 -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.INVISIBLE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                }
                position == this@MyPageClaimAdapter.itemCount - 1 -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.INVISIBLE
                }
                else -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                }
            }

            // Product
            if(!TextUtils.isEmpty(data.item.imageUrl)){
                ImageUtil.loadImage((this@MyPageClaimAdapter.model as MyPageClaimViewModel).mRequestManager, binding.imageviewMypageclaimlistProduct, data.item.imageUrl)
            }
            binding.imageviewMypageclaimlistProduct.contentDescription = data.item.dealId.toString()
            binding.imageviewMypageclaimlistProduct.setOnClickListener {
                var id = it.contentDescription.toString().toLong()
                CommonUtil.startProductActivity((model as MyPageClaimViewModel).context as Activity, id)
            }
            binding.textviewMypageclaimlistBrand.text = data.item.brandName
            binding.textviewMypageclaimlistTitle.text = data.item.productName
            binding.textviewMypageclaimlistSeller.text = if(TextUtils.isEmpty(data.item.sellerName)) "" else data.item.sellerName
            if(TextUtils.isEmpty(data.item.productSeason)){
                binding.textviewMypageclaimlistSeason.visibility = View.GONE
            }else{
                binding.textviewMypageclaimlistSeason.visibility = View.VISIBLE
                binding.textviewMypageclaimlistSeason.text = data.item.productSeason
            }
            var createdAtCal = Calendar.getInstance().apply { timeInMillis = data.inquiry.createdAt }
            binding.textviewMypageclaimlistDate.text = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_5, createdAtCal ) + " 작성")

            // Inquiry
            binding.textviewMypageclaimlistAsk1.text = data.inquiry.inquiry
            binding.linearlayoutMypageclaimlistAnswer1.tag = false
            if ("COMPLETED".equals(data.inquiry.status)) {
                binding.buttonMypageclaimlistDelete.visibility = View.GONE
                binding.buttonMypageclaimlistDelete.setOnClickListener(null)
                binding.textviewMypageclaimlistStatus.setBackgroundResource(R.color.common_blue_purple)
                binding.textviewMypageclaimlistStatus.setText(R.string.productdetail_qna_reply_completed)
                binding.textviewMypageclaimlistStatus.setTextColor(containerView.context.resources.getColor(R.color.common_white))

                binding.textviewMypageclaimlistAsk1.text = data.inquiry.inquiry
                binding.textviewMypageclaimlistAsk2.text = data.inquiry.inquiry
                binding.textviewMypageclaimlistAsk3.text = data.inquiry.reply
                if(data.inquiry.replyAt != null && data.inquiry.replyAt is Long){
                    var replyAtCal = Calendar.getInstance().apply { timeInMillis = data.inquiry.replyAt!! }
                    binding.textviewMypageclaimlistDate1.text = ("판매자 " +  DateUtil.getCalendarToString(Type.DateFormat.TYPE_5, replyAtCal ))
                }/*else{
                    try{
                        var replyAt = data.inquiry.replyAt as Array<Int>
                        binding.textviewMypageclaimlistDate1.text = ("판매자 " + (replyAt[0] - 2000) +
                                "." + String.format("%02d", replyAt[1]) +
                                "." + String.format("%02d", replyAt[2]) +
                                " " + String.format("%02d", replyAt[3]) +
                                ":" + String.format("%02d", replyAt[4]))
                    }catch (e : Exception){
                        if(CustomLog.flag)CustomLog.E(e)
                    }
                }*/
                binding.linearlayoutMypageclaimlistAnswer1.contentDescription = position.toString()
                binding.linearlayoutMypageclaimlistAnswer1.setOnClickListener(layoutClickListener)
                binding.linearlayoutMypageclaimlistAnswer2.setOnClickListener(layoutClickListener)
            }else{
                binding.buttonMypageclaimlistModify.visibility = View.VISIBLE
                binding.textviewMypageclaimlistStatus.setBackgroundResource(R.drawable.drawable_border_dsix)
                binding.textviewMypageclaimlistStatus.setText(R.string.productdetail_qna_reply_pending)
                binding.textviewMypageclaimlistStatus.setTextColor(containerView.context.resources.getColor(R.color.greyish_brown_two))
                binding.linearlayoutMypageclaimlistAnswer1.setOnClickListener {
                    var flag = it.tag as Boolean
                    if(!flag){
                        binding.textviewMypageclaimlistAsk1.maxLines = 100
                        binding.imageviewMypageclaimlistArrow1.setImageResource(R.drawable.detail_icon_arrow_close)
                    }else{
                        binding.textviewMypageclaimlistAsk1.maxLines = 1
                        binding.imageviewMypageclaimlistArrow1.setImageResource(R.drawable.detail_icon_arrow_open)
                    }
                    binding.linearlayoutMypageclaimlistAnswer1.tag = !flag
                }
                binding.buttonMypageclaimlistDelete.setOnClickListener {

                    CommonViewUtil.showDialog((itemView.context as AppCompatActivity),"삭제하시겠습니까?",true, object : OnBaseDialogListener {
                        override fun onClickOk() {
                            var loading = LoadingIndicatorUtil((model as MyPageClaimViewModel).context as AppCompatActivity)
                            (model as MyPageClaimViewModel).deleteClaimItem(position,data.inquiry.productId, data.inquiry.id, loading)
                        }
                    })
                }

            }
        }

        /**
         * 답변 완료 레이아웃 확장/축소
         */
        var layoutClickListener = View.OnClickListener {
            var index = binding.linearlayoutMypageclaimlistAnswer1.contentDescription.toString().toInt()
            var flag = binding.linearlayoutMypageclaimlistAnswer1.tag as Boolean
            if(!flag){
                binding.linearlayoutMypageclaimlistAnswer1.visibility = View.GONE
                binding.linearlayoutMypageclaimlistAnswer2.visibility = View.VISIBLE
                when{
                    index == 0 -> {
                        binding.imageviewMypageclaimlistTopbar1.visibility = View.INVISIBLE
                        binding.imageviewMypageclaimlistTopbar2.visibility = View.INVISIBLE
                    }
                    index == this@MyPageClaimAdapter.itemCount-1 -> {
                        binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                        binding.imageviewMypageclaimlistTopbar2.visibility = View.GONE
                    }
                    else -> {
                        binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                        binding.imageviewMypageclaimlistTopbar2.visibility = View.INVISIBLE
                    }
                }
            }else{
                binding.linearlayoutMypageclaimlistAnswer1.visibility = View.VISIBLE
                binding.linearlayoutMypageclaimlistAnswer2.visibility = View.GONE

                when{
                    index == 0 -> {
                        binding.imageviewMypageclaimlistTopbar1.visibility = View.INVISIBLE
                        binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                    }
                    index == this@MyPageClaimAdapter.itemCount-1 -> {
                        binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                        binding.imageviewMypageclaimlistTopbar2.visibility = View.GONE
                    }
                    else -> {
                        binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                        binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                    }
                }
            }
            binding.linearlayoutMypageclaimlistAnswer1.tag = !flag
        }

    }


    inner class MyPageMoreListViewHolder(val containerView: View, val binding: ItemMoreListBinding) : ListViewHolder<MyPageClaim.Content>(containerView,binding){
        override fun bind(model : ViewModel, position : Int, data: MyPageClaim.Content){
            binding.linearlayoutMoreView.setOnClickListener {
                (model as MyPageClaimViewModel).getMoreCalimList(data.pageNumber+1)
            }
        }
    }


}