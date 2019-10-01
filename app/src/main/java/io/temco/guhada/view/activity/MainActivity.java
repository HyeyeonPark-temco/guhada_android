package io.temco.guhada.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.EventBusData;
import io.temco.guhada.common.EventBusHelper;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.common.util.LoadingIndicatorUtil;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.db.GuhadaDB;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.UserShipping;
import io.temco.guhada.data.model.claim.Claim;
import io.temco.guhada.data.model.user.UserSize;
import io.temco.guhada.databinding.ActivityMainBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.MainPagerAdapter;
import io.temco.guhada.view.custom.dialog.BrandListDialog;
import io.temco.guhada.view.custom.dialog.CategoryListDialog;

/**
 * @author park jungho
 * 19.08.05
 * 사이드 메뉴,상품상세 화면 걷어 냄
 */
public class MainActivity extends BindActivity<ActivityMainBinding> {

    // -------- LOCAL VALUE --------
    private final int REQUEST_CODE_LOGIN = 101;
    private final int REQUEST_CODE_CATEGORY = 201;
    private final int REQUEST_CODE_BRAND = 202;

    private Disposable disposable = null;
    //

    private MainPagerAdapter mPagerAdapter;
    private CategoryListDialog mCategoryListDialog;
    private BrandListDialog mBrandListDialog;
    private LoadingIndicatorUtil mLoadingIndicatorUtil;
    private LinearLayout layout_maintab_layout[] = null;
    private ImageView imageview_maintab_con[] = null;
    private TextView textview_maintab_title[] = null;

    private CompositeDisposable mDisposable = null;
    private GuhadaDB mDb;

