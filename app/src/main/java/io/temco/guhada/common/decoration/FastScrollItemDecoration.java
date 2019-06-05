package io.temco.guhada.common.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.temco.guhada.common.listener.OnFastScrollListener;

public class FastScrollItemDecoration extends RecyclerView.ItemDecoration {

    // -------- LOCAL VALUE --------
    private OnFastScrollListener mListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FastScrollItemDecoration(@NonNull OnFastScrollListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        // Data
        String[] sections = mListener.getSections();
        String section = mListener.getCurrentSection();
        // Set View
        if (sections != null && sections.length > 0) {
            // All Letter
            Paint letterPaint = new Paint();
            letterPaint.setAntiAlias(true);
            // textPaint.setStyle(Paint.Style.FILL);
            float letterTotalHeight = 0;
            for (int i = 0; i < sections.length; i++) {
                boolean isSelected = mListener.getShowSection()
                        && !TextUtils.isEmpty(section)
                        && sections[i].toUpperCase().equals(section.toUpperCase());
                letterTotalHeight += createLetterText(parent, canvas, letterPaint,
                        sections[i].toUpperCase(), letterTotalHeight,
                        mListener.isAddPoint() && i != sections.length - 1, isSelected);
            }
            // Middle Letter
            if (mListener.getShowSection() & !TextUtils.isEmpty(section)) {
                float startX = parent.getWidth() - mListener.getSectionViewSize() - mListener.getSectionViewPadding();
                float startY = letterTotalHeight / 2 - mListener.getSectionViewSize() / 2 + mListener.getTopPadding();
                RectF f = new RectF(startX, startY,
                        startX + mListener.getSectionViewSize(), startY + mListener.getSectionViewSize());
                createSectionBackground(canvas, f);
                createSectionText(canvas, f, section);
            }
        } else {
            ;
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void createSectionBackground(Canvas canvas, RectF f) {
        Paint oval = new Paint();
        oval.setAntiAlias(true);
        oval.setColor(mListener.getColorSectionBackground());
        canvas.drawOval(f, oval);
    }

    private void createSectionText(Canvas canvas, RectF f, String text) {
        Paint ml = new Paint();
        ml.setAntiAlias(true);
        ml.setColor(mListener.getColorSectionText());
        ml.setTextSize(mListener.getSectionTextSize());
        // x
        float x = 0;
        switch (ml.getTextAlign()) {
            case LEFT:
                x = f.centerX() - ml.measureText(text) / 2f;
                break;

            case CENTER:
                x = f.centerX();
                break;

            case RIGHT:
                x = f.centerX() + ml.measureText(text) / 2f;
                break;
        }
        // y
        float baseline = Math.abs(ml.getFontMetrics().ascent) - Math.abs(ml.getFontMetrics().descent);
        float y = f.centerY() + baseline / 2f;
        // Draw
        canvas.drawText(text, x, y, ml);
    }

    private float createLetterText(View parent, Canvas canvas, Paint paint,
                                   String text, float totalHeight,
                                   boolean addPoint, boolean isSelected) {
        float x = parent.getWidth() - mListener.getLetterTextSize() - mListener.getTopPadding();
        paint.setTextAlign(Paint.Align.CENTER);
        // Letter
        paint.setColor(isSelected ? mListener.getColorLetterTextSelect() : mListener.getColorLetterTextNormal());
        paint.setTextSize(mListener.getLetterTextSize());
        canvas.drawText(text, x,
                totalHeight + mListener.getLetterTextSize() + mListener.getTopPadding(),
                paint);
        if (addPoint) {
            // Point
            paint.setColor(mListener.getColorLetterTextNormal());
            paint.setTextSize(mListener.getPointTextSize());
            canvas.drawText("•", x,
                    totalHeight + mListener.getLetterTextSize() + mListener.getPointTextSize() + mListener.getTopPadding(),
                    paint);
            return mListener.getLetterTextSize() + mListener.getPointTextSize();
        } else {
            return mListener.getLetterTextSize();
        }
    }

    ////////////////////////////////////////////////
}