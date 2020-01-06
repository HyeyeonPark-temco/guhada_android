package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.UserClaimGuhadaResponse
import io.temco.guhada.data.viewmodel.UserClaimGuhadaViewModel
import io.temco.guhada.databinding.ActivityUserclaimguhadaBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonImageAdapter
import io.temco.guhada.view.fragment.ListBottomSheetFragment

class UserClaimGuhadaActivity : BindActivity<ActivityUserclaimguhadaBinding>(), OnClickSelectItemListener {

    private lateinit var mViewModel: UserClaimGuhadaViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    // ----------------------------------------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = this@UserClaimGuhadaActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_userclaimguhada
    override fun getViewType(): Type.View = Type.View.USERCLAIM_GUHADA

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = UserClaimGuhadaViewModel(this)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }
        mBinding.setOnClickOkButton { sendData() }

        if (mBinding.recyclerviewUserclaimguhadaImagelist.adapter == null) {
            mBinding.recyclerviewUserclaimguhadaImagelist.adapter = CommonImageAdapter().apply { mList = mViewModel.userClaimGuhadaImages.value!! }
            (mBinding.recyclerviewUserclaimguhadaImagelist.adapter as CommonImageAdapter).mClickSelectItemListener = this@UserClaimGuhadaActivity
            mBinding.executePendingBindings()
        } else {
            (mBinding.recyclerviewUserclaimguhadaImagelist.adapter as CommonImageAdapter).setItems(mViewModel.userClaimGuhadaImages.value!!)
        }

        mBinding.edittextUserclaimguhadaText.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty() || s.isNullOrBlank()){
                    mViewModel.editTextUserClaimGuhadaTxtCount.set("0")
                }else{
                    if(s != null && s.isNotEmpty()){
                        mViewModel.editTextUserClaimGuhadaTxtCount.set(s!!.length.toString())
                    }
                }
            }
        })

        mBinding.setOnClickGetImage {
            if(CustomLog.flag)CustomLog.L("ReviewWriteActivity","setOnClickGetImage",mViewModel.userClaimGuhadaImages.value!!.size)
            if(mViewModel.userClaimGuhadaImages.value!!.size < 10){
                mViewModel.selectedImageIndex = mViewModel.userClaimGuhadaImages.value!!.size
                CommonUtil.startImageGallery(this@UserClaimGuhadaActivity)
            }else{
                ToastUtil.showMessage(resources.getString(R.string.review_activity_maximage_desc))
            }
        }

        mBinding.setOnClickUserClaimDescription {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mList = mViewModel.userClaimDescriptionList.get()!!
                this.mTitle = mBinding.root.context.getString(R.string.community_filter_title1)
                this.selectedIndex = mViewModel.userClaimDescriptionIndex.get()
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        if(position != mViewModel.userClaimDescriptionIndex.get()){
                            mViewModel.userClaimDescriptionMessage.set(mViewModel.userClaimDescriptionList.get()!![position])
                            mViewModel.userClaimDescriptionIndex.set(position)
                            mViewModel.setUserClaimDescriptionChildList()
                        }
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }
            bottomSheet.show(supportFragmentManager, baseTag)
        }

        mBinding.setOnClickUserClaimDescriptionChild {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mList = mViewModel.userClaimDescriptionChildList.get()!!
                this.mTitle = mBinding.root.context.getString(R.string.community_filter_title1)
                this.selectedIndex = mViewModel.userClaimDescriptionChildIndex.get()
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        if(position != mViewModel.userClaimDescriptionChildIndex.get()){
                            mViewModel.userClaimDescriptionChildMessage.set(mViewModel.userClaimDescriptionChildList.get()!![position])
                            mViewModel.userClaimDescriptionChildIndex.set(position)
                        }
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }
            bottomSheet.show(supportFragmentManager, baseTag)
        }

        setViewInit()
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
                        if(mViewModel.selectedImageIndex != -1 && mViewModel.userClaimGuhadaImages.value!!.size > mViewModel.selectedImageIndex){
                            mViewModel.userClaimGuhadaImages.value!!.removeAt(mViewModel.selectedImageIndex)
                            imageValue = fileNm
                            mViewModel.userClaimGuhadaImages.value!!.add(mViewModel.selectedImageIndex,imageValue)
                            (mBinding.recyclerviewUserclaimguhadaImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                        }else{
                            imageValue = fileNm
                            mViewModel.userClaimGuhadaImages.value!!.add(imageValue)
                            (mBinding.recyclerviewUserclaimguhadaImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                        }
                        setImageRecyclerViewVisible()
                        if(CustomLog.flag) CustomLog.L("ReviewWriteActivity","fileNm",fileNm)
                    }else{
                        CommonViewUtil.showDialog(this@UserClaimGuhadaActivity,"외부 이미지 링크가 아닌 파일만 등록 가능합니다.",false,false)
                    }
                }
            }
        }else{
            mViewModel.selectedImageIndex = -1
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
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

    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
        when(type){
            0 -> {
                (mBinding.recyclerviewUserclaimguhadaImagelist.adapter as CommonImageAdapter).mList.removeAt(index)
                (mBinding.recyclerviewUserclaimguhadaImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
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
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun setViewInit() {
        mLoadingIndicatorUtil.show()
        mViewModel.getUserInfo(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mLoadingIndicatorUtil.dismiss()
                if(resultFlag){
                    mBinding.email = mViewModel.writeUserInfo.email
                }else{
                    CommonViewUtil.showDialog(this@UserClaimGuhadaActivity,"회원정보를 가져오는중 오류가 발생하였습니다.",false,true)
                }
            }
        })

    }

    private fun setImageRecyclerViewVisible(){
        if(mViewModel.userClaimGuhadaImages.value!!.size > 0){
            mBinding.recyclerviewUserclaimguhadaImagelist.visibility = View.VISIBLE
        }else{
            mBinding.recyclerviewUserclaimguhadaImagelist.visibility = View.GONE
        }
    }

    private fun sendData(){
        if(mViewModel.userClaimDescriptionIndex.get() == -1){
            CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "문의 유형을 선택해주세요", false, false)
            return
        }
        if(mViewModel.userClaimDescriptionChildIndex.get() == -1){
            CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "상세 유형을 선택해주세요", false, false)
            return
        }
        if(TextUtils.isEmpty(mBinding.edittextUserclaimguhadaTitle.text.toString())){
            CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "제목을 입력해 주세요", false, false)
            return
        }
        if(TextUtils.isEmpty(mBinding.edittextUserclaimguhadaText.text.toString())){
            CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "내용을 입력해 주세요", false, false)
            return
        }
        if(!mViewModel.checkTermUserClaimGuhada.get()) {
            CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, resources.getString(R.string.user_size_update_check_desc), false,false)
            return
        }
        var response = UserClaimGuhadaResponse()
        response.content = mBinding.edittextUserclaimguhadaText.text.toString()
        response.title = mBinding.edittextUserclaimguhadaTitle.text.toString()
        response.typeCode = mViewModel.userClaimDescriptionData.get(mViewModel.userClaimDescriptionIndex.get()).children!!.get(mViewModel.userClaimDescriptionChildIndex.get()).code


        mLoadingIndicatorUtil.show()
        if(mViewModel.userClaimGuhadaImages.value.isNullOrEmpty()){
            if(CustomLog.flag)CustomLog.L("setOnClickWriteButton isNullOrEmpty","response", response)
            mViewModel.repository.saveUserClaimGuhada(mViewModel.userId, response, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mLoadingIndicatorUtil.dismiss()
                    if(resultFlag) CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "문의하기를 완료하였습니다.", false,true)
                    else CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "문의하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요."+value.toString(),false,false)
                }
            })
        }else{
            for((index,file) in mViewModel.userClaimGuhadaImages.value!!.iterator().withIndex()){
                if(CustomLog.flag)CustomLog.L("setOnClickWriteButton","file", file)
                mViewModel.repository.uploadImage(file, mViewModel.userImageUrl, index, object : OnCallBackListener{
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        if(CustomLog.flag)CustomLog.L("setOnClickWriteButton callBackListener","resultFlag", resultFlag, "value", value)
                        var data = value as ImageResponse
                        response.imageUrls.add(data.url)
                        if(response.imageUrls.size == mViewModel.userClaimGuhadaImages.value!!.size){
                            if(CustomLog.flag)CustomLog.L("setOnClickWriteButton isNullOrEmpty not","response", response)
                            mViewModel.repository.saveUserClaimGuhada(mViewModel.userId, response, object : OnCallBackListener{
                                override fun callBackListener(resultFlag: Boolean, value: Any) {
                                    mLoadingIndicatorUtil.dismiss()
                                    if(resultFlag)CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "문의하기를 완료하였습니다.", false,true)
                                    else CommonViewUtil.showDialog(this@UserClaimGuhadaActivity, "문의하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요."+value.toString(), false,false)
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