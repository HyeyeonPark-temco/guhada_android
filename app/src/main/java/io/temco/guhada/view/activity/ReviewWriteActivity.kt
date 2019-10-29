package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PointPopupType
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.review.*
import io.temco.guhada.data.viewmodel.ReviewWriteViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ReviewWriteImageAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import java.util.*


/**
 * 리뷰 작성 & 수정 Activity
 * @author park jungho
 */
class ReviewWriteActivity : BindActivity<io.temco.guhada.databinding.ActivityReviewwriteBinding>(), OnClickSelectItemListener {
    private val MAX_TXT_LENGTH = 1000
    private lateinit var mRequestManager: RequestManager
    private lateinit var mViewModel: ReviewWriteViewModel
    private var loadingIndicatorUtil: LoadingIndicatorUtil? = null

    private var reviewData: MyPageReviewBase? = null
    private var reviewAvailableData: ReviewAvailableOrder? = null

    private var deleteImageData = arrayListOf<ReviewPhotos>()

    private var ratingBarValue = 0.0f

    override fun getBaseTag(): String = ReviewWriteActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_reviewwrite
    override fun getViewType(): Type.View = Type.View.REVIEW_WRITE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun init() {
        loadingIndicatorUtil = LoadingIndicatorUtil(this)
        mRequestManager = Glide.with(this)
        mViewModel = ReviewWriteViewModel(this)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }
        mBinding.setOnClickGetImage {
            if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", "setOnClickGetImage", mViewModel.mReviewEditPhotos.value!!.size)
            if (mViewModel.mReviewEditPhotos.value!!.size < 10) {
                mViewModel.selectedImageIndex = mViewModel.mReviewEditPhotos.value!!.size
                CommonUtil.startImageGallery(this)
            } else {
                ToastUtil.showMessage(resources.getString(R.string.review_activity_maximage_desc))
            }
        }
        mBinding.setOnClickReviewWriteOrModify { imageCheck() }

        if (intent.extras != null && intent.extras.containsKey("reviewData")) {
            if (intent.extras.getSerializable("reviewData") is MyPageReviewContent) {
                reviewData = intent.extras.getSerializable("reviewData") as MyPageReviewBase
                if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", reviewData!!.toString())
                mViewModel.modifyReviewStatus.set(true)
                setReviewModify()
            } else {
                reviewAvailableData = intent.extras.getSerializable("reviewData") as ReviewAvailableOrder
                if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", reviewAvailableData!!.toString())
                mViewModel.getUserSize()
                mViewModel.modifyReviewStatus.set(false)
                setReviewWrite()
            }
        }

        if (mBinding.recyclerviewReviewwriteImagelist.adapter == null) {
            mBinding.recyclerviewReviewwriteImagelist.adapter = ReviewWriteImageAdapter().apply { mList = mViewModel.mReviewEditPhotos.value!! }
            (mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).mClickSelectItemListener = this@ReviewWriteActivity
            mBinding.executePendingBindings()
        } else {
            (mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).setItems(mViewModel.mReviewEditPhotos.value!!)
        }

