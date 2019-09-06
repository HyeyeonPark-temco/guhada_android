package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.community.CommunityTempInfo
import io.temco.guhada.data.viewmodel.community.TempBbsListViewModel
import io.temco.guhada.databinding.ActivityTempbbslistBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.TempBbsListAdapter

class TempBbsListActivity : BindActivity<ActivityTempbbslistBinding>(), OnClickSelectItemListener {

    private lateinit var mViewModel: TempBbsListViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun getBaseTag(): String = this@TempBbsListActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_tempbbslist
    override fun getViewType(): Type.View = Type.View.COMMUNITY_TEMP_LIST

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = TempBbsListViewModel(this)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }
        setViewInit()
    }


    private fun setViewInit() {
        mViewModel.getBbsTempListData(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mBinding.recyclerviewTembbslistList.adapter = TempBbsListAdapter().apply { mList = value as ArrayList<CommunityTempInfo> }
                (mBinding.recyclerviewTembbslistList.adapter as TempBbsListAdapter).mClickSelectItemListener = this@TempBbsListActivity
                (mBinding.recyclerviewTembbslistList.adapter as TempBbsListAdapter).notifyDataSetChanged()
            }
        })
    }

    override fun clickSelectItemListener(type: Int, index: Int, value: Any) {
        if(type == 0){
            var item = value as CommunityTempInfo
            mLoadingIndicatorUtil.show()
            mViewModel.getBbsTempData(item.id, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mLoadingIndicatorUtil.dismiss()
                    if(resultFlag){
                        var tmp = value as CommunityTempInfo
                        var intent = Intent()
                        intent.putExtra("tempData", tmp.getCreateBbsResponse())
                        intent.putExtra("tempDataId", tmp.id)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            })
        }else if(type == 1){
            var item = value as CommunityTempInfo
            mViewModel.deleteTempData(item.id, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(resultFlag){
                        (mBinding.recyclerviewTembbslistList.adapter as TempBbsListAdapter).mList.removeAt(index)
                        (mBinding.recyclerviewTembbslistList.adapter as TempBbsListAdapter).notifyDataSetChanged()
                    }
                }
            })
        }
    }


}