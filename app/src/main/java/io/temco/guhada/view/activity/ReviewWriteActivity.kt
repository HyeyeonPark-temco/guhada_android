package io.temco.guhada.view.activity

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.review.MyPageReviewBase
import io.temco.guhada.data.model.review.MyPageReviewContent
import io.temco.guhada.view.activity.base.BindActivity
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.ImageUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.viewmodel.ReviewWriteViewModel


/**
 * 리뷰 작성 & 수정 Activity
 * @author park jungho
 */
class ReviewWriteActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewwriteBinding>() {
    private lateinit var mRequestManager: RequestManager
    private lateinit var mViewModel : ReviewWriteViewModel

    private var reviewData : MyPageReviewBase? = null

    override fun getBaseTag(): String = ReviewWriteActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewwrite
    override fun getViewType(): Type.View = Type.View.REVIEW_WRITE

    override fun init() {
        mRequestManager = Glide.with(this)
        mViewModel = ReviewWriteViewModel(this)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }

        if(intent.extras != null && intent.extras.containsKey("reviewData")){
            if(intent.extras.getSerializable("reviewData") is MyPageReviewContent){
                reviewData = intent.extras.getSerializable("reviewData") as MyPageReviewBase
                if(CustomLog.flag)CustomLog.L("ReviewWriteActivity",reviewData!!.toString())
                mViewModel.modifyReviewStatus.set(true)
                setReviewModify()
            }else{
                mViewModel.modifyReviewStatus.set(false)
            }

        }

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels - CommonViewUtil.convertDpToPixel(40,this)
        val height = dm.heightPixels - CommonViewUtil.convertDpToPixel(60,this)
        mBinding.linearlayoutReivewwriteParent.layoutParams = FrameLayout.LayoutParams(width,height)
    }


    private fun setReviewModify(){
        var item = reviewData as MyPageReviewContent

        ImageUtil.loadImage(mRequestManager, mBinding.productItemLayout.imageItemmypagereviewlistreviewThumb, item.order.imageUrl)
        mBinding.productItemLayout.season = item.order.season
        mBinding.productItemLayout.brand = item.order.brandName
        mBinding.productItemLayout.title = item.order.prodName
        var option = (if(!item.order.optionAttribute1.isNullOrEmpty())item.order.optionAttribute1 else "")  +
                (if(!item.order.optionAttribute2.isNullOrEmpty())", "+item.order.optionAttribute2 else "")  +
                (if(!item.order.optionAttribute3.isNullOrEmpty())", "+item.order.optionAttribute3 else "")

        mBinding.productItemLayout.option = (if(!option.isNullOrEmpty()) option+", " else "") + item.order.quantity + "개"
        mBinding.productItemLayout.price = item.order.orderPrice

        if(item.order.shipCompleteDate.isNullOrEmpty()){
            mBinding.productItemLayout.deliveryComplete = item.order.purchaseStatusText
        }else{
            mBinding.productItemLayout.deliveryComplete = item.order.purchaseStatusText
        }

        mBinding.ratingbarReviewwriteactivityStar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            //ToastUtil.showMessage("ratingBar" + rating.toString())
        }
        mBinding.ratingbarReviewwriteactivityStar.rating = item.review.getRating()


        mBinding.edittextReviewwriteText.text = Editable.Factory.getInstance().newEditable(item.review.textReview)
        mBinding.textviewReviewwriteTextcount.text = item.review.textReview.length.toString()
        mBinding.edittextReviewwriteText.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mBinding.textviewReviewwriteTextcount.text = s!!.length.toString()
            }
        })

    }


}