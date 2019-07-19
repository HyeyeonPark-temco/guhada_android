package io.temco.guhada.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * @author park jungho
 * 19.07.18
 * 스크롤에 따른 화면이 접히는 효과의 RelativeLayout
 */
class CustomRelativeWrapper : RelativeLayout {

    private var mOffset: Int = 0
    private var mShouldClip: Boolean = false

    constructor(context: Context) : super(context) {
        mShouldClip = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mShouldClip = false
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mShouldClip = false
    }

    constructor(context: Context, shouldClick: Boolean) : super(context) {
        mShouldClip = shouldClick
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mShouldClip) {
            canvas.clipRect(Rect(left, top, right, bottom + mOffset))
        }
        super.dispatchDraw(canvas)
    }

    fun setClipY(offset: Int) {
        mOffset = offset
        invalidate()
    }
}
