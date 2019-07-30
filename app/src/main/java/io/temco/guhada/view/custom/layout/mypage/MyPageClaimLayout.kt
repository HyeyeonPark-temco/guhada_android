package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.viewmodel.mypage.MyPageClaimViewModel
import io.temco.guhada.databinding.CustomlayoutMypageClaimBinding
import io.temco.guhada.view.adapter.SpinnerAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 상품문의 화면
 *
 */
class MyPageClaimLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageClaimBinding, MyPageClaimViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_claim
    override fun init() {
        mViewModel = MyPageClaimViewModel(context)
        mBinding.viewModel = mViewModel
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bindClaimStatusSpinner")
        fun Spinner.bindClaimStatusSpinner(list: MutableList<String>) {
            if (list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = SpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, list)
                } else {
                    (this.adapter as SpinnerAdapter).setItems(list)
                }
                this.setSelection(0)
            }
        }
    }

    override fun onFocusView() {

    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }
}