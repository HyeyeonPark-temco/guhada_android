package io.temco.guhada.view.custom.dialog.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

import io.temco.guhada.R;

public abstract class BaseDialog<B extends ViewDataBinding> extends DialogFragment {

    // -------- LOCAL VALUE --------
    public B mBinding;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        init();
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (getDialog() != null) {
//            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
//        }
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    protected abstract int getLayoutId();

    protected abstract void init();

    ////////////////////////////////////////////////
}