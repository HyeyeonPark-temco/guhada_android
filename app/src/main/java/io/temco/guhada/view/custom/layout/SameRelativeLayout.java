package io.temco.guhada.view.custom.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SameRelativeLayout extends RelativeLayout {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SameRelativeLayout(Context context) {
        super(context);
    }

    public SameRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SameRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SameRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        getLayoutParams().width = widthSize;
        getLayoutParams().height = widthSize;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    ////////////////////////////////////////////////
}