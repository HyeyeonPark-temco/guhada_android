package io.temco.guhada.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.databinding.ActivityMainBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.MainPagerAdapter;
import io.temco.guhada.view.adapter.expand.SideMenuExpandFirstListAdapter;

public class MainActivity extends BindActivity<ActivityMainBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private final int REQUEST_CODE_CATEGORY = 11;
    private MainPagerAdapter mPagerAdapter;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.MAIN;
    }

    @Override
    protected void init() {
        if (mBinding != null) {
            setFullWideDrawerLayout();
            initMainPager();
            initSideMenu();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            ////////////////////////////////////////////////
            // Side Menu
            case R.id.layout_login:
                CommonUtil.debug(getBaseTag(), "layout_login");
                break;

            case R.id.image_home:
                CommonUtil.debug(getBaseTag(), "image_home");
                break;

            case R.id.image_setting:
                CommonUtil.debug(getBaseTag(), "image_setting");
                break;

            case R.id.image_close:
                changeDrawerLayout(false, false);
                break;

            case R.id.layout_brand:
                CommonUtil.debug(getBaseTag(), "layout_brand");
                break;

            // Sub Menu
//            case R.id.layout_sub_menu_community:
//                CommonUtil.debug(getBaseTag(), "layout_sub_menu_community");
//                break;

            ////////////////////////////////////////////////
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CATEGORY:
                    changeDrawerLayout(false, false);
                    if (data != null) {
                        Type.Category type = (Type.Category) data.getSerializableExtra(Info.INTENT_CATEGORY_TYPE);
                        int[] hierarchies = data.getIntArrayExtra(Info.INTENT_CATEGORY_HIERARCHIES);

                        CommonUtil.debug("" + Type.Category.get(type));
                        CommonUtil.debug("" + hierarchies.length);
                        mPagerAdapter.addProduct();
                    }
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setFullWideDrawerLayout() {
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mBinding.layoutSideMenu.getRoot().getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels;
        mBinding.layoutSideMenu.getRoot().setLayoutParams(params);
    }

    private void initMainPager() {
        // Adapter
        if (mPagerAdapter == null)
            mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setOnDrawerLayoutListener(isOpen -> changeDrawerLayout(isOpen));
        // Pager
        mBinding.layoutContents.layoutPager.setAdapter(mPagerAdapter);
        mBinding.layoutContents.layoutPager.setSwipeLocked(true);
        mBinding.layoutContents.layoutPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        // Tab
        addCustomTabs(3); // Home
        mBinding.layoutContents.layoutTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPagerAdapter.removeProduct();
                switch (tab.getPosition()) {
                    case 0: // Category
                        break;

                    case 1: // Brand
                        break;

                    case 2: // Home
                        mBinding.layoutContents.layoutPager.setCurrentItem(0);
                        break;

                    case 3: // Community
                        mBinding.layoutContents.layoutPager.setCurrentItem(1);
                        break;

                    case 4: // My Page
                        if (false) {
                            // if login
                        } else {
                            mBinding.layoutContents.layoutPager.setCurrentItem(2);
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mPagerAdapter.removeProduct();
            }
        });
    }

    private void addCustomTabs(int current) {
        // Add Custom Tab
        for (int i = 1; i <= 5; i++) {
            View v = getLayoutInflater().inflate(R.layout.layout_tab_main, null);
            switch (i) {
                case 1: // Category
                    v.findViewById(R.id.icon).setBackgroundResource(R.drawable.selector_tab_main_category);
                    ((TextView) v.findViewById(R.id.title)).setText(R.string.common_category);
                    break;

                case 2: // Brand
                    v.findViewById(R.id.icon).setBackgroundResource(R.drawable.selector_tab_main_brand);
                    ((TextView) v.findViewById(R.id.title)).setText(R.string.common_brand);
                    break;

                case 3: // Home
                    v.findViewById(R.id.icon).setBackgroundResource(R.drawable.selector_tab_main_home);
                    ((TextView) v.findViewById(R.id.title)).setText(R.string.common_home);
                    break;

                case 4: // Community
                    v.findViewById(R.id.icon).setBackgroundResource(R.drawable.selector_tab_main_community);
                    ((TextView) v.findViewById(R.id.title)).setText(R.string.common_community);
                    break;

                case 5: // My Page
                    v.findViewById(R.id.icon).setBackgroundResource(R.drawable.selector_tab_main_my_page);
                    ((TextView) v.findViewById(R.id.title)).setText(R.string.common_my_page);
                    break;
            }
            // Tab
            TabLayout.Tab tab = mBinding.layoutContents.layoutTab.newTab().setCustomView(v);
            mBinding.layoutContents.layoutTab.addTab(tab);
            if (i == current) {
                tab.select();
            }
        }
    }

    private void initSideMenu() {
        // Listener
        mBinding.layoutSideMenu.setClickListener(this);
        mBinding.layoutSideMenu.layoutHeader.setClickListener(this);

        // Category
        mBinding.layoutSideMenu.listContents.setLayoutManager(new LinearLayoutManager(this));
        SideMenuExpandFirstListAdapter adapter = new SideMenuExpandFirstListAdapter(this);
        adapter.setOnCategoryListener(mSideMenuCategoryListener);
        adapter.setItems(Preferences.getCategories());
        mBinding.layoutSideMenu.listContents.setAdapter(adapter);
    }

    private void changeDrawerLayout(boolean isOpen) {
        changeDrawerLayout(isOpen, true);
    }

    private void changeDrawerLayout(boolean isOpen, boolean animate) {
        if (isOpen &&
                !mBinding.layoutDrawer.isDrawerOpen(mBinding.layoutSideMenu.getRoot())) {
            mBinding.layoutDrawer.openDrawer(mBinding.layoutSideMenu.getRoot(), animate);
        } else {
            mBinding.layoutDrawer.closeDrawer(mBinding.layoutSideMenu.getRoot(), animate);
        }
    }

    private void startScreenByType(Type.Category type, int[] hierarchies) {
        switch (type) {
            case ALL:
                changeDrawerLayout(false, false);
                mPagerAdapter.addProduct();
                break;

            default:
                if (hierarchies != null && hierarchies.length >= 2) {
                    CategorySubActivity.startActivityForResult(this, hierarchies[1], REQUEST_CODE_CATEGORY);
                }
        }
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    private OnCategoryListener mSideMenuCategoryListener = this::startScreenByType;

    private OnCategoryListener mTabCategoryListener = (type, hierarchies) -> {

    };

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}