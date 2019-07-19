package io.temco.guhada.view.custom.spinner

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
//import io.temco.guhada.databinding.ViewCustomspinnerBinding

open class CustomSpinnerView : ConstraintLayout {
    private lateinit var mViewModel: CustomSpinnerViewModel
    //private lateinit var mBinding: ViewCustomspinnerBinding

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context, attrs: AttributeSet?) {
        /*mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_customspinner, this, true)
        mViewModel = CustomSpinnerViewModel(object : OnCustomSpinnerListener{
            override fun closeSpinner() {
                mBinding.framelayoutCustomspinnerSelected.bringToFront()
                if(mViewModel.isDropdownShown){
                    mBinding.spinnerCustomspinner.clearFocus()
                }else {
                    mBinding.spinnerCustomspinner.performClick()
                }
            }
        })
        mBinding.framelayoutCustomspinnerSelected.bringToFront()*/



        // expand 상태에서 다른 view 선택 안됨
//        mBinding.framelayoutCustomspinnerSelected.setOnTouchListener { v, event ->
//            if(event.action == MotionEvent.ACTION_UP){
//                if(mViewModel.isDropdownShown){
//                    mBinding.spinnerCustomspinner.clearFocus()
//                }else {
//                    mBinding.spinnerCustomspinner.performClick()
//                }
//            }
//
//            return@setOnTouchListener true
//        }
        /*mBinding.viewModel = mViewModel
        mBinding.spinnerCustomspinner.adapter = CustomSpinnerAdapter(context, mViewModel, R.layout.item_payment_spinner, listOf())
        mBinding.executePendingBindings()*/
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["spinnerList", "emptyMessage"])
        fun bindShippingMemo(view: View?, list: MutableList<String>, emptyMessage: String) {
            if (view != null) {
               /* val spinnerView = (view as CustomSpinnerView).mBinding.spinnerCustomspinner
                list.add(emptyMessage)
                (spinnerView.adapter as CustomSpinnerAdapter).setItems(list)
                spinnerView.setSelection(list.size - 1)*/
            }
        }
    }

}