    private int currentViewPagerIndex = 2;
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
        mDisposable = new CompositeDisposable();
        mDb = GuhadaDB.Companion.getInstance(this);
        mLoadingIndicatorUtil = new LoadingIndicatorUtil(this);
        CommonUtil.getUserIp();
        initMainPager();
        setEventBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * @author park jungho
         *
         * 메인으로 이동해서 선택될 탭 화면
         */
        if (((BaseApplication) getApplicationContext()).getMoveToMain() != null && ((BaseApplication) getApplicationContext()).getMoveToMain().isMoveToMain()) {
            if (((BaseApplication) getApplicationContext()).getMoveToMain().getResultCode() != -1){
                if (((BaseApplication) getApplicationContext()).getMoveToMain().getResultCode() == Flag.ResultCode.GO_TO_MAIN) {

                } else if (((BaseApplication) getApplicationContext()).getMoveToMain().getResultCode() == Flag.ResultCode.GO_TO_MAIN_HOME) {
                    mBinding.layoutContents.layoutPager.setCurrentItem(2);
                    selectTab(2, false);
                    if(((BaseApplication) getApplicationContext()).getMoveToMain().isInitMain()){
                        EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.HOME_MOVE,0));
                    }
                } else if (((BaseApplication) getApplicationContext()).getMoveToMain().getResultCode() == Flag.ResultCode.GO_TO_MAIN_COMUNITY) {
                    mBinding.layoutContents.layoutPager.setCurrentItem(3);
                    selectTab(3, false);
                } else if (((BaseApplication) getApplicationContext()).getMoveToMain().getResultCode() == Flag.ResultCode.GO_TO_MAIN_MYPAGE) {
                    mBinding.layoutContents.layoutPager.setCurrentItem(4);
                    selectTab(4, false);
                    if (((BaseApplication) getApplicationContext()).getMoveToMain().getResultPageIndex() != -1){
                        EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_MOVE,
                                ((BaseApplication) getApplicationContext()).getMoveToMain().getResultPageIndex()));
                    }
                }
            }
            ((BaseApplication) getApplicationContext()).getMoveToMain().clear();
            ((BaseApplication) getApplicationContext()).setMoveToMain(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("MainActivity", "onActivityResult", "requestCode",requestCode);
        String msg = "";
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Flag.RequestCode.DELIVERY:
                case Flag.RequestCode.CONFIRM_PURCHASE:
                case Flag.RequestCode.CANCEL_ORDER:
                    EventBusData eventBusData = new EventBusData(requestCode, null);
                    EventBusHelper.sendEvent(eventBusData);
                    break;

                case REQUEST_CODE_LOGIN:
                    //changeLoginStatus(checkToken());
                    break;

                case REQUEST_CODE_CATEGORY:
                    if (data != null) {
                        mPagerAdapter.removeAll();
                        Type.Category type = (Type.Category) data.getSerializableExtra(Info.INTENT_CATEGORY_TYPE);
                        int[] hierarchies = data.getIntArrayExtra(Info.INTENT_CATEGORY_HIERARCHIES);
                        CommonUtil.startCategoryScreen(this, type, hierarchies, false);
                    }
                    break;

                case REQUEST_CODE_BRAND:
                    if (data != null) {
                        /*mPagerAdapter.removeAll();
                        Brand b = (Brand) data.getSerializableExtra(Info.INTENT_BRAND_DATA);
                        mPagerAdapter.setProductBrandData(b);*/
                        Brand b = (Brand) data.getSerializableExtra(Info.INTENT_BRAND_DATA);
                        CommonUtil.startBrandScreen(this, b, true);
                    }
                    break;

                case Flag.RequestCode.EDIT_SHIPPING_ADDRESS:
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.EDIT_SHIPPING_ADDRESS, null));
                    ToastUtil.showMessage(getString(R.string.shippingaddress_message_edit_success));
                    break;

                case Flag.RequestCode.ADD_SHIPPING_ADDRESS:
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.ADD_SHIPPING_ADDRESS, null));
                    break;

                case Flag.RequestCode.MODIFY_CLAIM:
                    Claim claim = (Claim) data.getExtras().getSerializable("inquiry");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MODIFY_CLAIM, (claim != null ? claim : null)));
                    break;

                case Flag.RequestCode.SEARCH_WORD:
                    if (data != null) {
                        mPagerAdapter.removeAll();
                        String text = data.getExtras().getString("search_word");
                        if (CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("MainActivity", "SEARCH_WORD", text);
                        mPagerAdapter.setProductSearchData(text);
                    }
                    break;

                case Flag.RequestCode.REVIEW_WRITE:
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.REVIEW_WRITE, null));
                    break;
                case Flag.RequestCode.REVIEW_MODIFY:
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.REVIEW_MODIFY, null));
                    break;

                case Flag.RequestCode.KAKAO_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                    break;
                case Flag.RequestCode.NAVER_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                    break;
                case Flag.RequestCode.RC_GOOGLE_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                    break;
                case Flag.RequestCode.FACEBOOK_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                case Flag.RequestCode.VERIFY_EMAIL:
                    assert data != null;
                    String email = data.getStringExtra("email");
                    EventBusHelper.sendEvent(new EventBusData(requestCode, email));
                    break;
                case Flag.RequestCode.USER_SIZE:
                    EventBusHelper.sendEvent(new EventBusData(requestCode, RESULT_OK));
                    break;
            }
        } else {
            switch (requestCode) {
                case Flag.RequestCode.WRITE_CLAIM:
                    //   ToastUtil.showMessage(getResources().getString(R.string.common_message_error));
                    break;

                case Flag.RequestCode.KAKAO_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                    break;
                case Flag.RequestCode.NAVER_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                    break;
                case Flag.RequestCode.RC_GOOGLE_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
                    break;
                case Flag.RequestCode.FACEBOOK_LOGIN:
                    msg = "";
                    if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("resultMsg")) msg = data.getExtras().getString("resultMsg");
                    EventBusHelper.sendEvent(new EventBusData(Flag.RequestCode.MYPAGE_USERINFO_LOGIN, (resultCode+","+msg)));
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void moveMainTab(int index){
        mBinding.layoutContents.layoutPager.setCurrentItem(index);
        selectTab(index, false);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setEventBus() {
        disposable = EventBusHelper.listen(EventBusData.class).subscribe(new Consumer<EventBusData>() {
            @Override
            public void accept(EventBusData data) throws Exception {
                switch (data.getRequestCode()) {
                    case Flag.RequestCode.PRODUCT_DETAIL:
                        CommonUtil.startProductActivity(MainActivity.this, (Long) data.getData());
                        break;
                    case Flag.RequestCode.GO_TO_MAIN:
                        finish();
                        break;
                    case Flag.RequestCode.EDIT_SHIPPING_ADDRESS:
                        if (data.getData() != null) {
                            UserShipping shippingAddress = (UserShipping) data.getData();
                            Intent intent = new Intent(MainActivity.this, EditShippingAddressActivity.class);
                            intent.putExtra("orderShippingAddress", shippingAddress);
                            intent.putExtra("purchaseId", shippingAddress.getPId());
                            intent.putExtra("addButtonVisible", true);
                            startActivityForResult(intent, Flag.RequestCode.EDIT_SHIPPING_ADDRESS);
                        }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
               /* .subscribe(new Observer<EventBusData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EventBusData data) {
                        switch (data.getRequestCode()) {
                            case Flag.RequestCode.PRODUCT_DETAIL:
                                CommonUtil.startProductActivity(MainActivity.this, (Long) data.getData());
                                break;
                            case Flag.RequestCode.GO_TO_MAIN:
                                finish();
                                break;
                            case Flag.RequestCode.EDIT_SHIPPING_ADDRESS:
                                if (data.getData() != null) {
                                    UserShipping shippingAddress = (UserShipping) data.getData();
                                    Intent intent = new Intent(MainActivity.this, EditShippingAddressActivity.class);
                                    intent.putExtra("orderShippingAddress", shippingAddress);
                                    intent.putExtra("purchaseId", shippingAddress.getPId());
                                    intent.putExtra("addButtonVisible", true);
                                    startActivityForResult(intent, Flag.RequestCode.EDIT_SHIPPING_ADDRESS);
                                }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    // Main Pager
    private void initMainPager() {
        // Adapter
        if (mPagerAdapter == null)
            mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        // Pager
        mBinding.layoutContents.layoutPager.setAdapter(mPagerAdapter);
        mBinding.layoutContents.layoutPager.setSwipeEnabled(false);
        mBinding.layoutContents.layoutPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        // Tab
        addCustomTabs(currentViewPagerIndex); // Home
    }

    private void addCustomTabs(int current) {
        // Add Custom Tab
        layout_maintab_layout = new LinearLayout[]{mBinding.layoutContents.layoutMaintabLayout1
                , mBinding.layoutContents.layoutMaintabLayout2, mBinding.layoutContents.layoutMaintabLayout3
                , mBinding.layoutContents.layoutMaintabLayout4, mBinding.layoutContents.layoutMaintabLayout5};
        imageview_maintab_con = new ImageView[]{mBinding.layoutContents.imageviewMaintabIcon1
                , mBinding.layoutContents.imageviewMaintabIcon2, mBinding.layoutContents.imageviewMaintabIcon3
                , mBinding.layoutContents.imageviewMaintabIcon4, mBinding.layoutContents.imageviewMaintabIcon5};
        textview_maintab_title = new TextView[]{mBinding.layoutContents.textviewMaintabTitle1
                , mBinding.layoutContents.textviewMaintabTitle2, mBinding.layoutContents.textviewMaintabTitle3
                , mBinding.layoutContents.textviewMaintabTitle4, mBinding.layoutContents.textviewMaintabTitle5};
        for (int i = 0; i < layout_maintab_layout.length; i++) {
            layout_maintab_layout[i].setTag(i);
            layout_maintab_layout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    selectTab(position, position == currentViewPagerIndex);
                }
            });
            switch (i) {
                case 0: // Category
                    imageview_maintab_con[i].setBackgroundResource(R.drawable.selector_tab_main_category);
                    textview_maintab_title[i].setText(R.string.common_category);
                    break;

                case 1: // Brand
                    imageview_maintab_con[i].setBackgroundResource(R.drawable.selector_tab_main_brand);
                    textview_maintab_title[i].setText(R.string.common_brand);
                    break;

                case 2: // Home
                    imageview_maintab_con[i].setBackgroundResource(R.drawable.selector_tab_main_home);
                    textview_maintab_title[i].setText(R.string.common_home);
                    break;

                case 3: // Community
                    imageview_maintab_con[i].setBackgroundResource(R.drawable.selector_tab_main_community);
                    textview_maintab_title[i].setText(R.string.common_community);
                    break;

                case 4: // My Page
                    imageview_maintab_con[i].setBackgroundResource(R.drawable.selector_tab_main_my_page);
                    textview_maintab_title[i].setText(R.string.common_my_page);
                    break;
            }
        }
        selectTab(current, current == currentViewPagerIndex);
    }

    public void selectTab(int position, boolean isReselected) {
        int index = currentViewPagerIndex;
        if (position >= 2) index = position;
        for (int i = 0; i < layout_maintab_layout.length; i++) {
            if (index == i) {
                textview_maintab_title[i].setTextColor(Color.parseColor("#5d2ed1"));
                switch (i) {
                    case 2: // Home
                        imageview_maintab_con[i].setBackgroundResource(R.drawable.tool_icon_home_on);
                        break;
                    case 3: // Community
                        imageview_maintab_con[i].setBackgroundResource(R.drawable.tool_icon_community_on);
                        break;
                    case 4: // My Page
                        imageview_maintab_con[i].setBackgroundResource(R.drawable.tool_icon_mypage_on);
                        break;
                }
            } else {
                textview_maintab_title[i].setTextColor(Color.parseColor("#333333"));
                switch (i) {
                    case 2: // Home
                        imageview_maintab_con[i].setBackgroundResource(R.drawable.tool_icon_home_off);
                        break;
                    case 3: // Community
                        imageview_maintab_con[i].setBackgroundResource(R.drawable.tool_icon_community_off);
                        break;
                    case 4: // My Page
                        imageview_maintab_con[i].setBackgroundResource(R.drawable.tool_icon_mypage_off);
                        break;
                }
            }
        }
        if (position >= 2) currentViewPagerIndex = position;
        switch (position) {
            case 0: // Category
                showCategoryListDialog();
                //startActivity(new Intent(MainActivity.this, UserSizeUpdateActivity.class));
                break;
            case 1: // Brand
                showBrandListDialog();
                break;
            case 2: // Home
                mPagerAdapter.removeProduct();
                if (!isReselected) mBinding.layoutContents.layoutPager.setCurrentItem(0);
                break;
            case 3: // Community
                mPagerAdapter.removeProduct();
                if (!isReselected) mBinding.layoutContents.layoutPager.setCurrentItem(1);
                break;
            case 4: // My Page
                if (CommonUtil.checkToken()) {
                    mPagerAdapter.removeProduct();
                    if (!isReselected) {
                        if (false) {
                            // if login
                        } else {
                            mBinding.layoutContents.layoutPager.setCurrentItem(2);
                        }
                    }
                } else {
                    //ToastUtil.showMessage("로그인 후 사용해 주세요.");
                    CommonUtil.startLoginPage(MainActivity.this);
                    selectTab(2, false);
                }
                break;
        }
    }


    // Dialog
    private void showCategoryListDialog() {
        if (getSupportFragmentManager() != null) {
            if (mCategoryListDialog == null) {
                mCategoryListDialog = new CategoryListDialog();
                mCategoryListDialog.setOnCategoryListener(category -> CommonUtil.startCategoryScreen(this, category.type, category.hierarchies, false));
            }
            mCategoryListDialog.show(getSupportFragmentManager(), getBaseTag());
        }
    }

    private void showBrandListDialog() {
        if (getSupportFragmentManager() != null) {
            if (mBrandListDialog == null) {
                mBrandListDialog = new BrandListDialog();
                mBrandListDialog.setOnBrandListener(brand -> CommonUtil.startBrandScreen(this, brand, false));
            }
            mBrandListDialog.show(getSupportFragmentManager(), getBaseTag());
        }
    }


    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    /*private OnBrandListener mBrandListener = new OnBrandListener() {
        @Override
        public void onEvent(Brand brand) {
            if (brand != null) mPagerAdapter.setProductBrandData(brand);
        }
    };*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mLoadingIndicatorUtil != null) mLoadingIndicatorUtil.dismiss();
        if(mDisposable != null){
            mDisposable.dispose();
            mDisposable = null;
        }
        if(disposable != null){
            disposable.dispose();
        }
        super.onDestroy();
        BaseApplication.getInstance().setMoveToMain(null);
    }

    public CompositeDisposable getmDisposable() {
        return mDisposable;
    }

    public GuhadaDB getmDb() {
        return mDb;
    }

    public Disposable getDisposable() {
        return disposable;
    }
}