        mBinding.edittextReviewwriteText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || s.isNullOrBlank()) {
                    mViewModel.editTextReviewTxtCount.set("0")
                } else {
                    if (s != null && s.isNotEmpty()) {
                        mViewModel.editTextReviewTxtCount.set(s!!.length.toString())
                    }
                }
            }
        })

        // 화면 외각 Padding
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels - CommonViewUtil.convertDpToPixel(40, this)
        val height = dm.heightPixels - CommonViewUtil.convertDpToPixel(60, this)
        mBinding.linearlayoutReivewwriteParent.layoutParams = FrameLayout.LayoutParams(width, height)

        // 내 사이즈 등록/수정
        mBinding.setOnClickUserSize {
            var intent = Intent(this@ReviewWriteActivity, UserSizeUpdateActivity::class.java)
            if (mViewModel.userSize != null) intent.putExtra("userSize", mViewModel.userSize)
            this@ReviewWriteActivity.startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }

        mViewModel.mReviewEditPhotos.observe(this, androidx.lifecycle.Observer {
            if (it.isNullOrEmpty()) mBinding.recyclerviewReviewwriteImagelist.visibility = View.GONE
            else mBinding.recyclerviewReviewwriteImagelist.visibility = View.VISIBLE
        })
        deleteImageData.clear()
    }


    /**
     * 리뷰 수정 데이터 설정
     */
    private fun setReviewModify() {
        var item = reviewData as MyPageReviewContent
        if (!item.reviewPhotos.isNullOrEmpty()) {
            mViewModel.mReviewEditPhotos.value!!.addAll(item.reviewPhotos)
        }

        ImageUtil.loadImage(mRequestManager, mBinding.productItemLayout.imageItemmypagereviewlistreviewThumb, item.order.imageUrl)
        mBinding.productItemLayout.season = item.order.season
        mBinding.productItemLayout.brand = item.order.brandName
        mBinding.productItemLayout.title = item.order.prodName
        var option = (if (!item.order.optionAttribute1.isNullOrEmpty()) item.order.optionAttribute1 else "") +
                (if (!item.order.optionAttribute2.isNullOrEmpty()) ", " + item.order.optionAttribute2 else "") +
                (if (!item.order.optionAttribute3.isNullOrEmpty()) ", " + item.order.optionAttribute3 else "")

        mBinding.productItemLayout.option = (if (!option.isNullOrEmpty()) option + ", " else "") + item.order.quantity + "개"
        mBinding.productItemLayout.price = item.order.orderPrice

        if (item.order.shipCompleteTimestamp.isNullOrEmpty()) {
            mBinding.productItemLayout.deliveryComplete = ""
        } else {
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
        if (!item.review.textReview.isNullOrEmpty()) {
            txt = if (item.review.textReview.length < MAX_TXT_LENGTH) item.review.textReview else item.review.textReview.substring(0, MAX_TXT_LENGTH)
        }

        mBinding.edittextReviewwriteText.text = Editable.Factory.getInstance().newEditable(txt)
        mViewModel.editTextReviewTxtCount.set(txt.length.toString())
        setImageRecyclerViewVisible()

        if (item.userSize == null) {
            mViewModel.setUserSize(true, item.userSize)
        }
    }

    private fun setImageRecyclerViewVisible() {
        if (mViewModel.mReviewEditPhotos.value!!.size > 0) {
            mBinding.recyclerviewReviewwriteImagelist.visibility = View.VISIBLE
        } else {
            mBinding.recyclerviewReviewwriteImagelist.visibility = View.GONE
        }
    }


    /**
     * 리뷰 등록 데이터 설정
     */
    private fun setReviewWrite() {
        var item = reviewAvailableData

        ImageUtil.loadImage(mRequestManager, mBinding.productItemLayout.imageItemmypagereviewlistreviewThumb, item!!.imageUrl)
        mBinding.productItemLayout.season = item!!.season
        mBinding.productItemLayout.brand = item!!.brandName
        mBinding.productItemLayout.title = item!!.prodName
        var option = (if (!item!!.optionAttribute1.isNullOrEmpty()) item!!.optionAttribute1 else "") +
                (if (!item!!.optionAttribute2.isNullOrEmpty()) ", " + item!!.optionAttribute2 else "") +
                (if (!item!!.optionAttribute3.isNullOrEmpty()) ", " + item!!.optionAttribute3 else "")

        mBinding.productItemLayout.option = (if (!option.isNullOrEmpty()) option + ", " else "") + item!!.quantity + "개"
        mBinding.productItemLayout.price = item!!.orderPrice

        if (item!!.shipCompleteTimestamp.isNullOrEmpty()) {
            mBinding.productItemLayout.deliveryComplete = ""
        } else {
            var cal = Calendar.getInstance()
            cal.timeInMillis = item!!.shipCompleteTimestamp.toLong()
            mBinding.productItemLayout.deliveryComplete = item!!.shipCompleteTimestamp
        }

        mBinding.ratingbarReviewwriteactivityStar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingBarValue = rating
        }
        mBinding.ratingbarReviewwriteactivityStar.rating = 5.0f

    }


    /**
     * type - 0 : delete, 1 : click
     */
    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
        when (type) {
            0 -> {
                if ("http".equals((mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).mList.get(index).reviewPhotoUrl.substring(0, 4), true)) {
                    deleteImageData.add((mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).mList.get(index))
                }
                (mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).mList.removeAt(index)
                (mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).notifyDataSetChanged()
                setImageRecyclerViewVisible()
            }
            1 -> {
                if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", "clickSelectItemListener", "index", index, "value", value)
                mViewModel.selectedImageIndex = index
                CommonUtil.startImageGallery(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 갤러리에서 선택된 사진 파일 주소 받아옴
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Flag.RequestCode.IMAGE_GALLERY -> {
                    var fileNm = data!!.extras.getString("file_name")
                    if (!fileNm.substring(0, 4).equals("http", true)) {
                        var imageValue = ReviewPhotos()
                        if (mViewModel.mReviewEditPhotos.value!!.size > mViewModel.selectedImageIndex) {
                            mViewModel.mReviewEditPhotos.value!!.removeAt(mViewModel.selectedImageIndex)
                            imageValue.id = -99
                            imageValue.reviewPhotoUrl = fileNm
                            imageValue.imageStatus = ImageStatus.ADDED.name
                            imageValue.photoOrder = mViewModel.selectedImageIndex
                            mViewModel.mReviewEditPhotos.value!!.add(mViewModel.selectedImageIndex, imageValue)
                            (mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).notifyDataSetChanged()
                        } else {
                            imageValue.id = -99
                            imageValue.reviewPhotoUrl = fileNm
                            imageValue.imageStatus = ImageStatus.ADDED.name
                            imageValue.photoOrder = mViewModel.mReviewEditPhotos.value!!.size
                            mViewModel.mReviewEditPhotos.value!!.add(imageValue)
                            (mBinding.recyclerviewReviewwriteImagelist.adapter as ReviewWriteImageAdapter).notifyDataSetChanged()
                        }
                        setImageRecyclerViewVisible()
                        if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", "fileNm", fileNm)
                    } else {
                        showDialog("외부 이미지 링크가 아닌 파일만 등록 가능합니다.", false)
                    }
                }
                Flag.RequestCode.POINT_RESULT_DIALOG -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                Flag.RequestCode.USER_SIZE -> {
                    mViewModel.getUserSize()
                }
            }
        }
    }

    private fun clickReviewWriteOrModify(listPhoto: List<ReviewPhoto>?) {
        val data = ReviewWrMdResponse()

        var reviewId = 0
        var productId = 0L
        if (listPhoto != null) {
            if (CustomLog.flag) CustomLog.L("clickReviewWriteOrModify", "listPhoto", listPhoto)
        }
        if (deleteImageData.isNotEmpty()) {
            if (CustomLog.flag) CustomLog.L("clickReviewWriteOrModify", "deleteImageData", deleteImageData)
        }
        if (mViewModel.modifyReviewStatus.get()) {
            var item = reviewData as MyPageReviewContent
            data.sizeSatisfaction = getSizeSatisfaction(mViewModel.reviewSelectStatus1.get())
            data.colorSatisfaction = getColorSatisfaction(mViewModel.reviewSelectStatus2.get())
            data.lengthSatisfaction = getLengthSatisfaction(mViewModel.reviewSelectStatus3.get())
            data.productRating = getRating(ratingBarValue)
            data.orderProductGroupId = item.order.orderProdGroupId
            data.sellerId = item.order.sellerId
            data.productId = item.order.productId
            data.reviewId = item.review.id.toLong()
            data.textReview = mBinding.edittextReviewwriteText.text.toString()
            if (listPhoto != null) data.reviewPhotos.addAll(listPhoto)
            if (deleteImageData != null && deleteImageData.isNotEmpty()) {
                for (ph in deleteImageData) {
                    var pData = ReviewPhoto()
                    pData.reviewPhotoUrl = ph.reviewPhotoUrl
                    pData.photoOrder = ph.photoOrder
                    pData.imageStatus = ImageStatus.DELETED.name
                    pData.id = ph.id
                    data.reviewPhotos.add(pData)
                }
            }
            reviewId = item.review.id
            productId = item.order.productId
        } else {
            var item = reviewAvailableData
            data.sizeSatisfaction = getSizeSatisfaction(mViewModel.reviewSelectStatus1.get())
            data.colorSatisfaction = getColorSatisfaction(mViewModel.reviewSelectStatus2.get())
            data.lengthSatisfaction = getLengthSatisfaction(mViewModel.reviewSelectStatus3.get())
            data.productRating = getRating(ratingBarValue)
            data.orderProductGroupId = item!!.orderProdGroupId
            data.sellerId = item!!.sellerId
            data.productId = item!!.productId
            data.textReview = mBinding.edittextReviewwriteText.text.toString()
            if (listPhoto != null) data.reviewPhotos.addAll(listPhoto)
            reviewId = 0
            productId = item!!.productId
        }
        loadingIndicatorUtil?.show()
        //if(CustomLog.flag)CustomLog.L("clickReviewWriteOrModify",mViewModel.modifyReviewStatus.get()," data",data)

        if (data.sellerId > 0) {
            mViewModel.clickReviewWriteOrModify(data, productId, reviewId, object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    loadingIndicatorUtil?.dismiss()
                    if (CustomLog.flag) CustomLog.L("clickReviewWriteOrModify", "resultFlag", resultFlag, "value", value)
                    if (resultFlag) {
                        if (mViewModel.modifyReviewStatus.get()) {
                            ToastUtil.showMessage("리뷰 수정이 완료되었습니다.")
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            /**
                             * 포인트 적립 팝업 data 전달
                             * @author Hyeyeon Park
                             * @since 2019.10.29
                             */
                            if (value is ReviewData)
                                CommonUtil.startPointDialogActivity(this@ReviewWriteActivity, PointPopupType.REVIEW.type, value.savedPointResponse)
                            this@ReviewWriteActivity.setResult(Activity.RESULT_OK)
                            this@ReviewWriteActivity.finish()
                        }
                    }
                }
            })
        } else {
            CustomMessageDialog(message = "데이터에 오류가 있습니다.\n구하다로 문의 부탁드립니다.[RWA_SI0]", cancelButtonVisible = false,
                    confirmTask = {
                    }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
        }
    }

    private fun imageCheck() {
        val txt = mBinding.edittextReviewwriteText.text.toString()
        if (txt.isNullOrEmpty()) {
            ToastUtil.showMessage("리뷰를 입력해 주세요.")
            return
        }
        loadingIndicatorUtil?.show()
        if (mViewModel.mReviewEditPhotos.value != null && mViewModel.mReviewEditPhotos.value!!.isNotEmpty()) {
            var listPhoto = arrayListOf<ReviewPhoto>()
            for ((index, data) in mViewModel.mReviewEditPhotos.value!!.withIndex()) {
                if (data.id == -99) {
                    mViewModel.uploadImage(data.reviewPhotoUrl, mViewModel.reviewPhotoUrl, index, object : OnCallBackListener {
                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                            if (CustomLog.flag) CustomLog.L("clickReviewWriteOrModify", "uploadImage", resultFlag, "index", index)
                            var image = value as ImageResponse
                            var index = image.index!!
                            mViewModel.mReviewEditPhotos.value!!.get(index)
                            var reviewPhoto = ReviewPhoto()
                            reviewPhoto.id = 0
                            reviewPhoto.imageStatus = ImageStatus.ADDED.name
                            reviewPhoto.photoOrder = index
                            reviewPhoto.reviewPhotoUrl = image.url
                            listPhoto.add(index, reviewPhoto)
                            if (mViewModel.mReviewEditPhotos.value!!.size == listPhoto.size) {
                                clickReviewWriteOrModify(listPhoto)
                            }
                        }
                    })
                } else {
                    if (CustomLog.flag) CustomLog.L("clickReviewWriteOrModify", "no uploadImage", "index", index)
                    var reviewPhoto = ReviewPhoto()
                    reviewPhoto.id = mViewModel.mReviewEditPhotos.value!![index].id
                    reviewPhoto.imageStatus = if (mViewModel.modifyReviewStatus.get()) ImageStatus.UPDATED.name else ImageStatus.ADDED.name
                    reviewPhoto.photoOrder = index
                    reviewPhoto.reviewPhotoUrl = mViewModel.mReviewEditPhotos.value!![index].reviewPhotoUrl
                    listPhoto.add(index, reviewPhoto)
                    if (mViewModel.mReviewEditPhotos.value!!.size == listPhoto.size) {
                        if (CustomLog.flag) CustomLog.L("clickReviewWriteOrModify notNull", "listPhoto", listPhoto)
                        clickReviewWriteOrModify(listPhoto)
                    }
                }
            }
        } else {
            clickReviewWriteOrModify(null)
        }
    }


    private fun getRating(value: Float): String = when (value) {
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


    private fun getColorSatisfaction(value: Int): String = when (value) {
        0 -> "BRIGHTER"
        1 -> "SAME"
        2 -> "DARKER"
        else -> "SAME"
    }

    private fun getLengthSatisfaction(value: Int): String = when (value) {
        0 -> "SHORT"
        1 -> "REGULAR"
        2 -> "LONG"
        else -> "REGULAR"
    }

    private fun getSizeSatisfaction(value: Int): String = when (value) {
        0 -> "SMALL"
        1 -> "JUST_FIT"
        2 -> "LARGE"
        else -> "JUST_FIT"
    }

    private fun showDialog(msg: String, isFinish: Boolean) {
        CustomMessageDialog(message = msg, cancelButtonVisible = false,
                confirmTask = {
                    if (isFinish) finish()
                }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
    }


    override fun onDestroy() {
        super.onDestroy()
        if (loadingIndicatorUtil != null) {
            loadingIndicatorUtil?.hide()
            loadingIndicatorUtil = null
        }
    }
}