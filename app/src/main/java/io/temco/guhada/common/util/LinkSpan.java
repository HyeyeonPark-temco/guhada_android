package io.temco.guhada.common.util;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;

import io.temco.guhada.common.listener.OnLinkClickListener;

public class LinkSpan extends ClickableSpan {

    private OnLinkClickListener mClickListener;
    private float mTextSize;

    public LinkSpan(Context context, @DimenRes int size, OnLinkClickListener listener) {
        mClickListener = listener;
        if (context != null) {
            mTextSize = context.getResources().getDimension(size);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(true);
        ds.setTextSize(mTextSize);
    }

    @Override
    public void onClick(@NonNull View widget) {
        if (mClickListener != null) mClickListener.onClick();
    }
}
