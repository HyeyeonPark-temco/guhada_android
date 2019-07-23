package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPagePointViewModel
import io.temco.guhada.databinding.CustomlayoutMypagePointBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 포인트 화면
 *
 */
class MyPagePointLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypagePointBinding, MyPagePointViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_point
    override fun init() {

    }


}