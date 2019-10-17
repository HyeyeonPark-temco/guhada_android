package io.temco.guhada.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner


class CustomSpinner : AppCompatSpinner {

    private var mIsOpen = false
    lateinit var mListener: OnCustomSpinnerListener

    constructor(context: Context?) : super(context)
    constructor(context: Context?, mode: Int) : super(context, mode)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, mode: Int) : super(context, attrs, defStyleAttr, mode)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int, mode: Int) : super(context, attrs, defStyleAttr, defStyleRes, mode)

    override fun setSelection(position: Int, animate: Boolean) {
        val sameSelected = position === selectedItemPosition
        super.setSelection(position, animate)
        if (sameSelected) {
            onItemSelectedListener!!.onItemSelected(this, selectedView, position, selectedItemId)
        }
    }

    override fun setSelection(position: Int) {
        val sameSelected = position === selectedItemPosition
        super.setSelection(position)
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            onItemSelectedListener!!.onItemSelected(this, selectedView, position, selectedItemId)
        }
    }

    override fun performClick(): Boolean {
        mIsOpen = true
        if (::mListener.isInitialized)  mListener.onSpinnerOpened()

        return super.performClick()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (mIsOpen && hasWindowFocus)
            if (::mListener.isInitialized)  mListener.onSpinnerClosed()
    }

    interface OnCustomSpinnerListener {
        fun onSpinnerOpened()
        fun onSpinnerClosed()
    }
}