package io.temco.guhada.view.custom.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import io.temco.guhada.R;

public class HeightRelativeLayout extends RelativeLayout {

    // -------- LOCAL VALUE --------
    private int mWidth = 0;
    private int mHeight = 0;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public HeightRelativeLayout(Context context) {
        super(context);
        initSize(context, null);
    }

    public HeightRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSize(context, attrs);
    }

    public HeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSize(context, attrs);
    }

    public HeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSize(context, attrs);
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = widthSize * mHeight / mWidth;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
        getLayoutParams().width = widthSize;
        getLayoutParams().height = heightSize;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void initSize(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) {
            mWidth = 0;
            mHeight = 0;
        } else {
            TypedArray t = context.obtainStyledAttributes(attributeSet, R.styleable.HeightRelativeLayout);
            mWidth = t.getDimensionPixelSize(R.styleable.HeightRelativeLayout_width, 0);
            mHeight = t.getDimensionPixelSize(R.styleable.HeightRelativeLayout_height, 0);
            t.recycle();
        }
    }

    ////////////////////////////////////////////////
}
