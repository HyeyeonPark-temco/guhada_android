package io.temco.guhada.common.listener;

import android.view.View;

public interface OnStickyHeaderListener {

    boolean isHeader(int itemPosition);

    int getHeaderPositionForItem(int itemPosition);

    int getHeaderLayout(int headerPosition);

    void onBindHeaderData(View header, int headerPosition);
}