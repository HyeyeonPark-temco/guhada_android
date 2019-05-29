package io.temco.guhada.common;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import io.temco.guhada.common.util.ImageUtil;

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

    @BindingAdapter("textBold")
    public static void isBold(TextView textView, boolean isBold){
        if (isBold){
            textView.setTypeface(null, Typeface.BOLD);
        }else {
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }
}