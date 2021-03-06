package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.CreateBbsResponse
import io.temco.guhada.data.model.CreateBbsTempResponse
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.community.CommunityTempInfo
import io.temco.guhada.data.viewmodel.CreateBbsViewModel
import io.temco.guhada.databinding.ActivityCreatebbsBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonRoundImageResponseAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.ListBottomSheetFragment


/**
 * @author park jungho
 *
 * 게시글 등록 화면
 */
class CreateBbsActivity : BindActivity<ActivityCreatebbsBinding>(), OnClickSelectItemListener {
    private lateinit var mViewModel: CreateBbsViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    private lateinit var mTitleFragment: ListBottomSheetFragment
    private lateinit var mSubTitleFragment: ListBottomSheetFragment

    private var initTabIndex = 0
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
            initTabIndex = intent.extras.getInt("currentIndex")
        }
        if(intent.extras != null && intent.extras.containsKey("modifyData")){
            mViewModel.modifyBbsData = intent.extras.getSerializable("modifyData") as CreateBbsResponse
            mViewModel.bbsId = intent.extras.getLong("bbsId")
            mViewModel.communityDetailModifyData.set(true)
        }else{
            mViewModel.modifyBbsData = CreateBbsResponse()
            mViewModel.communityDetailModifyData.set(false)
        }
        mViewModel.bbsTempId = -1
        mViewModel.getCommunityInfo()
        mViewModel.communityInfoList.observe(this, Observer {
            setCategoryData()
        })
        setView(false)
        setClickEvent()

        val matrix = DisplayMetrics()
        this@CreateBbsActivity.windowManager.defaultDisplay.getMetrics(matrix)
        var height = matrix.heightPixels - CommonViewUtil.dipToPixel(this@CreateBbsActivity, 362)
        mBinding.edittextReportText.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height).apply {
            rightMargin = CommonViewUtil.dipToPixel(this@CreateBbsActivity, 20)
            leftMargin = CommonViewUtil.dipToPixel(this@CreateBbsActivity, 20)
            bottomMargin = CommonViewUtil.dipToPixel(this@CreateBbsActivity, 20)
        }

        /*KeyboardVisibilityEvent.setEventListener(this@CreateBbsActivity) {
            if(CustomLog.flag)CustomLog.L("KeyboardVisibilityEvent","setEventListener",it)
        }*/
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
                        /*mViewModel.selectedCategoryIndex = -1
                        mViewModel.selectedFilterIndex = -1
                        mViewModel.selectedFilterInit = true*/
                        mViewModel.modifyBbsData = value
                        mViewModel.bbsPhotos.value?.clear()
                        if(!value.imageList.isNullOrEmpty()){
                            mViewModel.bbsPhotos.value?.addAll(value.imageList)
                            setImageRecyclerViewVisible()
                        }
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
                    if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "init ", "communityCategoryId -----",value)
                    if(value.communityCategoryId.toLong() ==  mViewModel.modifyBbsData.categoryId){
                        mViewModel.onBbsCategorySelected(index)
                        if(!value.communityCategorySub.categoryFilterList.isNullOrEmpty() &&
                                (mViewModel.modifyBbsData.categoryFilterId != null && mViewModel.modifyBbsData.categoryFilterId!! > 0)){
                            for ((index2, filter) in value.communityCategorySub.categoryFilterList.iterator().withIndex()){
                                if(filter.id == mViewModel.modifyBbsData.categoryFilterId!!){
                                    mViewModel.selectedFilterInit = true
                                    mViewModel.onFilterSelect(index2)
                                    break@loop
                                }
                            }
                        }
                    }
                }
            }

            mViewModel.selectedCategoryIndex = initTabIndex
            val message = mViewModel.categoryList.get()!![initTabIndex]
            mViewModel.categoryTitle.set(message)
            if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "onBbsCategorySelected ", "position -----",initTabIndex, "categoryTitle",mViewModel.categoryTitle.get()!!)
            mViewModel.setFilterList()
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

        mBinding.setOnClickMenuTitle {
            if (!::mTitleFragment.isInitialized) {
                mTitleFragment = ListBottomSheetFragment(this).apply {
                    mTitle = "커뮤니티 선택"
                    mList = mViewModel.categoryList.get()!!
                    selectedIndex = mViewModel.selectedCategoryIndex
                    mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            mViewModel.selectedCategoryIndex = position
                            val message = mViewModel.categoryList.get()!![position]
                            mViewModel.categoryTitle.set(message)
                            if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "onBbsCategorySelected ", "position -----",position, "categoryTitle",mViewModel.categoryTitle.get()!!)
                            mViewModel.setFilterList()
                        }
                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }
                }
            }
            mTitleFragment.selectedIndex = mViewModel.selectedCategoryIndex
            mTitleFragment.show(supportFragmentManager, baseTag)
        }

        mBinding.setOnClickMenuSubTitle {
            if (!::mSubTitleFragment.isInitialized) {
                mSubTitleFragment = ListBottomSheetFragment(this).apply {
                    mTitle = "말머리 선택"
                    mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            mViewModel.selectedFilterIndex = position
                            val message = mViewModel.filterList.get()!![position]
                            mViewModel.filterTitle.set(message)
                        }
                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }

                }
            }
            mSubTitleFragment.mList = mViewModel.filterList.get()!!
            mSubTitleFragment.selectedIndex = mViewModel.selectedFilterIndex
            mSubTitleFragment.show(supportFragmentManager, baseTag)
        }

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
            if(mViewModel.filterListVisible.get() && mViewModel.selectedFilterIndex == -1){
                CustomMessageDialog(message = "말머리를 선택해주세요", cancelButtonVisible = false,
                        confirmTask = {
                        }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
                return@setOnClickWriteButton
            }
            if(TextUtils.isEmpty(mBinding.edittextReportTitle.text.toString())){
                CustomMessageDialog(message = "제목을 입력해주세요", cancelButtonVisible = false,
                        confirmTask = {
                        }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
                return@setOnClickWriteButton
            }

            if(TextUtils.isEmpty(mBinding.edittextReportText.text.toString())){
                CustomMessageDialog(message = "내용을 입력해주세요", cancelButtonVisible = false,
                        confirmTask = {
                        }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
                return@setOnClickWriteButton
            }
            if("IMAGE".equals(mViewModel.communityInfoList.value!![mViewModel.selectedCategoryIndex].communityCategorySub.type)) {
                if (mViewModel.bbsPhotos.value!!.size == 0) {
                    CustomMessageDialog(message = "해당게시판은 사진을 필수로 입력해주세요", cancelButtonVisible = false,
                            confirmTask = {
                            }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
                    return@setOnClickWriteButton
                }
            }

            var message = "등록하시겠습니까?"
            if(mViewModel.communityDetailModifyData.get()) message = "수정하시겠습니까?"

            CustomMessageDialog(message = message, cancelButtonVisible = true,
                    confirmTask = {
                        mLoadingIndicatorUtil.show()
                        postDetailData()
                    }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
        }

        mBinding.setOnClickTempList{
            this@CreateBbsActivity.startActivityForResult(Intent(this@CreateBbsActivity, TempBbsListActivity::class.java), Flag.RequestCode.COMMUNITY_DETAIL_TEMP_LIST)
        }

        mBinding.setOnClickTempSave{
            CustomMessageDialog(message = "임시 저장하시겠습니까?", cancelButtonVisible = true,
                    confirmTask = {
                        mLoadingIndicatorUtil.show()
                        if (CustomLog.flag) CustomLog.L("setOnClickTempSave", "init ", "mViewModel.bbsPhotos.value.isNullOrEmpty()")
                        if(mViewModel.bbsPhotos.value.isNullOrEmpty()){
                            if(mViewModel.bbsTempId > 0) modifyBbsTemp(setResponseTempData())
                            else createBbsTemp(setResponseTempData())
                        }else{
                            if (CustomLog.flag) CustomLog.L("setOnClickTempSave", "init ", "mViewModel.bbsPhotos.value.isNullOrEmpty() !!!!!!")
                            var response = setResponseTempData()
                            for ((index,file) in mViewModel.bbsPhotos.value!!.iterator().withIndex()){
                                if(file.fileSize == -1L && !"http".equals(file.url.substring(0,4), true)){
                                    mViewModel.imageUpload(file.url,index, object : OnCallBackListener{
                                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                                            if(resultFlag){
                                                response.imageList.add(value as ImageResponse)
                                                if(response.imageList.size == mViewModel.bbsPhotos.value!!.size){
                                                    if(mViewModel.bbsTempId > 0) modifyBbsTemp(response)
                                                    else createBbsTemp(response)
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
                                        if(mViewModel.bbsTempId > 0) modifyBbsTemp(response)
                                        else createBbsTemp(response)
                                    }
                                }
                            }
                        }
                    }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
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
                    deleteTemp()
                    showDialog(getFinishMsg(),true, Activity.RESULT_OK)
                }else{
                    showDialog("등록중 오류가 발생되었습니다.",false, null)
                }
            }
        })
    }


    private fun createBbsTemp(res : CreateBbsTempResponse){
        mViewModel.postTempBbs(res, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mLoadingIndicatorUtil.dismiss()
                if(resultFlag){
                    getTmpList()
                    showDialog("임시 저장이 완료되었습니다.",false, Activity.RESULT_OK)
                }else{
                    showDialog("등록중 오류가 발생되었습니다.",false, null)
                }
                if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "setOnClickTempList ----- resultFlag",resultFlag,"value",value)
            }
        })
    }


    private fun modifyBbsTemp(res : CreateBbsTempResponse){
        createBbsTemp(res)
        /*mViewModel.modifyBbsTemp(res, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag) CustomLog.L("modifyBbs callBackListener","resultFlag",resultFlag,"value",value)
                mLoadingIndicatorUtil.dismiss()
                if(resultFlag){
                    showDialog("임시 저장글이 수정되었습니다.",false, Activity.RESULT_OK)
                }else{
                    showDialog("등록중 오류가 발생되었습니다.",false, null)
                }
            }
        })*/
    }

    private fun modifyBbs(res : CreateBbsResponse){
        mViewModel.modifyBbs(res, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag) CustomLog.L("modifyBbs callBackListener","resultFlag",resultFlag,"value",value)
                mLoadingIndicatorUtil.dismiss()
                if(resultFlag){
                    deleteTemp()
                    showDialog(getFinishMsg(),true, Activity.RESULT_OK)
                }else{
                    showDialog("등록중 오류가 발생되었습니다.",false, null)
                }
            }
        })
    }

    private fun deleteTemp(){
        mViewModel.repository.deleteTempData(mViewModel.bbsTempId,null)
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
        data.imageUrl = ""
        data.categoryId = mViewModel.communityInfoList.value!![mViewModel.selectedCategoryIndex].communityCategoryId.toLong()
        if(CustomLog.flag)CustomLog.L("CreateBbsActivity","mViewModel.selectedCategoryIndex",mViewModel.selectedCategoryIndex)
        if(CustomLog.flag)CustomLog.L("CreateBbsActivity","mViewModel.selectedFilterIndex",mViewModel.selectedFilterIndex)
        if(mViewModel.selectedFilterIndex != -1){
            data.categoryFilterId = mViewModel.communityInfoList.value!![mViewModel.selectedCategoryIndex].communityCategorySub.categoryFilterList[mViewModel.selectedFilterIndex].id
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