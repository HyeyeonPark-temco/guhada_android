package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatSpinner
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.user.UserSize
import io.temco.guhada.data.viewmodel.UserSizeUpdateViewModel
import io.temco.guhada.view.CustomSpinner
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


        // 스피너 드롭다운 Max Height 5개 높이로 설정
        val popup1 = AppCompatSpinner::class.java.getDeclaredField("mPopup")
        popup1.isAccessible = true
        val popupWindow1= popup1.get(mBinding.spinnerUsersizeupdateSub1) as androidx.appcompat.widget.ListPopupWindow
        popupWindow1.height = CommonViewUtil.convertDpToPixel(230, mBinding.root.context)
        mBinding.spinnerUsersizeupdateSub1.mListener = object : CustomSpinner.OnCustomSpinnerListener{
            override fun onSpinnerOpened() {
                mViewModel.userSizeSubMenuImgArrow.set(1)
            }
            override fun onSpinnerClosed() {
                mViewModel.userSizeSubMenuImgArrow.set(-1)
            }
        }

        val popup2 = AppCompatSpinner::class.java.getDeclaredField("mPopup")
        popup2.isAccessible = true
        val popupWindow2= popup2.get(mBinding.spinnerUsersizeupdateSub2) as androidx.appcompat.widget.ListPopupWindow
        popupWindow2.height = CommonViewUtil.convertDpToPixel(230, mBinding.root.context)
        mBinding.spinnerUsersizeupdateSub2.mListener = object : CustomSpinner.OnCustomSpinnerListener{
            override fun onSpinnerOpened() {
                mViewModel.userSizeSubMenuImgArrow.set(2)
            }
            override fun onSpinnerClosed() {
                mViewModel.userSizeSubMenuImgArrow.set(-1)
            }
        }

        val popup3 = AppCompatSpinner::class.java.getDeclaredField("mPopup")
        popup3.isAccessible = true
        val popupWindow3= popup3.get(mBinding.spinnerUsersizeupdateSub3) as androidx.appcompat.widget.ListPopupWindow
        popupWindow3.height = CommonViewUtil.convertDpToPixel(230, mBinding.root.context)
        mBinding.spinnerUsersizeupdateSub3.mListener = object : CustomSpinner.OnCustomSpinnerListener{
            override fun onSpinnerOpened() {
                mViewModel.userSizeSubMenuImgArrow.set(3)
            }
            override fun onSpinnerClosed() {
                mViewModel.userSizeSubMenuImgArrow.set(-1)
            }
        }
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