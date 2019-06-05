package io.temco.guhada.common.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class SmoothScrollerWithLinearLayoutManager extends LinearLayoutManager {

    public SmoothScrollerWithLinearLayoutManager(Context context) {
        super(context, RecyclerView.VERTICAL, false);
    }

    public SmoothScrollerWithLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SmoothScrollerWithLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {

        TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SmoothScrollerWithLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}
