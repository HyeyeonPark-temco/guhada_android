package io.temco.guhada.view.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.util.CustomLog;

public abstract class BaseFragment<B extends ViewDataBinding> extends Fragment {
    protected boolean isActivated = false;
    protected int scrollState = 0;

    private Tracker mTracker;
    private FirebaseAnalytics mFirebaseAnalytics;

    // -------- LOCAL VALUE --------
    protected B mBinding;
    protected ViewGroup mContainer;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            // Obtain the shared Tracker instance.
            BaseApplication application = (BaseApplication) getContext().getApplicationContext();
            mTracker = application.getDefaultTracker();

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        }catch (Exception e){
            if(CustomLog.getFlag())CustomLog.E(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mContainer = container;
        init();
        return mBinding.getRoot();
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    protected abstract String getBaseTag();

    protected abstract int getLayoutId();

    protected abstract void init();

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public boolean isActivated() {
        return isActivated;
    }

    public void onPageScrollStateChanged(int paramInt) {
        this.scrollState = paramInt;
    }

    public B getmBinding() {
        return mBinding;
    }

    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public Tracker getmTracker() {
        return mTracker;
    }
    ////////////////////////////////////////////////
}
