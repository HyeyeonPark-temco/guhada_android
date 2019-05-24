package io.temco.guhada.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import androidx.databinding.ObservableField;

import com.bumptech.glide.Glide;

import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.view.custom.BorderEditTextView;

public class BindAdapter {

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        ImageUtil.loadImage(Glide.with(view.getContext()), view, url);
    }

    @BindingAdapter({"imageRes"})
    public static void setImage(ImageView view, @DrawableRes int value) {
        view.setImageResource(value);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    // NOT COMMIT
    @BindingAdapter("txtAttrChanged")
    public static void setListener(BorderEditTextView editTextView, InverseBindingListener listener) {
        if (listener != null) {
            editTextView.setTextWatcher(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.onChange();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    @InverseBindingAdapter(attribute = "app:txt")
    public static String getTxt(BorderEditTextView view) {
        return view.getText();
    }

    @BindingAdapter("app:txt")
    public static void setTxt(BorderEditTextView editTextView, String value) {
        editTextView.setText(value);
    }
}