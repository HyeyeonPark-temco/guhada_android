package io.temco.guhada.view.activity

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import io.temco.guhada.R
import io.temco.guhada.common.enum.SaveActionType
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.CouponDownloadDialogViewModel
import io.temco.guhada.databinding.ActivityCoupondownloaddialogBinding
import io.temco.guhada.view.adapter.CouponDownloadListAdapter

/**
 * 상품상세-쿠폰 다운로드 팝업
 * @author Hyeyeon Park
 */
class CouponDownloadDialogActivity : AppCompatActivity() {
    private lateinit var mViewModel: CouponDownloadDialogViewModel
    lateinit var mBinding: ActivityCoupondownloaddialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_coupondownloaddialog)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mViewModel = CouponDownloadDialogViewModel().apply { mOnClickCloseTask = { finish() } }
        intent.getParcelableArrayListExtra<Coupon>("couponList").let {
            if (it != null) {
                mViewModel.mList = it as MutableList<Coupon>
                mBinding.recyclerviewCoupondownload.adapter = CouponDownloadListAdapter().apply { this.mList = it }
                setTitleAndButtonText(it)
            } else {
                ToastUtil.showMessage(resources.getString(R.string.common_message_error))
                finish()
            }
        }
        mBinding.viewModel = mViewModel
    }

    /**
     * FOLLOW 쿠폰 여부에 따른 title, button text
     * @author Hyeyeon Park
     * @since 2019.09.10
     */
    private fun setTitleAndButtonText(list: MutableList<Coupon>) {
        var isFollowExist = false
        for (coupon in list) {
            if (coupon.saveTargetType == SaveActionType.FOLLOW.type) {
                isFollowExist = true
                break
            }
        }

        if (isFollowExist) {
            mBinding.textviewCoupondownloadFollowtitle.text = resources.getString(R.string.coupondownload_title_follow)
            mBinding.buttonCoupondownloadDownload.text =
                    if (list.size > 1) resources.getString(R.string.coupondownload_button_follow_all)
                    else resources.getString(R.string.coupondownload_button_follow)
        } else {
            mBinding.textviewCoupondownloadFollowtitle.text = resources.getString(R.string.coupondownload_title_unfollow)
            mBinding.buttonCoupondownloadDownload.text =
                    if (list.size > 1) resources.getString(R.string.coupondownload_button_unfollow_all)
                    else resources.getString(R.string.coupondownload_button_unfollow)
        }
    }
}