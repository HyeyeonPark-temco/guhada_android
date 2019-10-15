package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.PhotoPagerViewModel
import io.temco.guhada.databinding.ActivityPhotopagerBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ImagePagerAdapter

/**
 * 사진 확대 pager activity
 * @author Hyeyeon Park
 * @since 2019.10.15
 */
class PhotoPagerActivity : BindActivity<ActivityPhotopagerBinding>() {
    lateinit var mViewModel: PhotoPagerViewModel
    override fun getBaseTag(): String = PhotoPagerActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_photopager
    override fun getViewType(): Type.View = Type.View.PHOTO_PAGER

    override fun init() {
        mViewModel = PhotoPagerViewModel().apply {
            this.mUrlList = intent.getStringArrayListExtra("urlList")
        }
        mBinding.viewpagerPhotopager.adapter = ImagePagerAdapter().apply {
            this.list = mViewModel.mUrlList
        }

    }
}