package io.temco.guhada.view.custom.layout.main

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.HomeListViewModel
import io.temco.guhada.data.viewmodel.WomenListViewModel
import io.temco.guhada.databinding.CustomlayoutMainWomenlistBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

class WomenListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainWomenlistBinding, WomenListViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = WomenListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_womenlist
    override fun init() {

    }


}