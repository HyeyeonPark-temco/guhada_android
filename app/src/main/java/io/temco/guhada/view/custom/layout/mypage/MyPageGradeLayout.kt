package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.mypage.MyPageGradeViewModel
import io.temco.guhada.databinding.CustomlayoutMypageGradeBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 회원등급 화면
 *
 */
class MyPageGradeLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageGradeBinding, MyPageGradeViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_grade
    override fun init() {

    }


    override fun onFocusView() {

    }

    override fun onStart() {
        if (CustomLog.flag) CustomLog.L("MyPageGradeLayout", "onStart ", "init -----")

    }

    override fun onResume() {
        if (CustomLog.flag) CustomLog.L("MyPageGradeLayout", "onResume ", "init -----")

    }

    override fun onPause() {
        if (CustomLog.flag) CustomLog.L("MyPageGradeLayout", "onPause ", "init -----")

    }

    override fun onStop() {
        if (CustomLog.flag) CustomLog.L("MyPageGradeLayout", "onStop ", "init -----")

    }

    override fun onDestroy() {
        if (CustomLog.flag) CustomLog.L("MyPageGradeLayout", "onDestroy ", "init -----")

    }
}