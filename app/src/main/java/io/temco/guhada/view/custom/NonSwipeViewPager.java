package io.temco.guhada.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

/**
 *
 */
public class NonSwipeViewPager extends ViewPager {

    // -------- LOCAL VALUE --------
    private boolean mSwipeEnabled = false;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public NonSwipeViewPager(@NonNull Context context) {
        super(context);
        // setMyScroller();
    }

    public NonSwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // setMyScroller();
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mSwipeEnabled){
            return mSwipeEnabled && super.onTouchEvent(event);
        }else {
            return MotionEventCompat.getActionMasked(event) != MotionEvent.ACTION_MOVE && super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        return !mSwipeEnabled && super.onInterceptTouchEvent(event);
        if (mSwipeEnabled) {
            return super.onInterceptTouchEvent(event);
        } else {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
                // ignore move action
            } else {
                if (super.onInterceptTouchEvent(event)) {
                    super.onTouchEvent(event);
                }
            }
            return false;
        }
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

    public boolean getsetSwipeEnabled() {
        return mSwipeEnabled;
    }

    public void setSwipeEnabled(boolean locked) {
        mSwipeEnabled = locked;
    }

    ////////////////////////////////////////////////
}