package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.claim.MyPageClaimSellerContent
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimListItemViewModel
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimViewModel
import io.temco.guhada.databinding.ItemMoreListBinding
import io.temco.guhada.databinding.ItemMypageClaimsellerListBinding
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import java.lang.Exception
import java.util.*

class MyPageSellerClaimAdapter(private val model: ViewModel, list: ArrayList<MyPageClaimSellerContent>) :
        CommonRecyclerAdapter<MyPageClaimSellerContent, CommonRecyclerAdapter.ListViewHolder<MyPageClaimSellerContent>>(list) {

    override fun getItemViewType(position: Int): Int {
        if(items[position].id != 0){
            return 1
        }else{
            return 0
        }
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<MyPageClaimSellerContent> {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(viewType){
            1 -> {
                var res = R.layout.item_mypage_claimseller_list
                val binding: ItemMypageClaimsellerListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
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

    override fun setOnBindViewHolder(viewHolder: ListViewHolder<MyPageClaimSellerContent>, item: MyPageClaimSellerContent, position: Int) {
        viewHolder.bind(model, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 마이페이지 상품문의 의 리스트에 사용할 viewholder
     */
    inner class MyPageClaimListViewHolder(val containerView: View, val binding: ItemMypageClaimsellerListBinding) : ListViewHolder<MyPageClaimSellerContent>(containerView, binding) {
        override fun bind(model: ViewModel, position: Int, data: MyPageClaimSellerContent) {
            // init
            binding.linearlayoutMypageclaimlistAnswer1.visibility = View.VISIBLE
            binding.linearlayoutMypageclaimlistAnswer2.visibility = View.GONE
            binding.linearlayoutMypageclaimlistAnswer2.setOnClickListener(null)
            binding.textviewMypageclaimlistAsk1.maxLines = 1
            binding.imageviewMypageclaimlistArrow1.setImageResource(R.drawable.detail_icon_arrow_open)

            when {
                position == 0 -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.INVISIBLE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                }
                position == this@MyPageSellerClaimAdapter.itemCount - 1 -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.INVISIBLE
                }
                else -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                }
            }

            ImageUtil.loadImage((this@MyPageSellerClaimAdapter.model as MyPageClaimViewModel).mRequestManager, binding.imageviewMypageclaimlistProduct, data.productImageUrl)
            binding.textviewMypageclaimlistBrand.text = data.brandName
            binding.textviewMypageclaimlistTitle.text = data.productName
            binding.textviewMypageclaimlistSeller.text = data.nickname
            if(TextUtils.isEmpty(data.season)){
                binding.textviewMypageclaimlistSeason.visibility = View.GONE
            }else{
                binding.textviewMypageclaimlistSeason.visibility = View.VISIBLE
                binding.textviewMypageclaimlistSeason.text = data.season
            }
            try{
                if(data.createdAt != null){
                    var createdAtCal = Calendar.getInstance().apply { timeInMillis = data.createdAt!! }
                    binding.textviewMypageclaimlistDate.text = (DateUtil.getCalendarToString(Type.DateFormat.TYPE_5, createdAtCal ) + " 작성")
                }
            }catch (e : Exception){}

            // Inquiry
            binding.textviewMypageclaimlistAsk1.text = data.contents
            // Inquiry
            binding.linearlayoutMypageclaimlistAnswer1.tag = false
            binding.textviewMypageclaimlistAsk1.text = data.contents
            binding.textviewMypageclaimlistAsk2.text = data.contents
            binding.textviewMypageclaimlistAsk10.text = data.type
            binding.textviewMypageclaimlistAsk20.text = data.type

            if(data.repliedAt != null && data.reply != null){
                binding.buttonMypageclaimlistDelete.visibility = View.GONE
                binding.buttonMypageclaimlistDelete.setOnClickListener(null)

                binding.textviewMypageclaimlistStatus.setTextColor(containerView.context.resources.getColor(R.color.common_white))
                binding.textviewMypageclaimlistStatus.setBackgroundResource(R.color.common_blue_purple)
                binding.textviewMypageclaimlistStatus.setText(R.string.productdetail_qna_reply_completed)

                binding.textviewMypageclaimlistAsk3.text = data.reply as String
                if(data.repliedAt != null && data.repliedAt is Long){
                    var replyAtCal = Calendar.getInstance().apply { timeInMillis = data.repliedAt!! as Long }
                    binding.textviewMypageclaimlistDate1.text = ("판매자 " +  DateUtil.getCalendarToString(Type.DateFormat.TYPE_5, replyAtCal ))
                }
                binding.linearlayoutMypageclaimlistAnswer1.contentDescription = position.toString()
                binding.linearlayoutMypageclaimlistAnswer1.setOnClickListener(layoutClickListener)
                binding.linearlayoutMypageclaimlistAnswer2.setOnClickListener(layoutClickListener)
            }else{
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
                binding.buttonMypageclaimlistDelete.visibility = View.VISIBLE
                binding.buttonMypageclaimlistDelete.setOnClickListener {
                    CommonViewUtil.showDialog((itemView.context as AppCompatActivity),"삭제하시겠습니까?",true, object :OnBaseDialogListener{
                        override fun onClickOk() {
                            var loading = LoadingIndicatorUtil((model as MyPageClaimViewModel).context as AppCompatActivity)
                            (model as MyPageClaimViewModel).deleteSellerClaimItem(position,data.id, loading)
                        }
                    })
                }
            }
            /*if ("COMPLETED".equals(data.inquiry.status)) {
                binding.buttonMypageclaimlistDelete.visibility = View.GONE
                binding.buttonMypageclaimlistDelete.setOnClickListener(null)
                binding.textviewMypageclaimlistStatus.setBackgroundResource(R.color.common_blue_purple)
                binding.textviewMypageclaimlistStatus.setText(R.string.productdetail_qna_reply_completed)
                binding.textviewMypageclaimlistStatus.setTextColor(containerView.context.resources.getColor(R.color.common_white))

                binding.textviewMypageclaimlistAsk1.text = data.inquiry.inquiry
                binding.textviewMypageclaimlistAsk2.text = data.inquiry.inquiry
                binding.textviewMypageclaimlistAsk3.text = data.inquiry.reply
                if(data.repliedAt != null && data.repliedAt is Long){
                    var replyAtCal = Calendar.getInstance().apply { timeInMillis = data.repliedAt!! as Long }
                    binding.textviewMypageclaimlistDate1.text = ("판매자 " +  DateUtil.getCalendarToString(Type.DateFormat.TYPE_5, replyAtCal ))
                }else{
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
                }
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
                    var loading = LoadingIndicatorUtil((model as MyPageClaimViewModel).context as AppCompatActivity)
                    (model as MyPageClaimViewModel).deleteClaimItem(position,data.inquiry.productId, data.inquiry.id, loading)
                }

            }*/


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
                    index == this@MyPageSellerClaimAdapter.itemCount-1 -> {
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
                    index == this@MyPageSellerClaimAdapter.itemCount-1 -> {
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


    inner class MyPageMoreListViewHolder(val containerView: View, val binding: ItemMoreListBinding) : ListViewHolder<MyPageClaimSellerContent>(containerView,binding){
        override fun bind(model : ViewModel, position : Int, data: MyPageClaimSellerContent){
            binding.linearlayoutMoreView.setOnClickListener {
                (model as MyPageClaimViewModel).getMoreSellerCalimList(data.pageNumber+1)
            }
        }
    }


}