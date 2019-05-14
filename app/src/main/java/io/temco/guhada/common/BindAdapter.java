package io.temco.guhada.common;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;
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
}