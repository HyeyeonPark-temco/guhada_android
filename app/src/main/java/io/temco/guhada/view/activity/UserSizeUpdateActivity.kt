package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.user.UserSize
import io.temco.guhada.data.viewmodel.UserSizeUpdateViewModel
import io.temco.guhada.view.activity.base.BindActivity


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


    override fun onCreate(savedInstanceState: Bundle?) {
        this.overridePendingTransition(R.anim.ease_in, R.anim.ease_out)
        super.onCreate(savedInstanceState)
    }

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

        mBinding.setOnClickCloseButton {
            this.overridePendingTransition(R.anim.ease_out, R.anim.ease_in)
            finish()
            this.overridePendingTransition(R.anim.ease_out, R.anim.ease_in)
        }


        // 드롭다운 스피너
        mBinding.spinnerUsersizeupdateSub1.setItems(mViewModel.userHeightTxtList)
        mBinding.spinnerUsersizeupdateSub1.setOnItemSelectedListener { view, position, id, item ->
            mViewModel.onShippingSub1Selected(position)
        }

        mBinding.spinnerUsersizeupdateSub2.setItems(mViewModel.userWeightTxtList)
        mBinding.spinnerUsersizeupdateSub2.setOnItemSelectedListener { view, position, id, item ->
            mViewModel.onShippingSub2Selected(position)
        }

        mBinding.spinnerUsersizeupdateSub3.setItems(mViewModel.userFootTxtList)
        mBinding.spinnerUsersizeupdateSub3.setOnItemSelectedListener { view, position, id, item ->
            mViewModel.onShippingSub3Selected(position)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Flag.RequestCode.POINT_RESULT_DIALOG && resultCode == Activity.RESULT_OK){
            this.overridePendingTransition(R.anim.ease_out, R.anim.ease_in)
            setResult(Activity.RESULT_OK)
            finish()
            this.overridePendingTransition(R.anim.ease_out, R.anim.ease_in)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

}