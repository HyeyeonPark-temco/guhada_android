package io.temco.guhada.view.activity

import android.content.Intent
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivityImagedetailBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 이미지 상세 화면
 */
class ImageDetailViewActivity : BindActivity<ActivityImagedetailBinding>() {

    override fun getBaseTag(): String = ImageGetActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_imagedetail
    override fun getViewType(): Type.View = Type.View.IMAGE_DETAIL

    override fun init() {

    }


}