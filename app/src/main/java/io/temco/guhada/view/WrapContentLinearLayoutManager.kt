package io.temco.guhada.view


import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * 수정된 RecyclerView LinearLayoutManager
 */
class WrapContentLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    )
    override fun supportsPredictiveItemAnimations(): Boolean {
        //return super.supportsPredictiveItemAnimations();
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            if (CustomLog.flag) CustomLog.E(e)
        } catch (e: NullPointerException) {
            if (CustomLog.flag) CustomLog.E(e)
        }

    }
}