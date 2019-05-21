package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private int textViewResId;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.textViewResId = resource;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = (TextView) LayoutInflater.from(getContext()).inflate(textViewResId, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(getItem(position));
        if (position == 0) {
            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            layoutParams.height = 1;
            textView.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            textView.setLayoutParams(layoutParams);
        }
        return textView;
    }
}
