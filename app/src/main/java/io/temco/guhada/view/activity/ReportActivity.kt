package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.community.CommunityDetail
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.viewmodel.ReportWriteViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonImageAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.ListBottomSheetFragment

/**
 * @author park jungho
 *
 * 신고하기 등록화면
 *
 * type : 0, productData - 상품 신고
 * type : 1, userData - 회원 신고
 * type : 2, communityData - 게시글/댓글 신고
 * type : 3, commentData - 게시글/댓글 신고
 *
 */
class ReportActivity : BindActivity<io.temco.guhada.databinding.ActivityReportBinding>(), View.OnClickListener , OnClickSelectItemListener {
    private lateinit var mViewModel : ReportWriteViewModel
    private lateinit var mLoadingIndicatorUtil : LoadingIndicatorUtil

    // ----------------------------------------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_report
    override fun getViewType(): Type.View = Type.View.REPORT_WRITE

    override fun init() {
        mViewModel = ReportWriteViewModel(this)
        mBinding.viewModel = mViewModel

        if(intent?.extras?.containsKey("type")!!){
            mViewModel.reportType = intent?.extras?.getInt("type")!!
            when(mViewModel.reportType){
                0->{
                    mViewModel.productData = intent?.extras?.getSerializable("data") as Product
                    mViewModel.reportTarget = ReportTarget.PRODUCT
                    mBinding.linearlayoutReportdetailType0.type = mViewModel.reportType
                    mBinding.linearlayoutReportdetailType0.text01 = mViewModel.productData?.productId.toString()
                    mBinding.linearlayoutReportdetailType0.text02 = mViewModel.productData?.name
                    mBinding.linearlayoutReportdetailType0.text03 = mViewModel.productData?.sellerName
                }
                1->{
                    mViewModel.reportUserId = (intent?.extras?.getSerializable("data") as ReportUserModel).reportUserId
                    mViewModel.reportTarget = ReportTarget.USER
                    mBinding.linearlayoutReportdetailType1.type = mViewModel.reportType
                }
                2->{
                    mViewModel.communityData = intent?.extras?.getSerializable("data") as CommunityDetail
                    mViewModel.reportTarget = ReportTarget.BOARD

                    mBinding.linearlayoutReportdetailType2.type = mViewModel.reportType
                    mBinding.linearlayoutReportdetailType2.text01 = mViewModel.communityData?.id.toString()
                    mBinding.linearlayoutReportdetailType2.text02 = mViewModel.communityData?.title
                    mBinding.linearlayoutReportdetailType2.text03 = mViewModel.communityData?.contents
                    mBinding.linearlayoutReportdetailType2.text04 = mViewModel.communityData?.createUserInfo?.nickname
                }
                3->{
                    mViewModel.commentData = intent?.extras?.getSerializable("data") as Comments
                    mViewModel.communityData = intent?.extras?.getSerializable("data2") as CommunityDetail
                    mViewModel.reportTarget = ReportTarget.COMMENT

                    mBinding.linearlayoutReportdetailType3.type = mViewModel.reportType
                    mBinding.linearlayoutReportdetailType3.text01 = mViewModel.communityData?.id.toString()
                    mBinding.linearlayoutReportdetailType3.text02 = mViewModel.communityData?.title
                    mBinding.linearlayoutReportdetailType3.text03 = mViewModel.commentData?.contents
                    mBinding.linearlayoutReportdetailType3.text04 = mViewModel.commentData?.createUserInfo?.nickname
                }
            }
            mBinding.title = resources.getStringArray(R.array.report_header_title)[mViewModel.reportType]
            mBinding.type = mViewModel.reportType
            mBinding.setOnClickCloseButton { finish() }
            mViewModel.setInit()
        }

        mViewModel.userId = CommonUtil.checkUserId()

        mViewModel.writeUserInfo.observe(this, Observer {
            if(CustomLog.flag)CustomLog.L("ReportActivity",it.toString())
            mBinding.email = it.email
            if(mViewModel.reportTarget == ReportTarget.USER){
                mBinding.linearlayoutReportdetailType1.text01 = mViewModel.userData?.nickname ?: ""
            }
        })

        mBinding.edittextReportText.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty() || s.isNullOrBlank()){
                    mViewModel.editTextReportTxtCount.set("0")
                }else{
                    if(s != null && s.isNotEmpty()){
                        mViewModel.editTextReportTxtCount.set(s!!.length.toString())
                    }
                }
            }
        })


        if (mBinding.recyclerviewReportwriteImagelist.adapter == null) {
            mBinding.recyclerviewReportwriteImagelist.adapter = CommonImageAdapter().apply { mList = mViewModel.reportPhotos.value!! }
            (mBinding.recyclerviewReportwriteImagelist.adapter as CommonImageAdapter).mClickSelectItemListener = this@ReportActivity
            mBinding.executePendingBindings()
        } else {
            (mBinding.recyclerviewReportwriteImagelist.adapter as CommonImageAdapter).setItems(mViewModel.reportPhotos.value!!)
        }

        mBinding.setOnClickGetImage {
            if(CustomLog.flag)CustomLog.L("ReviewWriteActivity","setOnClickGetImage",mViewModel.reportPhotos.value!!.size)
            if(mViewModel.reportPhotos.value!!.size < 10){
                mViewModel.selectedImageIndex = mViewModel.reportPhotos.value!!.size
                CommonUtil.startImageGallery(this)
            }else{
                ToastUtil.showMessage(resources.getString(R.string.review_activity_maximage_desc))
            }
        }

        mBinding.setOnClickWriteButton {
            sendReportData()
        }

        mBinding.setOnReportTypeSelected {
            val bottomSheet = ListBottomSheetFragment(mBinding.root.context).apply {
                this.mList = mViewModel.reportTypeMessages.get()!!
                this.mTitle = mBinding.root.context.getString(R.string.community_filter_title1)
                this.selectedIndex = mViewModel.selectReportTypeIndex
                this.mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                    override fun onItemClick(position: Int) {
                        if (mViewModel.selectReportTypeIndex != position) {
                            mViewModel.selectReportTypeIndex = position
                            val message = mViewModel.reportTypeMessages.get()!![position]
                            mViewModel.reportTypeMessage.set(message)
                        }
                    }

                    override fun onClickClose() {
                        this@apply.dismiss()
                    }
                }
            }
            bottomSheet.show(supportFragmentManager, baseTag)
        }
    }


    override fun onClick(v: View?) {

    }


    /**
     * type - 0 : delete, 1 : click
     */
    override fun clickSelectItemListener(type : Int, index: Int, value: Any) {
        when(type){
            0 -> {
                (mBinding.recyclerviewReportwriteImagelist.adapter as CommonImageAdapter).mList.removeAt(index)
                (mBinding.recyclerviewReportwriteImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                setImageRecyclerViewVisible()
            }
            1 -> {
                if(CustomLog.flag)CustomLog.L("ReviewWriteActivity","clickSelectItemListener","index",index,"value",value)
                mViewModel.selectedImageIndex = index
                CommonUtil.startImageGallery(this)
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
                        if(mViewModel.selectedImageIndex != -1 && mViewModel.reportPhotos.value!!.size > mViewModel.selectedImageIndex){
                            mViewModel.reportPhotos.value!!.removeAt(mViewModel.selectedImageIndex)
                            imageValue = fileNm
                            mViewModel.reportPhotos.value!!.add(mViewModel.selectedImageIndex,imageValue)
                            (mBinding.recyclerviewReportwriteImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                        }else{
                            imageValue = fileNm
                            mViewModel.reportPhotos.value!!.add(imageValue)
                            (mBinding.recyclerviewReportwriteImagelist.adapter as CommonImageAdapter).notifyDataSetChanged()
                        }
                        setImageRecyclerViewVisible()
                        if(CustomLog.flag)CustomLog.L("ReviewWriteActivity","fileNm",fileNm)
                    }else{
                        showDialog("외부 이미지 링크가 아닌 파일만 등록 가능합니다.",false)
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
            if(CustomLog.flag)CustomLog.E(e)
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

    private fun setImageRecyclerViewVisible(){
        if(mViewModel.reportPhotos.value!!.size > 0){
            mBinding.recyclerviewReportwriteImagelist.visibility = View.VISIBLE
        }else{
            mBinding.recyclerviewReportwriteImagelist.visibility = View.GONE
        }
    }

    private fun sendReportData(){
        if(mBinding.edittextReportTitle.text.isNullOrEmpty()) {
            showDialog("제목을 입력해 주세요.", false)
            return
        }
        if(mBinding.edittextReportText.text.isNullOrEmpty()) {
            showDialog("내용을 입력해 주세요.", false)
            return
        }
        if(!mViewModel.checkTermReport.get()) {
            showDialog(resources.getString(R.string.user_private_term_check), false)
            return
        }
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        var response = ReportResponse()
        response.content = mBinding.edittextReportText.text.toString()
        response.title = mBinding.edittextReportTitle.text.toString()
        when(mViewModel.reportType){
            0->response.targetId = mViewModel.productData!!.productId
            1->response.targetId = mViewModel.userData!!.userDetail.id
            2->response.targetId = mViewModel.communityData!!.id
            3->response.targetId = mViewModel.commentData!!.id
        }
        response.reporter = mViewModel.userId
        response.reportType = mViewModel.reportTypeList.value!![mViewModel.selectReportTypeIndex].name
        response.reportTarget = mViewModel.reportTarget.name

        mLoadingIndicatorUtil.show()
        if(mViewModel.reportPhotos.value.isNullOrEmpty()){
            if(CustomLog.flag)CustomLog.L("setOnClickWriteButton isNullOrEmpty","response", response)
            mViewModel.saveReport(response, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mLoadingIndicatorUtil.dismiss()
                    if(resultFlag)showDialog("신고하기를 완료하였습니다.", true)
                    else showDialog("신고하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요."+value.toString(), false)
                }
            })
        }else{
            for((index,file) in mViewModel.reportPhotos.value!!.iterator().withIndex()){
                if(CustomLog.flag)CustomLog.L("setOnClickWriteButton","file", file)
                mViewModel.imageUpload(file,index,object : OnCallBackListener{
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        if(CustomLog.flag)CustomLog.L("setOnClickWriteButton callBackListener","resultFlag", resultFlag, "value", value)
                        var data = value as ImageResponse
                        response.imageUrls.add(data.url)
                        if(response.imageUrls.size == mViewModel.reportPhotos.value!!.size){
                            if(CustomLog.flag)CustomLog.L("setOnClickWriteButton isNullOrEmpty not","response", response)
                            mViewModel.saveReport(response, object : OnCallBackListener{
                                override fun callBackListener(resultFlag: Boolean, value: Any) {
                                    mLoadingIndicatorUtil.dismiss()
                                    if(resultFlag)showDialog("신고하기를 완료하였습니다.", true)
                                    else showDialog("신고하기를 실패하였습니다.\n잠시 후 다시 시도해 주세요."+value.toString(), false)
                                }
                            })
                        }
                    }
                })
            }
        }
    }

    private fun showDialog(msg : String, isFinish : Boolean){
        CustomMessageDialog(message = msg, cancelButtonVisible = false,
                confirmTask = {
                    if(isFinish)finish()
                }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
    }

    ////////////////////////////////////////////////





}