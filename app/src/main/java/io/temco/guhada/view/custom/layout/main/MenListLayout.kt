package io.temco.guhada.view.custom.layout.main

import android.content.Context
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.WomenListViewModel
import io.temco.guhada.databinding.CustomlayoutMainMenlistBinding
import io.temco.guhada.view.custom.layout.common.BaseListLayout

class MenListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainMenlistBinding, WomenListViewModel>(context, attrs, defStyleAttr) {

    override fun getBaseTag() = MenListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_menlist
    override fun init() {

    }

    override fun onDestroy() {

    }
}