package io.temco.guhada.common.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EqualSpacingItemDecoration extends RecyclerView.ItemDecoration {

    // -------- LOCAL VALUE --------
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private final int GRID = 2;
    private int mSpacing;
    private int mDisplayMode;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public EqualSpacingItemDecoration(int spacing) {
        setSpacing(spacing);
    }

    public EqualSpacingItemDecoration(int spacing, int displayMode) {
        setSpacing(spacing, displayMode);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setSpacing(int mSpacing) {
        setSpacing(mSpacing, -1);
    }

    private void setSpacing(int spacing, int displayMode) {
        mSpacing = spacing;
        mDisplayMode = displayMode;
    }

    private void setSpacingForDirection(Rect outRect,
                                        RecyclerView.LayoutManager layoutManager,
                                        int position,
                                        int itemCount) {
        // Resolve display mode automatically
        if (mDisplayMode == -1) {
            mDisplayMode = resolveDisplayMode(layoutManager);
        }

        switch (mDisplayMode) {
            case HORIZONTAL:
                outRect.left = mSpacing;
                outRect.right = position == itemCount - 1 ? mSpacing : 0;
                outRect.top = mSpacing;
                outRect.bottom = mSpacing;
                break;
            case VERTICAL:
                outRect.left = mSpacing;
                outRect.right = mSpacing;
                outRect.top = mSpacing;
                outRect.bottom = position == itemCount - 1 ? mSpacing : 0;
                break;
            case GRID:
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    int cols = gridLayoutManager.getSpanCount();
                    int rows = itemCount / cols;

                    outRect.left = mSpacing;
                    outRect.right = position % cols == cols - 1 ? mSpacing : 0;
                    outRect.top = mSpacing;
                    outRect.bottom = position / cols == rows - 1 ? mSpacing : 0;
                }
                break;
        }
    }

    private int resolveDisplayMode(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) return GRID;
        if (layoutManager.canScrollHorizontally()) return HORIZONTAL;
        return VERTICAL;
    }

    ////////////////////////////////////////////////
}