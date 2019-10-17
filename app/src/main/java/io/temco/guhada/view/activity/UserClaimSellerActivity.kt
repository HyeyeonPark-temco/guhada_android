package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.*
import android.text.style.StyleSpan
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.SellerInquireOrder
import io.temco.guhada.data.model.UserClaimGuhadaResponse
import io.temco.guhada.data.model.UserClaimSellerResponse
import io.temco.guhada.data.viewmodel.UserClaimSellerViewModel
import io.temco.guhada.databinding.ActivityUserclaimsellerBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonImageAdapter
import io.temco.guhada.view.adapter.UserClaimSellerProductAdapter

class UserClaimSellerActivity : BindActivity<ActivityUserclaimsellerBinding>(), OnClickSelectItemListener {

    private lateinit var mViewModel: UserClaimSellerViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    // ----------------------------------------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = this@UserClaimSellerActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_userclaimseller
    override fun getViewType(): Type.View = Type.View.USERCLAIM_SELLER

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = UserClaimSellerViewModel(this)
        mBinding.viewModel = mViewModel

        mViewModel.sellerId = intent?.extras?.getLong("sellerId") ?: -1
        mViewModel.productId = intent?.extras?.getLong("productId") ?: -1
        mViewModel.orderProdGroupId = intent?.extras?.getLong("orderProdGroupId") ?: -1
        mViewModel.checkUserClaimSellerOrderList.set(false)

