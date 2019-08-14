package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.review.MyPageReviewBase
import io.temco.guhada.data.model.review.MyPageReviewContent
import io.temco.guhada.data.model.review.ReviewAvailableOrder
import io.temco.guhada.data.model.review.ReviewWrMdResponse
import io.temco.guhada.data.viewmodel.ReviewWriteViewModel
import io.temco.guhada.view.activity.base.BindActivity
import java.util.*


/**
 * 리뷰 작성 & 수정 Activity
 * @author park jungho
 */
class ReviewWriteActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewwriteBinding>() {
    private val MAX_TXT_LENGTH = 1000
    private lateinit var mRequestManager: RequestManager
    private lateinit var mViewModel : ReviewWriteViewModel
    private lateinit var loadingIndicatorUtil : LoadingIndicatorUtil

    private var reviewData : MyPageReviewBase? = null
    private var reviewAvailableData : ReviewAvailableOrder ? = null

    private var product : Product? = null

    private var ratingBarValue = 0.0f

    override fun getBaseTag(): String = ReviewWriteActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewwrite
    override fun getViewType(): Type.View = Type.View.REVIEW_WRITE

    override fun init() {
        loadingIndicatorUtil = LoadingIndicatorUtil(this)
        mRequestManager = Glide.with(this)
        mViewModel = ReviewWriteViewModel(this)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }
        mBinding.setOnClickGetImage {
            CommonUtil.startImageGallery(this)
        }
        mBinding.setOnClickReviewWriteOrModify {
            var data = ReviewWrMdResponse()
            var txt = mBinding.edittextReviewwriteText.text.toString()
            if(txt.isNullOrEmpty()){
                ToastUtil.showMessage("리뷰를 입력해 주세요.")
                return@setOnClickReviewWriteOrModify
            }
            loadingIndicatorUtil.show()
            var reviewId = 0
            var productId = 0L
            if(mViewModel.modifyReviewStatus.get()){
                var item = reviewData as MyPageReviewContent
                data.colorSatisfaction = getColorSatisfaction(mViewModel.reviewSelectStatus2.get())
                data.lengthSatisfaction = getLengthSatisfaction(mViewModel.reviewSelectStatus1.get())
                data.sizeSatisfaction = getSizeSatisfaction(mViewModel.reviewSelectStatus3.get())
                data.productRating = getRating(ratingBarValue)
                data.orderProductGroupId = item.order.orderProdGroupId
                data.sellerId = item.order.sellerId
                data.productId = item.order.productId
                data.reviewId = item.review.id.toLong()
                data.textReview = mBinding.edittextReviewwriteText.text.toString()
                reviewId = item.review.id
                productId = item.order.productId
            }else{
                var item = reviewAvailableData
                data.colorSatisfaction = getColorSatisfaction(mViewModel.reviewSelectStatus2.get())
                data.lengthSatisfaction = getLengthSatisfaction(mViewModel.reviewSelectStatus1.get())
                data.sizeSatisfaction = getSizeSatisfaction(mViewModel.reviewSelectStatus3.get())
                data.productRating = getRating(ratingBarValue)
                data.orderProductGroupId = item!!.orderProdGroupId
                data.sellerId = item!!.sellerId
                data.productId = item!!.productId
                data.textReview = mBinding.edittextReviewwriteText.text.toString()
                reviewId = 0
                productId = item!!.productId
            }
            mViewModel.clickReviewWriteOrModify(data, productId, reviewId , object  : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    loadingIndicatorUtil.hide()
                    if(CustomLog.flag)CustomLog.L("clickReviewWriteOrModify","resultFlag",resultFlag,"value",value)
                    if(resultFlag){
                        if(mViewModel.modifyReviewStatus.get()){
                            ToastUtil.showMessage("리뷰 수정이 완료되었습니다.")
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{

                        }
                    }
                }
            })
        }

        if(intent.extras != null && intent.extras.containsKey("reviewData")){
            if(intent.extras.getSerializable("reviewData") is MyPageReviewContent){
                reviewData = intent.extras.getSerializable("reviewData") as MyPageReviewBase
                if(CustomLog.flag)CustomLog.L("ReviewWriteActivity",reviewData!!.toString())
                mViewModel.modifyReviewStatus.set(true)
                setReviewModify()
            }else{
                reviewAvailableData = intent.extras.getSerializable("reviewData") as ReviewAvailableOrder
                if(CustomLog.flag)CustomLog.L("ReviewWriteActivity",reviewAvailableData!!.toString())
                mViewModel.modifyReviewStatus.set(false)
                setReviewWrite()
            }
        }

        mBinding.edittextReviewwriteText.addTextChangedListener(object  : TextWatcher{
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty() || s.isNullOrBlank()){
                    mViewModel.editTextReviewTxtCount.set("0")
                }else{
                    if(s != null && s.isNotEmpty()){
                        mViewModel.editTextReviewTxtCount.set(s!!.length.toString())
                    }
                }
            }
        })
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

        if(item.order.shipCompleteTimestamp.isNullOrEmpty()){
            mBinding.productItemLayout.deliveryComplete = ""
        }else{
            var cal = Calendar.getInstance()
            cal.timeInMillis = item.order.shipCompleteTimestamp.toLong()
            mBinding.productItemLayout.deliveryComplete = item.order.shipCompleteTimestamp
        }

        mBinding.ratingbarReviewwriteactivityStar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingBarValue = rating
        }
        mBinding.ratingbarReviewwriteactivityStar.rating = item.review.getRating()

        mViewModel.reviewSelectStatus1.set(item.review.getSizeSatisfaction())
        mViewModel.reviewSelectStatus2.set(item.review.getColorSatisfaction())
        mViewModel.reviewSelectStatus3.set(item.review.getLengthSatisfaction())

        var txt = ""
        if(!item.review.textReview.isNullOrEmpty()){
            txt = if(item.review.textReview.length < MAX_TXT_LENGTH) item.review.textReview else item.review.textReview.substring(0, MAX_TXT_LENGTH)
        }

        mBinding.edittextReviewwriteText.text = Editable.Factory.getInstance().newEditable(txt)
        mViewModel.editTextReviewTxtCount.set(txt.length.toString())
    }



    private fun setReviewWrite(){
        var item = reviewAvailableData

        ImageUtil.loadImage(mRequestManager, mBinding.productItemLayout.imageItemmypagereviewlistreviewThumb, item!!.imageUrl)
        mBinding.productItemLayout.season = item!!.season
        mBinding.productItemLayout.brand = item!!.brandName
        mBinding.productItemLayout.title = item!!.prodName
        var option = (if(!item!!.optionAttribute1.isNullOrEmpty())item!!.optionAttribute1 else "")  +
                (if(!item!!.optionAttribute2.isNullOrEmpty())", "+item!!.optionAttribute2 else "")  +
                (if(!item!!.optionAttribute3.isNullOrEmpty())", "+item!!.optionAttribute3 else "")

        mBinding.productItemLayout.option = (if(!option.isNullOrEmpty()) option+", " else "") + item!!.quantity + "개"
        mBinding.productItemLayout.price = item!!.orderPrice

        if(item!!.shipCompleteTimestamp.isNullOrEmpty()){
            mBinding.productItemLayout.deliveryComplete = ""
        }else{
            var cal = Calendar.getInstance()
            cal.timeInMillis = item!!.shipCompleteTimestamp.toLong()
            mBinding.productItemLayout.deliveryComplete = item!!.shipCompleteTimestamp
        }

        mBinding.ratingbarReviewwriteactivityStar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingBarValue = rating
        }
        mBinding.ratingbarReviewwriteactivityStar.rating = 5.0f

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Flag.RequestCode.IMAGE_GALLERY&& resultCode == Activity.RESULT_OK){
            var fileNm = data!!.extras.getString("file_name")
            setPic(mBinding.imageviewReviewwriteImg01, fileNm)
            if(CustomLog.flag)CustomLog.L("ReviewWriteActivity","fileNm",fileNm)
        }
    }

    private fun setPic(imageView : ImageView, imgFile : String) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(imgFile, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun getRating(value : Float): String = when (value) {
        0.5f -> "HALF"
        1.0f -> "ONE"
        1.5f -> "ONE_HALF"
        2.0f -> "TWO"
        2.5f -> "TWO_HALF"
        3.0f -> "THREE"
        3.5f -> "THREE_HALF"
        4.0f -> "FOUR"
        4.5f -> "FOUR_HALF"
        5.0f -> "FIVE"
        else -> "FIVE"
    }


    private fun getColorSatisfaction(value : Int): String = when (value) {
        0  -> "BRIGHTER"
        1  -> "SAME"
        2  -> "DARKER"
        else -> "SAME"
    }

    private fun getLengthSatisfaction (value : Int): String = when (value) {
        0  -> "SHORT"
        1  -> "REGULAR"
        2  -> "LONG"
        else -> "REGULAR"
    }

    private fun getSizeSatisfaction  (value : Int): String = when (value) {
        0  -> "SMALL"
        1  -> "JUST_FIT"
        2  -> "LARGE"
        else -> "JUST_FIT"
    }


}