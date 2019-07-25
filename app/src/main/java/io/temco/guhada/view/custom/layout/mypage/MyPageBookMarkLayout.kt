package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageBookMarkViewModel
import io.temco.guhada.databinding.CustomlayoutMypageBookmarkBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 찜한상품 화면
 *
 */
class MyPageBookMarkLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageBookmarkBinding, MyPageBookMarkViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_bookmark
    override fun init() {

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