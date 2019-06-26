package io.temco.guhada.common.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.databinding.LayoutLoadingindicatorBinding

/**
 * Circle Loading Indicator
 * @author Hyeyeon Park
 */
class LoadingIndicatorUtil(val mContext: Context) : Dialog(mContext) {
    init {

        val binding = DataBindingUtil.inflate<LayoutLoadingindicatorBinding>(LayoutInflater.from(mContext), R.layout.layout_loadingindicator, null, false)
        binding.progressbarLoadingdialog.isIndeterminate = true
        binding.progressbarLoadingdialog.indeterminateDrawable.setColorFilter(context.resources.getColor(R.color.common_white), android.graphics.PorterDuff.Mode.MULTIPLY)

        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        setContentView(binding.root)
    }

    /**
     * Execute task() with Loading Indicator
     * You Should call dismiss() after calling execute(..) function
     * @param task function
     */
    fun execute(task: () -> Unit) = task()

    override fun hide() {
        if (this.isShowing) super.hide()
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}