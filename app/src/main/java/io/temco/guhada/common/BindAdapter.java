package io.temco.guhada.common;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import io.temco.guhada.common.util.GlideApp;
import io.temco.guhada.common.util.ImageUtil;

public class BindAdapter {

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
//        ImageUtil.loadImage(Glide.with(view.getContext()), view, url);
        ImageUtil.loadImage(GlideApp.with(view.getContext()), view, url);
    }

    @BindingAdapter({"imageRes"})
    public static void setImage(ImageView view, @DrawableRes int value) {
        view.setImageResource(value);
    }

    @BindingAdapter("ovalImageUrl")
    public static void loadOvalImage(ImageView view, String url) {
        GlideApp.with(view.getContext()).load(url).thumbnail(0.9f).apply(RequestOptions.circleCropTransform()).into(view);
    }

    @BindingAdapter(value = {"roundCornerImageUrl", "roundCornerRadius"})
    public static void loadRoundCornerImage(ImageView view, String url, int radius) {
        if(url != null && !url.equals(""))
            GlideApp.with(view.getContext()).load(url).thumbnail(0.9f).apply(new RequestOptions().transform(new FitCenter(), new RoundedCorners(radius))).into(view);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("textBold")
    public static void isBold(TextView textView, boolean isBold) {
        if (isBold) {
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }
}