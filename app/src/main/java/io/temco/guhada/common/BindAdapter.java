package io.temco.guhada.common;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import io.temco.guhada.R;
import io.temco.guhada.common.util.GlideApp;
import io.temco.guhada.common.util.ImageUtil;

public class BindAdapter {

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
//        ImageUtil.loadImage(Glide.with(view.getContext()), view, url);
        if (url != null && url != "")
            ImageUtil.loadImage(GlideApp.with(view.getContext()), view, url);
    }

    @BindingAdapter({"imageRes"})
    public static void setImage(ImageView view, Drawable value) {
        view.setImageDrawable(value);
    }

    @BindingAdapter("ovalImageUrl")
    public static void loadOvalImage(ImageView view, String url) {
        if (url != null && url != "")
            GlideApp.with(view.getContext()).load(url).apply(RequestOptions.circleCropTransform()).into(view);
        else
            GlideApp.with(view.getContext()).load(R.drawable.background_color_dot_pinkishgrey).apply(RequestOptions.circleCropTransform()).into(view);
    }

    @BindingAdapter("ovalProfileImageUrl")
    public static void loadOvalProfileImage(ImageView view, String url) {
        if (url != null && url != "")
            GlideApp.with(view.getContext()).load(url).apply(RequestOptions.circleCropTransform()).into(view);
        else
            GlideApp.with(view.getContext()).load(R.drawable.profile_non_square).apply(RequestOptions.circleCropTransform()).into(view);
    }

    @BindingAdapter(value = {"roundCornerImageUrl", "roundCornerRadius"})
    public static void loadRoundCornerImage(ImageView view, String url, int radius) {
        if (url != null && !url.equals(""))
            GlideApp.with(view.getContext()).load(url).apply(new RequestOptions().transform(new FitCenter(), new RoundedCorners(radius))).into(view);
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

    @BindingAdapter("underLine")
    public static void setUnderLine(TextView textView, boolean value) {
        if (value) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}