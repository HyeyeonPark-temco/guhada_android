package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.CreateBbsResponse
import io.temco.guhada.data.model.CreateBbsTempResponse
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.community.CommunityTempInfo
import io.temco.guhada.data.viewmodel.CreateBbsViewModel
import io.temco.guhada.databinding.ActivityCreatebbsBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonRoundImageAdapter
import io.temco.guhada.view.adapter.CommonRoundImageResponseAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog

/**
 * @author park jungho
 *
 * 게시글 등록 화면
 */
class CreateBbsActivity : BindActivity<ActivityCreatebbsBinding>(), OnClickSelectItemListener {
    private lateinit var mViewModel: CreateBbsViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    // ----------------------------------------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_createbbs
    override fun getViewType(): Type.View = Type.View.REPORT_WRITE

    override fun init() {
        mViewModel = CreateBbsViewModel(this)
        mBinding.viewModel = mViewModel
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)

        if(intent.extras != null && intent.extras.containsKey("currentIndex")){
            mViewModel.selectInfoIndex = intent.extras.getInt("currentIndex")
        }
        if(intent.extras != null && intent.extras.containsKey("modifyData")){
            mViewModel.modifyBbsData = intent.extras.getSerializable("modifyData") as CreateBbsResponse
            mViewModel.bbsId = intent.extras.getLong("bbsId")
            mViewModel.communityDetailModifyData.set(true)
        }else{
            mViewModel.modifyBbsData = CreateBbsResponse()
            mViewModel.communityDetailModifyData.set(false)
        }

