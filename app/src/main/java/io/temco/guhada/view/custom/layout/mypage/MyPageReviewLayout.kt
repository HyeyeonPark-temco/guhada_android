package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageReviewViewModel
import io.temco.guhada.databinding.CustomlayoutMypageReviewBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 상품리뷰 화면
 *
 */
class MyPageReviewLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageReviewBinding, MyPageReviewViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_recent
    override fun init() {

    }


}