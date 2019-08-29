package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
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
import io.temco.guhada.data.viewmodel.CreateBbsViewModel
import io.temco.guhada.databinding.ActivityCreatebbsBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonRoundImageAdapter
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
        mViewModel.getCommunityInfo()

        if (mBinding.recyclerviewCreatebbsImagelist.adapter == null) {
            mBinding.recyclerviewCreatebbsImagelist.adapter = CommonRoundImageAdapter().apply { mList = mViewModel.bbsPhotos.value!! }
            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageAdapter).mClickSelectItemListener = this@CreateBbsActivity
            mBinding.executePendingBindings()
        } else {
            (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageAdapter).setItems(mViewModel.bbsPhotos.value!!)
        }
        mViewModel.communityInfoList.observe(this, Observer {
            mViewModel.setCategoryList()
        })

        setClickEvent()
    }



    /**
     * type - 0 : delete, 1 : click
     */
    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
        when (type) {
            0 -> {
                (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageAdapter).mList.removeAt(index)
                (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageAdapter).notifyDataSetChanged()
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
                    var imageValue = ""
                    if (mViewModel.bbsPhotos.value!!.size > mViewModel.selectedImageIndex) {
                        mViewModel.bbsPhotos.value!!.removeAt(mViewModel.selectedImageIndex)
                        imageValue = fileNm
                        mViewModel.bbsPhotos.value!!.add(mViewModel.selectedImageIndex, imageValue)
                        (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageAdapter).notifyDataSetChanged()
                    } else {
                        imageValue = fileNm
                        mViewModel.bbsPhotos.value!!.add(imageValue)
                        (mBinding.recyclerviewCreatebbsImagelist.adapter as CommonRoundImageAdapter).notifyDataSetChanged()
                    }
                    setImageRecyclerViewVisible()
                    if (CustomLog.flag) CustomLog.L("ReviewWriteActivity", "fileNm", fileNm)
                }
            }
        } else {
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
            mViewModel.postBbs(setResponseData(), object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(CustomLog.flag) CustomLog.L("postBbs callBackListener","resultFlag",resultFlag,"value",value)
                    mLoadingIndicatorUtil.dismiss()
                }
            })

        }

        mBinding.setOnClickTempList{

        }

        mBinding.setOnClickTempSave{
            setResponseData()
        }

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

/*
    private fun sendReportData() {
        if (mBinding.edittextReportTitle.text.isNullOrEmpty()) {
            showDialog("제목을 입력해 주세요.", false)
            return
        }
        if (mBinding.edittextReportText.text.isNullOrEmpty()) {
            showDialog("내용을 입력해 주세요.", false)
            return
        }
        if (!mViewModel.checkTermReport.get()) {
            showDialog(resources.getString(R.string.user_size_update_check_desc), false)
            return
        }
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        var response = ReportResponse()
        response.content = mBinding.edittextReportText.text.toString()
        response.title = mBinding.edittextReportTitle.text.toString()
        when (mViewModel.reportType) {
            0 -> response.targetId = mViewModel.productData!!.productId
            1 -> response.targetId = mViewModel.userData!!.userDetail.id
            2 -> response.targetId = mViewModel.communityData!!.id
            3 -> response.targetId = mViewModel.commentData!!.id
        }
        response.reporter = mViewModel.writeUserInfo.value!!.userDetail.id
        response.reportType = mViewModel.reportTypeList.value!![mViewModel.selectReportTypeIndex].name
        response.reportTarget = mViewModel.reportTarget.name

        mLoadingIndicatorUtil.show()
        if (mViewModel.reportPhotos.value.isNullOrEmpty()) {
            if (CustomLog.flag) CustomLog.L("setOnClickWriteButton isNullOrEmpty", "response", response)
            mViewModel.saveReport(response, object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mLoadingIndicatorUtil.dismiss()
                    if (resultFlag) showDialog("신고하기를 완료하였습니다.", true)
                    else showDialog("신고하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요." + value.toString(), false)
                }
            })
        } else {
            for ((index, file) in mViewModel.reportPhotos.value!!.iterator().withIndex()) {
                if (CustomLog.flag) CustomLog.L("setOnClickWriteButton", "file", file)
                mViewModel.imageUpload(file, index, object : OnCallBackListener {
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        if (CustomLog.flag) CustomLog.L("setOnClickWriteButton callBackListener", "resultFlag", resultFlag, "value", value)
                        var data = value as ImageResponse
                        response.imageUrls.add(data.url)
                        if (response.imageUrls.size == mViewModel.reportPhotos.value!!.size) {
                            if (CustomLog.flag) CustomLog.L("setOnClickWriteButton isNullOrEmpty not", "response", response)
                            mViewModel.saveReport(response, object : OnCallBackListener {
                                override fun callBackListener(resultFlag: Boolean, value: Any) {
                                    mLoadingIndicatorUtil.dismiss()
                                    if (resultFlag) showDialog("신고하기를 완료하였습니다.", true)
                                    else showDialog("신고하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요." + value.toString(), false)
                                }
                            })
                        }
                    }
                })
            }
        }
    }*/

    private fun showDialog(msg: String, isFinish: Boolean) {
        CustomMessageDialog(message = msg, cancelButtonVisible = false,
                confirmTask = {
                    if (isFinish) finish()
                }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
    }

    ////////////////////////////////////////////////

}