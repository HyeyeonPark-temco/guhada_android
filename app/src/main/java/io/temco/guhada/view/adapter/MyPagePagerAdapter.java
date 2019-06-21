package io.temco.guhada.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.temco.guhada.view.fragment.mypage.CancelSwapReturnFragment;
import io.temco.guhada.view.fragment.mypage.OrderShipFragment;

public class MyPagePagerAdapter extends FragmentPagerAdapter {

    // -------- LOCAL VALUE --------
    private final String TAG = MyPagePagerAdapter.class.getSimpleName();
    //
    private OrderShipFragment mOrderShipFragment;
    private CancelSwapReturnFragment mCancelSwapReturnFragment;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public MyPagePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getCount() {
        return 1;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //
                if (mOrderShipFragment == null)
                    mOrderShipFragment = new OrderShipFragment();
                return mOrderShipFragment;

            case 1: //
                if (mCancelSwapReturnFragment == null)
                    mCancelSwapReturnFragment = new CancelSwapReturnFragment();
                return mCancelSwapReturnFragment;
        }
        return new Fragment();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}
