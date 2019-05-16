package io.temco.guhada.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.temco.guhada.R;

public class BorderEditTextView extends ConstraintLayout implements View.OnFocusChangeListener {
    private View mView;
    private EditText mEditText;

    public BorderEditTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public BorderEditTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BorderEditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attributeSet) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.view_borderedittext, this);
        mEditText = mView.findViewById(R.id.editText);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BorderEditTextView);
        mEditText.setHint(typedArray.getString(R.styleable.BorderEditTextView_hint));
        mEditText.setOnFocusChangeListener(this);
        typedArray.recycle();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        ConstraintLayout layout = mView.findViewById(R.id.linearlayout);
        if (hasFocus) {
            layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            layout.setBackgroundColor(getResources().getColor(R.color.pinkish_grey));
        }
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }
}
