package io.temco.guhada.common

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import io.temco.guhada.R
import io.temco.guhada.view.adapter.CommonSpinnerAdapter

class BindingKotlinAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("bindCreateBbsSpinner")
        fun Spinner.bindCreateBbsSpinner(list: MutableList<String>) {
            if (list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = CommonSpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_common_spinner, list)
                } else {
                    (this.adapter as CommonSpinnerAdapter).setItems(list)
                }
            }
        }


        @JvmStatic
        @BindingAdapter("bindCommonSpinner")
        fun Spinner.bindCommonSpinner(list: MutableList<String>) {
            if (list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = CommonSpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_common_spinner, list)
                } else {
                    (this.adapter as CommonSpinnerAdapter).setItems(list)
                }
            }
        }


    }

}