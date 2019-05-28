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
        // setMyScroller();
    }

    public SwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // setMyScroller();
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

    /*
    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyScroller extends Scroller {

        MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 1000); // 1s
        }
    }
    */

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