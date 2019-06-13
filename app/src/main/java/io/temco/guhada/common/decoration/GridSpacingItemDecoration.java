package io.temco.guhada.common.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpacingColumn;
    private int mSpacingRow;
    private boolean mIncludeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        setData(spanCount, spacing, spacing, includeEdge);
    }

    public GridSpacingItemDecoration(int spanCount, int spacingColumn, int spacingRow, boolean includeEdge) {
        setData(spanCount, spacingColumn, spacingRow, includeEdge);
    }

    private void setData(int spanCount, int spacingColumn, int spacingRow, boolean includeEdge) {
        mSpanCount = spanCount;
        mSpacingColumn = spacingColumn;
        mSpacingRow = spacingRow;
        mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % mSpanCount;
        if (mIncludeEdge) {
            outRect.left = mSpacingColumn - column * mSpacingColumn / mSpanCount;
            outRect.right = (column + 1) * mSpacingColumn / mSpanCount;
            if (position < mSpanCount) outRect.top = mSpacingRow;
            outRect.bottom = mSpacingRow;
        } else {
            outRect.left = column * mSpacingColumn / mSpanCount;
            outRect.right = mSpacingColumn - (column + 1) * mSpacingColumn / mSpanCount;
            if (position >= mSpanCount) outRect.top = mSpacingRow; // item top
        }
    }
}