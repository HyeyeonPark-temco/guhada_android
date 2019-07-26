package io.temco.guhada.view.custom.layout.main

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.WomenListViewModel
import io.temco.guhada.databinding.CustomlayoutMainKidslistBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

class KidsListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainKidslistBinding,WomenListViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = KidsListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_kidslist
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