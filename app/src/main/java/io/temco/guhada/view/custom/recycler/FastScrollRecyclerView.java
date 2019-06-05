package io.temco.guhada.view.custom.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.temco.guhada.common.listener.OnFastScrollListener;

public class FastScrollRecyclerView extends RecyclerView {

    // -------- LOCAL VALUE --------
    private Handler mHandler;
    private boolean mIsSetup = false;
    //
    private OnFastScrollListener mListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FastScrollRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void onDraw(Canvas c) {
        if (!mIsSetup) setupThings();
        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return checkPositionEvent(event);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setupThings() {
        if (getAdapter() instanceof OnFastScrollListener) {
            mListener = (OnFastScrollListener) getAdapter();
        }
        mIsSetup = true;
    }

    private boolean checkPositionEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        // Event
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isNotAction(x, y)) {
                    return super.onTouchEvent(event);
                } else {
                    setPositionEvent(y - mListener.getTopPadding());
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!mListener.getShowSection() && isNotAction(x, y)) {
                    return super.onTouchEvent(event);
                } else {
                    setPositionEvent(y - mListener.getTopPadding());
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mHandler = new FastScrollHandler();
                mHandler.sendEmptyMessageDelayed(0, 100);
                if (isNotAction(x, y)) {
                    return super.onTouchEvent(event);
                }
                break;
            }
        }
        return true;
    }

    private boolean isNotAction(float x, float y) {
        return x < getWidth() - mListener.getLetterTextSize() - mListener.getTopPadding() || x > getWidth() - mListener.getTopPadding()
                || y < mListener.getTopPadding() || y > getTotalTextSize();
    }

    private float getTotalTextSize() {
        if (mListener.isAddPoint()) {
            return (mListener.getLetterTextSize() + mListener.getPointTextSize()) * mListener.getSections().length + mListener.getTopPadding();
        } else {
            return mListener.getLetterTextSize() * mListener.getSections().length + mListener.getTopPadding();
        }
    }

    private void setPositionEvent(float currentY) {
        // Position
        int position;
        if (mListener.isAddPoint()) {
            position = (int) Math.floor(currentY / (mListener.getLetterTextSize() + mListener.getPointTextSize()));
        } else {
            position = (int) Math.floor(currentY / mListener.getLetterTextSize());
        }
        if (position < 0) position = 0;
        if (position >= mListener.getSections().length) {
            position = mListener.getSections().length - 1;
        }
        // Section
        String current = mListener.getSections()[position];
        mListener.setCurrentSection(current);
        mListener.setShowSection(true);
        // Scroll
        int positionInData = 0;
        if (mListener.getIndex().containsKey(current.toUpperCase())) {
            positionInData = mListener.getIndex().get(current.toUpperCase());
        }
        //
        int offset = position * mListener.getChildHeight();
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(positionInData, -offset);
        invalidate();
    }

    ////////////////////////////////////////////////
    // INNER CLASS
    ////////////////////////////////////////////////

    private class FastScrollHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListener.setShowSection(false);
            invalidate();
        }
    }

    ////////////////////////////////////////////////
}