        if(mViewModel.productId != -1L){
            mLoadingIndicatorUtil.show()
            mViewModel.getSellerInquireOrder(object  : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mViewModel.userId = CommonUtil.checkUserId()
                    mLoadingIndicatorUtil.dismiss()
                    if(mViewModel.userSellerInquireOrderList.get().isNullOrEmpty()){
                        CommonViewUtil.showDialog(this@UserClaimSellerActivity, "해당 판매자에게 주문한 기록을 찾을 수 없습니다.\n" +
                                "상품 문의를 통해서만 문의가 가능합니다.", object:OnBaseDialogListener{
                            override fun onClickOk() {
                                onBackPressed()
                            }
                        }, object:OnBaseDialogListener{
                            override fun onClickOk() {
                                setResult(Activity.RESULT_FIRST_USER)
                                onBackPressed()
                            }
                        }, resources.getString(R.string.productdetail_qna_button_contactforproduct))
                    }else{
                        mViewModel.getUserClaimSellerData()
                        mViewModel.checkUserClaimSellerOrderList.set(true)
                        setViewInit()
                    }
                }
            })
        }else onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        try{
            if (::mLoadingIndicatorUtil.isInitialized) {
                mLoadingIndicatorUtil.dismiss()
            }
        }catch (e : Exception){
            if(CustomLog.flag) CustomLog.E(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 갤러리에서 선택된 사진 파일 주소 받아옴
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                Flag.RequestCode.IMAGE_GALLERY -> {
                    var fileNm = data!!.extras.getString("file_name")
                    if(!fileNm.substring(0,4).equals("http",true)){
                        var imageValue = ""
                        if(mViewModel.selectedImageIndex != -1 && mViewModel.userClaimSellerImages.value!!.size > mViewModel.selectedImageIndex){
                            mViewModel.userClaimSellerImages.value!!.removeAt(mViewModel.selectedImageIndex)
                            imageValue = fileNm
                            mViewModel.userClaimSellerImages.value!!.add(mViewModel.selectedImageIndex,imageValue)
                            (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                        }else{
                            imageValue = fileNm
                            mViewModel.userClaimSellerImages.value!!.add(imageValue)
                            (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                        }
                        setImageRecyclerViewVisible()
                        if(CustomLog.flag) CustomLog.L("ReviewWriteActivity","fileNm",fileNm)
                    }else{
                        CommonViewUtil.showDialog(this@UserClaimSellerActivity,"외부 이미지 링크가 아닌 파일만 등록 가능합니다.",false,false)
                    }
                }
            }
        }else{
            mViewModel.selectedImageIndex = -1
        }
    }

    override fun onBackPressed() {
        if(!mViewModel.userSellerInquireOrderList.get().isNullOrEmpty() && mViewModel.userClaimSellerProductListShow.get()){
            mViewModel.userClaimSellerProductListShow.set(false)
        }else{
            overridePendingTransition(0, 0)
            super.onBackPressed()
            overridePendingTransition(0, 0)
        }
    }

    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
        when(type){
            0 -> {
                (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).mList.removeAt(index)
                (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                setImageRecyclerViewVisible()
            }
            1 -> {
                mViewModel.selectedImageIndex = index
                CommonUtil.startImageGallery(this)
            }
        }
    }

    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun setImageRecyclerViewVisible(){
        if(mViewModel.userClaimSellerImages.value!!.size > 0){
            mBinding.recyclerviewUserclaimsellerImagelist.visibility = View.VISIBLE
        }else{
            mBinding.recyclerviewUserclaimsellerImagelist.visibility = View.GONE
        }
    }

    private fun setViewInit() {
        if (mBinding.recyclerviewUserclaimsellerImagelist.adapter == null) {
            mBinding.recyclerviewUserclaimsellerImagelist.adapter = CommonImageAdapter().apply { mList = mViewModel.userClaimSellerImages.value!! }
            (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).mClickSelectItemListener = this@UserClaimSellerActivity
        } else {
            (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).setItems(mViewModel.userClaimSellerImages.value!!)
        }


        if (mBinding.recyclerviewUserclaimsellerProductlist.adapter == null) {
            mBinding.recyclerviewUserclaimsellerProductlist.adapter = UserClaimSellerProductAdapter().apply { mList = mViewModel.userSellerInquireOrderList.get()!! }
            (mBinding.recyclerviewUserclaimsellerProductlist.adapter as UserClaimSellerProductAdapter).mClickSelectItemListener = object : OnClickSelectItemListener{
                override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
                    var data = value as SellerInquireOrder
                    setProductTitle(data, index)
                }
            }
        } else {
            (mBinding.recyclerviewUserclaimsellerProductlist.adapter as UserClaimSellerProductAdapter).setItems(mViewModel.userSellerInquireOrderList.get()!!)
        }


        mBinding.edittextUserclaimsellerText.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty() || s.isNullOrBlank()){
                    mViewModel.editTextUserClaimSellerTxtCount.set("0")
                }else{
                    if(s != null && s.isNotEmpty()){
                        mViewModel.editTextUserClaimSellerTxtCount.set(s!!.length.toString())
                    }
                }
            }
        })

        mBinding.setOnClickGetImage {
            if(CustomLog.flag) CustomLog.L("ReviewWriteActivity","setOnClickGetImage",mViewModel.userClaimSellerImages.value!!.size)
            if(mViewModel.userClaimSellerImages.value!!.size < 10){
                mViewModel.selectedImageIndex = mViewModel.userClaimSellerImages.value!!.size
                CommonUtil.startImageGallery(this@UserClaimSellerActivity)
            }else{
                ToastUtil.showMessage(resources.getString(R.string.review_activity_maximage_desc))
            }
        }
        mBinding.executePendingBindings()

        if(mViewModel.orderProdGroupId != -1L){
            loop1@for ((index, vlu) in mViewModel.userSellerInquireOrderList.get()!!.withIndex()){
                if(vlu.orderProdGroupId == mViewModel.orderProdGroupId){
                    setProductTitle(vlu ,index)
                    break@loop1
                }
            }
        }

        mBinding.setOnClickOkButton { sendData() }
        mBinding.setOnClickCloseButton { finish() }
    }


    private fun setProductTitle(data : SellerInquireOrder, index : Int){
        var text = data.brandName + " " + (if(TextUtils.isEmpty(data.season)) "" else data.season +" ") + data.productName
        val ssb = SpannableStringBuilder(text)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, data.brandName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Style
        mViewModel.userClaimSellerProductData.set(ssb)
        mViewModel.userClaimSellerProductIndex.set(index)
        mViewModel.onClickUserClaimSellerProduct()
    }


    private fun sendData(){
        if(mViewModel.userClaimSellerProductIndex.get() == -1){
            CommonViewUtil.showDialog(this@UserClaimSellerActivity, "주문을 선택해주세요", false, false)
            return
        }
        if(mViewModel.userClaimSellerDescriptionIndex.get() == -1){
            CommonViewUtil.showDialog(this@UserClaimSellerActivity, "문의 유형을 선택해주세요", false, false)
            return
        }
        if(TextUtils.isEmpty(mBinding.edittextUserclaimsellerText.text.toString())){
            CommonViewUtil.showDialog(this@UserClaimSellerActivity, "내용을 입력해 주세요", false, false)
            return
        }
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mLoadingIndicatorUtil.show()
        var response = UserClaimSellerResponse()
        response.contents = mBinding.edittextUserclaimsellerText.text.toString()
        response.title = mViewModel.userClaimSellerProductData.get().toString()
        response.type = mViewModel.userClaimSellerDescriptionData.get(mViewModel.userClaimSellerDescriptionIndex.get()).name
        response.sellerId = mViewModel.sellerId
        response.orderProdGroupId = mViewModel.userSellerInquireOrderList.get()?.get(mViewModel.userClaimSellerProductIndex.get())?.orderProdGroupId ?: 0
        if(CustomLog.flag)CustomLog.L("setOnClickWriteButton callBackListener","response", response.toString())

        if(mViewModel.userClaimSellerImages.value.isNullOrEmpty()){
            if(CustomLog.flag)CustomLog.L("setOnClickWriteButton isNullOrEmpty","response", response)
            mViewModel.repository.saveUserClaimSeller(mViewModel.userId, response, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mLoadingIndicatorUtil.dismiss()
                    if(resultFlag) CommonViewUtil.showDialog(this@UserClaimSellerActivity, "판매자 문의하기를 완료하였습니다.", false,true)
                    else CommonViewUtil.showDialog(this@UserClaimSellerActivity, "판매자 문의하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요."+value.toString(),false,false)
                }
            })
        }else{
            for((index,file) in mViewModel.userClaimSellerImages.value!!.iterator().withIndex()){
                if(CustomLog.flag)CustomLog.L("setOnClickWriteButton","file", file)
                mViewModel.repository.uploadImage(file, mViewModel.userImageUrl, index, object : OnCallBackListener{
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        if(CustomLog.flag)CustomLog.L("setOnClickWriteButton callBackListener","resultFlag", resultFlag, "value", value)
                        var data = value as ImageResponse
                        response.imageUrls.add(data.url)
                        if(response.imageUrls.size == mViewModel.userClaimSellerImages.value!!.size){
                            if(CustomLog.flag)CustomLog.L("setOnClickWriteButton isNullOrEmpty not","response", response)
                            mViewModel.repository.saveUserClaimSeller(mViewModel.userId, response, object : OnCallBackListener{
                                override fun callBackListener(resultFlag: Boolean, value: Any) {
                                    mLoadingIndicatorUtil.dismiss()
                                    if(resultFlag)CommonViewUtil.showDialog(this@UserClaimSellerActivity, "판매자 문의하기를 완료하였습니다.", false,true)
                                    else CommonViewUtil.showDialog(this@UserClaimSellerActivity, "판매자 문의하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요."+value.toString(), false,false)
                                }
                            })
                        }
                    }
                })
            }
        }

    }

    ////////////////////////////////////////////////


}