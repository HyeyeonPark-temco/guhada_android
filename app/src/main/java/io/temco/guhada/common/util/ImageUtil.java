package io.temco.guhada.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ImageUtil {

    private static RequestManager requestManager;

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    // Clip
    public static void setRoundCornerView(Context context, View view) {
//        setRoundCornerView(context, R.drawable.background_image_round, view);
    }

    public static void setRoundCornerView(Context context, View... views) {
//        setRoundCornerView(context, R.drawable.background_image_round, views);
    }

    public static void setRoundCornerView(Context context, int drawable, View view) {
//        setClipImage(context, view, ContextCompat.getDrawable(context, drawable));
    }

    public static void setRoundCornerView(Context context, int drawable, View... views) {
        if (views != null && views.length > 0) {
            Drawable d = ContextCompat.getDrawable(context, drawable);
            for (View v : views) {
                setClipImage(context, v, d);
            }
        }
    }

    public static void setOvalView(Context context, View view) {
        setClipImage(context, view, new ShapeDrawable(new OvalShape()));
    }

    public static void setOvalView(Context context, View... views) {
        if (views != null && views.length > 0) {

//            OvalShape ss = new OvalShape();
//            Outline line = new Outline();
//            ss.getOutline(line);

            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            for (View v : views) {
                setClipImage(context, v, drawable);
            }
        }
    }

    // Blur/Dim
    public static void loadDefaultDimImage(RequestManager manager, ImageView view, String url) {
        loadDimImage(manager, view, url);
    }

    public static void loadDefaultDimImage(RequestManager manager, ImageView view, int res) {
        localDimImage(manager, view, res);
    }

    public static void loadDefaultBlurDimImage(RequestManager manager, ImageView view, String url) {
        loadBlurDimImage(manager, view, url, 25); // MAX
    }

    public static void loadDefaultBlurDimImage(RequestManager manager, ImageView view, int res) {
        localBlurDimImage(manager, view, res, 25); // MAX
    }


    // Local
    public static void localImage(RequestManager manager, ImageView view, int res) {
        localGlideImage(manager, view, res, null);
    }

    public static void localImage(RequestManager manager, ImageView view, int res, int width, int height) {
        localGlideImage(manager, view, res, new RequestOptions().override(width, height));
    }

    public static void localImage(RequestManager manager, ImageView view, int res, int placeHolder) {
        localGlideImage(manager, view, res, new RequestOptions().placeholder(placeHolder));
    }

    public static void localImage(RequestManager manager, ImageView view, int res, Drawable placeHolder) {
        localGlideImage(manager, view, res, new RequestOptions().placeholder(placeHolder));
    }

    // Load Url
    public static void loadImage(RequestManager manager, ImageView view, String url) {
        loadGlideImage(manager, view, url, null);
    }

    // 메인(타임딜, 럭키드로우)에서 사용
    public static void loadImage(Context context, ImageView view, String url, @Nullable Integer widthPx, @Nullable Integer heightPx) {
        if (requestManager == null)
            requestManager = Glide.with((Activity) context);

        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fitCenter();

        if (widthPx != null && heightPx != null) {
            requestManager.load(url)
                    .downsample(DownsampleStrategy.AT_MOST)
                    .apply(options)
                    .thumbnail(0.2f)

                    .into(view);
        } else {
            requestManager.load(url)
                    .downsample(DownsampleStrategy.AT_MOST)
                    .apply(options)
                    .thumbnail(0.2f)
                    .into(view);
        }
//        loadGlideImage(requestManager, view, url, null);
    }


    public static void loadImage(RequestManager manager, ImageView view, String url, int width, int height) {
        loadGlideImage(manager, view, url, new RequestOptions().override(width, height));
    }

    public static void loadImage(RequestManager manager, ImageView view, String url, int placeHolder) {
        loadGlideImage(manager, view, url, new RequestOptions().placeholder(placeHolder));
    }

    public static void loadImage(RequestManager manager, ImageView view, String url, Drawable placeHolder) {
        loadGlideImage(manager, view, url, new RequestOptions().placeholder(placeHolder));
    }

    public static void loadImage(RequestManager manager, ImageView view, String url, RequestOptions options) {
        loadGlideImage(manager, view, url, options);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private static void setClipImage(Context context, View view, Drawable drawable) {
        view.setBackground(drawable);
        view.setClipToOutline(true);
    }

    private static void loadDimImage(RequestManager manager, ImageView view, String url) {
//        loadGlideImage(manager, view, url, bitmapTransform(new ColorFilterTransformation(R.color.background_dim_1)));
    }

    private static void localDimImage(RequestManager manager, ImageView view, int res) {
//        localGlideImage(manager, view, res, bitmapTransform(new ColorFilterTransformation(R.color.background_dim_1)));
    }

    private static void loadBlurDimImage(RequestManager manager, ImageView view, String url, int radius) {
//        loadGlideImage(manager, view, url, bitmapTransform(
//                new MultiTransformation<>(
//                        new BlurTransformation(radius),
//                        new ColorFilterTransformation(R.color.background_dim_1))));
    }

    private static void localBlurDimImage(RequestManager manager, ImageView view, int res, int radius) {
//        localGlideImage(manager, view, res, bitmapTransform(
//                new MultiTransformation<>(
//                        new BlurTransformation(radius),
//                        new ColorFilterTransformation(R.color.background_dim_1))));
    }

    private static void loadGlideImage(RequestManager manager, ImageView view, String url, RequestOptions options) {
        if (manager != null) {
            if (options == null) {
                options = new RequestOptions();
            }
            // options.diskCacheStrategy(DiskCacheStrategy.NONE); // Disk Cache
            // options.skipMemoryCache(true) // Memory Cache
            manager.load(url)
                    .apply(RequestOptions.fitCenterTransform())
                    .apply(options)
                    .thumbnail(0.1f)
                    .into(view);
        }
    }

    private static void localGlideImage(RequestManager manager, ImageView view, int res, RequestOptions options) {
        if (manager != null) {
            if (options == null) {
                options = new RequestOptions();
            }
            manager.load(res)
                    .apply(options)
                    .into(view);
        }
    }

    ////////////////////////////////////////////////

    // 메인(타임딜, 럭키드로우)에서 사용
    public static void clearGlide(Context context, ImageView view) {
        if (requestManager == null)
            requestManager = Glide.with(context);
        requestManager.clear(view);
    }
}