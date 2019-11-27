package io.temco.guhada.view.custom.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerView : RecyclerView{

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        var cy : Int = (velocityY * 0.55f).toInt()
        return super.fling(velocityX, cy)
    }
}