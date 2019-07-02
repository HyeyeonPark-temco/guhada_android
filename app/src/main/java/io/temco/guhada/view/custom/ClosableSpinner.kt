package io.temco.guhada.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner

class ClosableSpinner : Spinner {
    constructor(context: Context) : super(context)
    constructor(context: Context, mode: Int) : super(context, mode)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int, mode: Int) : super(context, attrs, defStyle, mode)

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}