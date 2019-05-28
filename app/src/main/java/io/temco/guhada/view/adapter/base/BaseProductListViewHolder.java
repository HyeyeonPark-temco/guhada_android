package io.temco.guhada.view.adapter.base;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.RequestManager;

import io.temco.guhada.R;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.data.model.Deal;

public abstract class BaseProductListViewHolder<B extends ViewDataBinding> extends BaseViewHolder<B> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseProductListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void init(Context context, RequestManager manager, Deal data);

    ////////////////////////////////////////////////
    // PROTECTED
    ////////////////////////////////////////////////

    protected void addColor(Context context, ViewGroup parent, int unit, String[] colors) {
        if (colors != null && colors.length > 0) {
            int point = colors.length / unit;
            int re = colors.length % unit;

            String[] s;
            if (point > 0) {
                for (int i = 0; i < point; i++) {
                    s = new String[unit];
                    System.arraycopy(colors, unit * i, s, 0, s.length);
                    parent.addView(createColorLayout(context, s));
                }
            }
            if (re > 0) {
                s = new String[re];
                System.arraycopy(colors, unit * point, s, 0, s.length);
                parent.addView(createColorLayout(context, s));
            }
        }
    }

    protected void addText(Context context, String[] texts) {
        // Not..
//        if (texts != null && texts.length > 0) {
//            for (String t : texts) {
//            }
//        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private LinearLayout createColorLayout(Context context, String[] colors) {
        if (colors != null && colors.length > 0) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (String c : colors) {
                layout.addView(createColorView(context, c));
            }
            return layout;
        }
        return null;
    }

    private View createColorView(Context context, String color) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_color_dot, null);
        ImageUtil.setOvalView(context, v.findViewById(R.id.layout_color));
        v.findViewById(R.id.image_color).setBackgroundColor(Color.parseColor(color));
        return v;
    }

    ////////////////////////////////////////////////
}