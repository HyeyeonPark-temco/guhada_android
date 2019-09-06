package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.viewmodel.UserClaimSellerViewModel
import io.temco.guhada.databinding.ActivityUserclaimsellerBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonImageAdapter

class UserClaimSellerActivity : BindActivity<ActivityUserclaimsellerBinding>(), OnClickSelectItemListener {

    private lateinit var mViewModel: UserClaimSellerViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

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
                    mLoadingIndicatorUtil.dismiss()
                    if(mViewModel.sellerInquireOrderList.isNullOrEmpty()){
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
                        mViewModel.checkUserClaimSellerOrderList.set(true)
                        setViewInit()
                    }
                }
            })
        }else onBackPressed()
    }


    private fun setViewInit() {
        if (mBinding.recyclerviewUserclaimsellerImagelist.adapter == null) {
            mBinding.recyclerviewUserclaimsellerImagelist.adapter = CommonImageAdapter().apply { mList = mViewModel.userClaimSellerImages.value!! }
            (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).mClickSelectItemListener = this@UserClaimSellerActivity
            mBinding.executePendingBindings()
        } else {
            (mBinding.recyclerviewUserclaimsellerImagelist.adapter as CommonImageAdapter).setItems(mViewModel.userClaimSellerImages.value!!)
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


    private fun setImageRecyclerViewVisible(){
        if(mViewModel.userClaimSellerImages.value!!.size > 0){
            mBinding.recyclerviewUserclaimsellerImagelist.visibility = View.VISIBLE
        }else{
            mBinding.recyclerviewUserclaimsellerImagelist.visibility = View.GONE
        }
    }


    override fun onBackPressed() {
        overridePendingTransition(0, 0)
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {

    }
}