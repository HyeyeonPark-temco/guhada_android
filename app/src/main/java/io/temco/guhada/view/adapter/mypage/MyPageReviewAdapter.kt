package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.review.MyPageReviewBase
import io.temco.guhada.data.model.review.MyPageReviewContent
import io.temco.guhada.data.model.review.MyPageReviewType
import io.temco.guhada.data.model.review.ReviewAvailableOrder
import io.temco.guhada.data.viewmodel.mypage.MyPageReviewViewModel
import io.temco.guhada.databinding.ItemMoreListBinding
import io.temco.guhada.databinding.ItemMypageReviewListAvaiableBinding
import io.temco.guhada.databinding.ItemMypageReviewListReviewBinding
import io.temco.guhada.view.activity.ReviewWriteActivity
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author park jungho
 * 19.07.26
 *
 * 마이페이지 최근본상품,
 */
class MyPageReviewAdapter (private val model : ViewModel, list : ArrayList<MyPageReviewBase>) :
        CommonRecyclerAdapter<MyPageReviewBase, MyPageReviewAdapter.ListViewHolder>(list) {

    override fun getItemViewType(position: Int): Int {
        return items.let {
            if(items[position].isMoreList) 0
            else {
                if(items[position].type == MyPageReviewType.AvailableReview)  1
                else 2
            }
        }
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if(viewType == 1){
            var res = R.layout.item_mypage_review_list_avaiable
            val binding : ItemMypageReviewListAvaiableBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
            return MyPageMyPageReviewAvailableListViewHolder(binding.root, binding)
        }else if(viewType == 2){
            var res = R.layout.item_mypage_review_list_review
            val binding : ItemMypageReviewListReviewBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
            return MyPageMyPageReviewListViewHolder(binding.root, binding)
        }else{
            var res = R.layout.item_more_list
            val binding : ItemMoreListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
            return MyPageMoreListViewHolder(binding.root, binding)
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: MyPageReviewBase, position: Int) {
        viewHolder.bind(model, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : ViewModel, position : Int, data : MyPageReviewBase)
    }

    /**
     * 메인 리스트에 사용할 base view holder
     */
    inner class MyPageMyPageReviewAvailableListViewHolder(val containerView: View, val binding: ItemMypageReviewListAvaiableBinding) : ListViewHolder(containerView,binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(model : ViewModel, position : Int, data : MyPageReviewBase) {
            if (data != null) {
                var item = data as ReviewAvailableOrder
                ImageUtil.loadImage((this@MyPageReviewAdapter.model as MyPageReviewViewModel).mRequestManager, binding.productItemLayout.imageItemmypagereviewlistreviewThumb, item.imageUrl)

                binding.position = position
                binding.productItemLayout.season = item.season
                binding.productItemLayout.brand = item.brandName
                binding.productItemLayout.title = item.prodName
                binding.productItemLayout.setClickProductListener { CommonUtil.startProductActivity(itemView.context as Activity, item.dealId) }
                var option = (if(!item.optionAttribute1.isNullOrEmpty())item.optionAttribute1 else "")  +
                        (if(!item.optionAttribute2.isNullOrEmpty())", "+item.optionAttribute2 else "")  +
                        (if(!item.optionAttribute3.isNullOrEmpty())", "+item.optionAttribute3 else "")

                binding.productItemLayout.option = (if(!option.isNullOrEmpty()) option+", " else "") + item.quantity + "개"
                binding.productItemLayout.price = item.orderPrice

                if(item.shipCompleteTimestamp.isNullOrEmpty()){
                    binding.productItemLayout.deliveryComplete = item.purchaseStatusText
                }else{
                    var cal = Calendar.getInstance()
                    binding.productItemLayout.deliveryComplete = item.purchaseStatusText
                }
                binding.setClickWriteListener {
                    var intent = Intent(containerView.context as Activity, ReviewWriteActivity::class.java)
                    intent.putExtra("reviewData", item)
                    (containerView.context as Activity).startActivityForResult(intent, Flag.RequestCode.REVIEW_WRITE)
                }
            }
        }
    }

    inner class MyPageMyPageReviewListViewHolder(val containerView: View, val binding: ItemMypageReviewListReviewBinding) : ListViewHolder(containerView,binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) { }
        override fun bind(model : ViewModel, position : Int, data : MyPageReviewBase) {
            if (data != null) {
                var item = data as MyPageReviewContent

                ImageUtil.loadImage((this@MyPageReviewAdapter.model as MyPageReviewViewModel).mRequestManager, binding.productItemLayout.imageItemmypagereviewlistreviewThumb, item.order.imageUrl)

                binding.position = position
                binding.productItemLayout.season = item.order.season
                binding.productItemLayout.brand = item.order.brandName
                binding.productItemLayout.title = item.order.prodName
                var option = (if(!item.order.optionAttribute1.isNullOrEmpty())item.order.optionAttribute1 else "")  +
                        (if(!item.order.optionAttribute2.isNullOrEmpty())", "+item.order.optionAttribute2 else "")  +
                        (if(!item.order.optionAttribute3.isNullOrEmpty())", "+item.order.optionAttribute3 else "")

                binding.productItemLayout.setClickProductListener { CommonUtil.startProductActivity(itemView.context as Activity, item.order.dealId) }

                binding.productItemLayout.option = (if(!option.isNullOrEmpty()) option+", " else "") + item.order.quantity + "개"
                binding.productItemLayout.price = item.order.orderPrice

                if(item.order.shipCompleteTimestamp.isNullOrEmpty()){
                    binding.productItemLayout.deliveryComplete = item.order.purchaseStatusText
                }else{
                    var cal = Calendar.getInstance()
                    binding.productItemLayout.deliveryComplete = item.order.purchaseStatusText
                }

                var option2 = (if(!item.order.optionAttribute1.isNullOrEmpty())item.order.optionAttribute1 else "") +
                        (if(!item.order.optionAttribute2.isNullOrEmpty()) "/" +item.order.optionAttribute2 else "") +
                        (if(!item.order.optionAttribute3.isNullOrEmpty()) "/" +item.order.optionAttribute3 else "")

                binding.option2 = containerView.context.resources.getString(R.string.cart_option_title) +" : "+ (if(option2.isNullOrEmpty()) "없음" else option2)
                binding.reviewTextTitle1 = "사이즈"
                binding.reviewTextDesc1 = item.reviewTexts.size/*"작아요"*/

                binding.reviewTextTitle2 = "컬러"
                binding.reviewTextDesc2 = item.reviewTexts.color/*"화면과 같아요"*/

                binding.reviewTextTitle3 = "길이감"
                binding.reviewTextDesc3 = item.reviewTexts.length/*"길어요"*/

                binding.reviewImageCount = item.review.photoCount
                binding.reviewImageCountTxt = item.review.photoCount.toString()
                if(item.review.photoCount > 0)
                    ImageUtil.loadImage((this@MyPageReviewAdapter.model as MyPageReviewViewModel).mRequestManager, binding.imageItemmypagereviewlistreviewReview, item.reviewPhotos[0].reviewPhotoUrl)
                binding.reviewDesc = item.review.textReview
                binding.reviewDate = item.review.createdAt
                binding.rating = item.review.getRating()

                binding.setClickDelListener {
                    CustomMessageDialog(message = BaseApplication.getInstance().getString(R.string.review_activity_tab2_review_del_desc),
                            cancelButtonVisible = true,
                            confirmTask = {
                                if(model is MyPageReviewViewModel){
                                    model.mLoadingIndicatorUtil.show()
                                    var productId = item.order.productId
                                    var reviewId = item.review.id.toLong()
                                    model.deleteMyReview(productId, reviewId, object : OnCallBackListener{
                                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                                            if(resultFlag){
                                                items.removeAt(position)
                                                if(model.getReviewAdapter().itemCount > 0){
                                                    model.getReviewAdapter().notifyItemChanged(position)
                                                }else{
                                                    model.tab2EmptyViewVisible.set(true)
                                                }
                                                model.mypageReviewtab2Title.set(model.mypageReviewtab2Title.get()-1)
                                            }else{
                                                ToastUtil.showMessage(value.toString())
                                            }
                                            model.mLoadingIndicatorUtil.hide()
                                            model.mLoadingIndicatorUtil.dismiss()
                                        }
                                    })
                                }
                            }).show(manager = ((this@MyPageReviewAdapter.model as MyPageReviewViewModel).context as AppCompatActivity).supportFragmentManager, tag = "MyPageReviewAdapter")

                }

                binding.setClickModifyListener {
                    var intent = Intent(containerView.context as Activity, ReviewWriteActivity::class.java)
                    intent.putExtra("reviewData", item)
                    (containerView.context as Activity).startActivityForResult(intent, Flag.RequestCode.REVIEW_MODIFY)
                }
            }
        }
    }

    inner class MyPageMoreListViewHolder(val containerView: View, val binding: ItemMoreListBinding) : ListViewHolder(containerView,binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?, position : Int) {  }
        override fun bind(model : ViewModel, position : Int, data : MyPageReviewBase){
            binding.linearlayoutMoreView.setOnClickListener {
                if(data is MyPageReviewContent){
                    (model as MyPageReviewViewModel).getMoreTab2List()
                }else{
                    (model as MyPageReviewViewModel).getMoreTab1List()
                }
            }
        }
    }

}