package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.mypage.MyPageFollowViewModel
import io.temco.guhada.databinding.CustomlayoutMypageFollowBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 팔로우한 스토어 화면
 *
 */
class MyPageFollowLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageFollowBinding, MyPageFollowViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_follow
    override fun init() {

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