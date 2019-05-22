package io.temco.guhada.view.custom.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class SwipeViewPager extends ViewPager {

    // -------- LOCAL VALUE --------
    private boolean mSwipeEnabled = false;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SwipeViewPager(@NonNull Context context) {
        super(context);
    }

    public SwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !mSwipeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !mSwipeEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return !mSwipeEnabled && super.canScrollHorizontally(direction);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public boolean getSwipeLocked() {
        return mSwipeEnabled;
    }

    public void setSwipeLocked(boolean locked) {
        mSwipeEnabled = locked;
    }

    ////////////////////////////////////////////////
}