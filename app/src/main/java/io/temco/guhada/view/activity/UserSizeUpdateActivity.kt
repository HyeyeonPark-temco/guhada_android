package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
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
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.review.*
import io.temco.guhada.data.model.user.UserSize
import io.temco.guhada.data.viewmodel.ReviewWriteViewModel
import io.temco.guhada.data.viewmodel.UserSizeUpdateViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ReviewWriteImageAdapter
import java.util.*


/**
 * @author park jungho
 *
 * 유저 사이즈 등록
 *
 */
class UserSizeUpdateActivity : BindActivity<io.temco.guhada.databinding.ActivityUsersizeupdateBinding>() {
    private lateinit var mRequestManager: RequestManager
    private lateinit var loadingIndicatorUtil : LoadingIndicatorUtil
    private lateinit var mViewModel : UserSizeUpdateViewModel


    override fun getBaseTag(): String = ReviewWriteActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_usersizeupdate
    override fun getViewType(): Type.View = Type.View.USER_SIZE_UPDATE


    override fun init() {
        loadingIndicatorUtil = LoadingIndicatorUtil(this)
        mRequestManager = Glide.with(this)
        mViewModel = UserSizeUpdateViewModel(this)
        mBinding.viewModel = mViewModel

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels - CommonViewUtil.convertDpToPixel(40,this)
        val height = dm.heightPixels - CommonViewUtil.convertDpToPixel(60,this)
        mBinding.linearlayoutUsersizeupdateParent.layoutParams = FrameLayout.LayoutParams(width,height)

        if(intent != null && intent.extras != null && intent.extras.containsKey("userSize")){
            var userSize = intent.extras.getSerializable("userSize") as UserSize
            mViewModel.setUserSize(false, userSize)
        }

        mBinding.setOnClickCloseButton { finish() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Flag.RequestCode.POINT_RESULT_DIALOG && resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

}