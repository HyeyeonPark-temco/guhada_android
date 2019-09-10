package io.temco.guhada.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnBorderEditTextFocusListener;
import io.temco.guhada.databinding.ViewBorderedittextBinding;

public class BorderEditTextView extends ConstraintLayout implements View.OnFocusChangeListener {
    private ViewBorderedittextBinding binding;
    private OnBorderEditTextFocusListener onBorderEditTextFocusListener;

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

    public ViewBorderedittextBinding getBinding() {
        return binding;
    }

    public void setBinding(ViewBorderedittextBinding binding) {
        this.binding = binding;
    }

    private void initView(Context context, @Nullable AttributeSet attributeSet) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_borderedittext, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BorderEditTextView);
        String text = typedArray.getString(R.styleable.BorderEditTextView_txt);
        String inputType = typedArray.getString(R.styleable.BorderEditTextView_type);
        int max = typedArray.getInteger(R.styleable.BorderEditTextView_max, 0);
        boolean readonly = typedArray.getBoolean(R.styleable.BorderEditTextView_readonly, false);
        inputType = inputType != null ? inputType : "text";

        if (max > 0) {
            InputFilter[] inputFilters = new InputFilter[1];
            inputFilters[0] = new InputFilter.LengthFilter(max);
            binding.editText.setFilters(inputFilters);
        }

        binding.editText.setEnabled(!readonly);
        binding.editText.setText(text);
        binding.editText.setHint(typedArray.getString(R.styleable.BorderEditTextView_hint));
        binding.editText.setOnFocusChangeListener(this);
        binding.executePendingBindings();

        switch (inputType) {
            case "password":
                binding.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                return;
            case "text":
                binding.editText.setInputType(InputType.TYPE_CLASS_TEXT);
                return;
            case "number":
                binding.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                return;
            case "email":
                binding.editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                return;
        }
        typedArray.recycle();

        binding.executePendingBindings();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            binding.constraintlayoutBorderedittextContaiiner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            binding.constraintlayoutBorderedittextContaiiner.setBackgroundColor(getResources().getColor(R.color.pinkish_grey));
        }
        if(onBorderEditTextFocusListener!=null)onBorderEditTextFocusListener.onFocusChange(v, hasFocus);
        binding.executePendingBindings();
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        binding.editText.addTextChangedListener(textWatcher);
    }

    public void setText(String text) {
        binding.editText.setText(text);
        binding.executePendingBindings();
    }

    public String getText() {
        return binding.editText.getText().toString();
    }

    @BindingAdapter("txt")
    public static void setEditTextContent(BorderEditTextView view, @Nullable String text) {
        String old = view.getText();
        if (!old.equals(text)) {
            view.binding.editText.setText(text);
        }
    }

    @BindingAdapter("txtAttrChanged")
    public static void setInverseBindingListener(BorderEditTextView view, @Nullable InverseBindingListener listener) {
        view.binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.onChange();
                }
            }
        });
    }

    @InverseBindingAdapter(attribute = "txt", event = "txtAttrChanged")
    public static String getContent(BorderEditTextView view) {
        return view.binding.editText.getText().toString();
    }

    public void setOnBorderEditTextFocusListener(OnBorderEditTextFocusListener onBorderEditTextFocusListener) {
        this.onBorderEditTextFocusListener = onBorderEditTextFocusListener;
    }
}
