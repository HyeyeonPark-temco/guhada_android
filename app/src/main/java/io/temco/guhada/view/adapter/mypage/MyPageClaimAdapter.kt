package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.ProductBridge
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.data.model.Inquiry
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimListItemViewModel
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimViewModel
import io.temco.guhada.databinding.ItemMypageClaimListBinding
import io.temco.guhada.view.activity.WriteClaimActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter

class MyPageClaimAdapter(private val model: ViewModel, list: ArrayList<MyPageClaim.Content>) : CommonRecyclerAdapter<MyPageClaim.Content, CommonRecyclerAdapter.ListViewHolder>(list) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        var res = R.layout.item_mypage_claim_list
        val binding: ItemMypageClaimListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
        var viewModel = MyPageClaimListItemViewModel((model as MyPageClaimViewModel).context)
        binding.item = viewModel
        return MyPageClaimListViewHolder(binding.root, binding)
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: MyPageClaim.Content, position: Int) {
        viewHolder.bind(model, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 마이페이지 상품문의 의 리스트에 사용할 viewholder
     */
    inner class MyPageClaimListViewHolder(val containerView: View, val binding: ItemMypageClaimListBinding) : ListViewHolder(containerView, binding) {
        override fun bind(model: ViewModel, index: Int, data: MyPageClaim.Content) {
            // init
            binding.linearlayoutMypageclaimlistAnswer1.visibility = View.VISIBLE
            binding.linearlayoutMypageclaimlistAnswer2.visibility = View.GONE
            binding.linearlayoutMypageclaimlistAnswer2.setOnClickListener(null)
            binding.textviewMypageclaimlistAsk1.maxLines = 1
            binding.imageviewMypageclaimlistArrow1.setImageResource(R.drawable.detail_icon_arrow_open)
            binding.buttonMypageclaimlistModify.visibility = View.GONE

            /**
             * 상품 문의 수정
             * @author Hyeyeon Park
             * @since 2019.07.30
             */
            binding.buttonMypageclaimlistModify.setOnClickListener {
                val context = (model as MyPageClaimViewModel).context as AppCompatActivity
                val intent = Intent(context, WriteClaimActivity::class.java)
                val claimContent = model.listData.value?.get(adapterPosition)

                val inquiry = Inquiry().apply {
                    content = claimContent?.inquiry?.inquiry ?: ""
                    privateInquiry = claimContent?.inquiry?.private ?: false
                    inquiryId = claimContent?.inquiry?.id?.toInt()
                }

                intent.putExtra("inquiry", inquiry)
                intent.putExtra("productId", claimContent?.inquiry?.productId)
                context.startActivityForResult(intent, Flag.RequestCode.MODIFY_CLAIM)
            }
            when {
                index == 0 -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.INVISIBLE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                }
                index == this@MyPageClaimAdapter.itemCount - 1 -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.INVISIBLE
                }
                else -> {
                    binding.imageviewMypageclaimlistTopbar1.visibility = View.GONE
                    binding.imageviewMypageclaimlistTopbar2.visibility = View.VISIBLE
                }
            }

            // Product
            ImageUtil.loadImage(Glide.with(containerView.context as Activity), binding.imageviewMypageclaimlistProduct, data.item.imageUrl)
            binding.imageviewMypageclaimlistProduct.contentDescription = data.item.dealId.toString()
            binding.imageviewMypageclaimlistProduct.setOnClickListener {
                var id = it.contentDescription.toString().toLong()
                ProductBridge.mainActivity.addProductDetailView(id)
            }
            binding.textviewMypageclaimlistBrand.text = data.item.brandName
            binding.textviewMypageclaimlistTitle.text = data.item.productName
            binding.textviewMypageclaimlistSeller.text = data.item.sellerName
            binding.textviewMypageclaimlistSeason.text = data.item.productSeason
            binding.textviewMypageclaimlistDate.text = (data.inquiry.createdAt[0] - 2000).toString() +
                    "." + String.format("%02d", data.inquiry.createdAt[1]) +
                    "." + String.format("%02d", data.inquiry.createdAt[2]) +
                    " " + String.format("%02d", data.inquiry.createdAt[3]) +
                    ":" + String.format("%02d", data.inquiry.createdAt[4]) + " 작성"

            // Inquiry
            binding.textviewMypageclaimlistAsk1.text = data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry
            binding.linearlayoutMypageclaimlistAnswer1.tag = false
            if ("COMPLETED".equals(data.inquiry.status)) {
                binding.textviewMypageclaimlistStatus.setBackgroundResource(R.color.common_blue_purple)
                binding.textviewMypageclaimlistStatus.setText(R.string.productdetail_qna_reply_completed)
                binding.textviewMypageclaimlistStatus.setTextColor(containerView.context.resources.getColor(R.color.common_white))

                binding.textviewMypageclaimlistAsk1.text = data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry
                binding.textviewMypageclaimlistAsk2.text = data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry + data.inquiry.inquiry
                binding.textviewMypageclaimlistAsk3.text = data.inquiry.reply
                binding.textviewMypageclaimlistDate1.text = "판매자 " + (data.inquiry.replyAt[0] - 2000) +
                        "." + String.format("%02d", data.inquiry.replyAt[1]) +
                        "." + String.format("%02d", data.inquiry.replyAt[2]) +
                        " " + String.format("%02d", data.inquiry.replyAt[3]) +
                        ":" + String.format("%02d", data.inquiry.replyAt[4])
                binding.linearlayoutMypageclaimlistAnswer1.setOnClickListener{
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
                binding.linearlayoutMypageclaimlistAnswer2.setOnClickListener{
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
            }
        }

    }




}