        mViewModel.getCommunityInfo()
        mViewModel.communityInfoList.observe(this, Observer {
            setCategoryData()
        })
        setView(false)
        setClickEvent()
    }



    /**
     * type - 0 : delete, 1 : click
     */
    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
        when (type) {
            0 -> {
                (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).mList.removeAt(index)
                (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).notifyDataSetChanged()
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
                    if(!fileNm.substring(0,4).equals("http",true)){
                        var imageValue = ImageResponse()
                        if (mViewModel.bbsPhotos.value!!.size > mViewModel.selectedImageIndex) {
                            mViewModel.bbsPhotos.value!!.removeAt(mViewModel.selectedImageIndex)
                            imageValue.fileSize = -1
                            imageValue.url = fileNm
                            mViewModel.bbsPhotos.value!!.add(mViewModel.selectedImageIndex, imageValue)
                            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).notifyDataSetChanged()
                        } else {
                            imageValue.fileSize = -1
                            imageValue.url = fileNm
                            mViewModel.bbsPhotos.value!!.add(imageValue)
                            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).notifyDataSetChanged()
                        }
                        setImageRecyclerViewVisible()
                        if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", "fileNm", fileNm)
                    }else{
                        showDialog("외부 이미지 링크가 아닌 파일만 등록 가능합니다.",false, null)
                    }
                }
                Flag.RequestCode.COMMUNITY_DETAIL_TEMP_LIST -> {
                    if(data!=null && data!!.extras!!.containsKey("tempData")!!){
                        var value = data!!.extras!!.getSerializable("tempData") as CreateBbsResponse
                        mViewModel.bbsTempId = data!!.extras!!.getLong("tempDataId")!!
                        mViewModel.selectedCategoryIndex = 0
                        mViewModel.selectedFilterIndex = 0
                        mViewModel.modifyBbsData = value
                        mViewModel.bbsPhotos.value?.clear()
                        setView(true)
                    }
                }
            }
        } else {
            mViewModel.selectedImageIndex = -1
        }
    }

    override fun onStart() {
        super.onStart()
        getTmpList()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::mLoadingIndicatorUtil.isInitialized) {
                mLoadingIndicatorUtil.dismiss()
            }
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
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

    private fun setView(reLoad : Boolean){
        mBinding.item = mViewModel.modifyBbsData
        if(reLoad) {
            mBinding.recyclerviewCreatebbsImagelist.adapter = null
            setCategoryData()
        }

        if (mBinding.recyclerviewCreatebbsImagelist.adapter == null) {
            mBinding.recyclerviewCreatebbsImagelist.adapter = CommonRoundImageResponseAdapter().apply { mList = mViewModel.bbsPhotos.value!! }
            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).mClickSelectItemListener = this@CreateBbsActivity
        } else {
            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).setItems(mViewModel.bbsPhotos.value!!)
        }

        if(mViewModel.communityDetailModifyData.get()){
            mViewModel.bbsPhotos.value!!.addAll(mViewModel.modifyBbsData.imageList)
            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageResponseAdapter).notifyDataSetChanged()
        }
        setImageRecyclerViewVisible()
        mBinding.executePendingBindings()
    }


    private fun setCategoryData(){
        synchronized(this){
            mViewModel.setCategoryList()
            if(mViewModel.communityDetailModifyData.get()){
                if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "init ", "mViewModel.modifyBbsData -----",mViewModel.modifyBbsData)
                loop@for ((index, value) in mViewModel.communityInfoList.value!!.iterator().withIndex()){
                    if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "init ", "communityCategoryId -----",value.communityCategoryId.toLong())
                    if(value.communityCategoryId.toLong() ==  mViewModel.modifyBbsData.categoryId){
                        mViewModel.onBbsCategorySelected(index)
                        if(!value.communityCategorySub.categoryFilterList.isNullOrEmpty() &&
                                (mViewModel.modifyBbsData.categoryFilterId != null && mViewModel.modifyBbsData.categoryFilterId!! > 0)){
                            for ((index2, filter) in value.communityCategorySub.categoryFilterList.iterator().withIndex()){
                                if(filter.id.toLong() == mViewModel.modifyBbsData.categoryFilterId!!){
                                    mViewModel.selectedFilterInit = true
                                    mViewModel.onFilterSelect(index2)
                                    break@loop
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun setImageRecyclerViewVisible() {
        if (mViewModel.bbsPhotos.value!!.size > 0) {
            mViewModel.visibleImageCheckLayout.set(true)
        } else {
            mViewModel.visibleImageCheckLayout.set(false)
        }
    }


    private fun setClickEvent(){
        mBinding.setOnClickCloseButton { finish() }

        mBinding.setOnClickGetImage {
            if(CustomLog.flag)CustomLog.L("CreateBbsActivity","setOnClickGetImage",mViewModel.bbsPhotos.value!!.size)
            if(mViewModel.bbsPhotos.value!!.size < 10){
                mViewModel.selectedImageIndex = mViewModel.bbsPhotos.value!!.size
                CommonUtil.startImageGallery(this)
            }else{
                ToastUtil.showMessage(resources.getString(R.string.review_activity_maximage_desc))
            }
        }

        mBinding.setOnClickWriteButton {
            mLoadingIndicatorUtil.show()
            postDetailData()
        }

        mBinding.setOnClickTempList{
            this@CreateBbsActivity.startActivityForResult(Intent(this@CreateBbsActivity, TempBbsListActivity::class.java), Flag.RequestCode.COMMUNITY_DETAIL_TEMP_LIST)
        }

        mBinding.setOnClickTempSave{
            mLoadingIndicatorUtil.show()
            mViewModel.postTempBbs(setResponseTempData(),object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mLoadingIndicatorUtil.dismiss()
                    getTmpList()
                    if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "setOnClickTempList ----- resultFlag",resultFlag,"value",value)
                }
            })
        }

    }

    private fun postDetailData(){
        if(mViewModel.bbsPhotos.value.isNullOrEmpty()){
            if(mViewModel.communityDetailModifyData.get()) modifyBbs(setResponseData())
            else createBbs(setResponseData())
        }else{
            var response = setResponseData()
            for ((index,file) in mViewModel.bbsPhotos.value!!.iterator().withIndex()){
                if(file.fileSize == -1L && !"http".equals(file.url.substring(0,4), true)){
                    mViewModel.imageUpload(file.url,index, object : OnCallBackListener{
                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                            if(resultFlag){
                                response.imageList.add(value as ImageResponse)
                                if(response.imageList.size == mViewModel.bbsPhotos.value!!.size){
                                    response.imageUrl = response.imageList[0].url
                                    if(mViewModel.communityDetailModifyData.get()) modifyBbs(response)
                                    else createBbs(response)
                                }
                            }else{
                                mLoadingIndicatorUtil.dismiss()
                                showDialog("이미지 등록중 오류가 발생되었습니다.",false, null)
                            }
                        }
                    })
                }else{
                    response.imageList.add(file)
                    if(response.imageList.size == mViewModel.bbsPhotos.value!!.size){
                        response.imageUrl = response.imageList[0].url
                        if(mViewModel.communityDetailModifyData.get()) modifyBbs(response)
                        else createBbs(response)
                    }
                }
            }
        }
    }

    private fun createBbs(res : CreateBbsResponse){
        mViewModel.postBbs(res, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag) CustomLog.L("postBbs imageUpload callBackListener","resultFlag",resultFlag,"value",value)
                mLoadingIndicatorUtil.dismiss()
                if(resultFlag){
                    showDialog(getFinishMsg(),true, Activity.RESULT_OK)
                }else{
                    showDialog("등록중 오류가 발생되었습니다.",false, null)
                }
            }
        })
    }


    private fun modifyBbs(res : CreateBbsResponse){
        mViewModel.modifyBbs(res, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag) CustomLog.L("modifyBbs callBackListener","resultFlag",resultFlag,"value",value)
                mLoadingIndicatorUtil.dismiss()
                if(resultFlag){
                    showDialog(getFinishMsg(),true, Activity.RESULT_OK)
                }else{
                    showDialog("등록중 오류가 발생되었습니다.",false, null)
                }
            }
        })
    }


    private fun getFinishMsg() : String =
        when(mViewModel.communityDetailModifyData.get()){
            true -> "글이 수정되었습니다."
            false -> "글이 등록되었습니다."
        }


    private fun getTmpList(){
        mViewModel.getBbsTempListData(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                var size = value as ArrayList<CommunityTempInfo>
                if(!size.isNullOrEmpty()){
                    mBinding.textviewCreatebbsTmplist.visibility = View.VISIBLE
                    mBinding.textviewCreatebbsTmplist.text = size.size.toString()
                }else{
                    mBinding.textviewCreatebbsTmplist.visibility = View.GONE
                }
                if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "init ", "getBbsTempListData ----- resultFlag",resultFlag,"value",value)
            }
        })
    }

    private fun setResponseData() : CreateBbsResponse{
        var data = CreateBbsResponse()
        data.brandName = mBinding.edittextReportBrand.text.toString()
        data.contents = mBinding.edittextReportText.text.toString()
        data.dealName = mBinding.edittextReportProduct.text.toString()
        data.title = mBinding.edittextReportTitle.text.toString()
        data.categoryId = mViewModel.communityInfoList.value!![mViewModel.selectedCategoryIndex].communityCategoryId.toLong()
        if(CustomLog.flag)CustomLog.L("CreateBbsActivity","mViewModel.selectedCategoryIndex",mViewModel.selectedCategoryIndex)
        if(CustomLog.flag)CustomLog.L("CreateBbsActivity","mViewModel.selectedFilterIndex",mViewModel.selectedFilterIndex)
        if(mViewModel.selectedFilterIndex != -1){
            data.categoryFilterId = mViewModel.communityInfoList.value!![mViewModel.selectedCategoryIndex].communityCategorySub.categoryFilterList[mViewModel.selectedFilterIndex].id.toLong()
        }else{
            data.categoryFilterId = null
        }
        if(CustomLog.flag)CustomLog.L("CreateBbsActivity","setResponseData",data.toString())
        return data
    }



    private fun setResponseTempData() : CreateBbsTempResponse{
        var data = CreateBbsTempResponse()
        data.brandName = mBinding.edittextReportBrand.text.toString()
        data.contents = mBinding.edittextReportText.text.toString()
        data.dealName = mBinding.edittextReportProduct.text.toString()
        data.title = mBinding.edittextReportTitle.text.toString()
        data.brandId = 0
        data.dealId = 0
        if(CustomLog.flag)CustomLog.L("CreateBbsActivity","setResponseData",data.toString())
        return data
    }


    private fun showDialog(msg: String, isFinish: Boolean, resultCode : Int?) {
        CustomMessageDialog(message = msg, cancelButtonVisible = false,
                confirmTask = {
                    if(resultCode!=null) setResult(resultCode)
                    if (isFinish) finish()
                }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
    }

    ////////////////////////////////////////////////

}