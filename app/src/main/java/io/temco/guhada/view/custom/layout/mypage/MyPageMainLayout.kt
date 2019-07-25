package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageMainViewModel
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.databinding.CustomlayoutMypageMainBinding

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 메인 화면
 *
 */
class MyPageMainLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageMainBinding, MyPageMainViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_main
    override fun init() {

    }

    override fun onDestroy() {

    }
}