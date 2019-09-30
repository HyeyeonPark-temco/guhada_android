package io.temco.guhada.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;

import com.google.android.material.tabs.TabLayout;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnFindAccountListener;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnTimerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CountTimer;
import io.temco.guhada.common.util.LoadingIndicatorUtil;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.viewmodel.account.FindAccountViewModel;
import io.temco.guhada.data.viewmodel.account.FindPasswordViewModel;
import io.temco.guhada.databinding.ActivityFindaccountBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.FindAccountPagerAdapter;
import io.temco.guhada.view.fragment.findaccount.FindIdFragment;
import io.temco.guhada.view.fragment.findaccount.FindPasswordFragment;

/**
 * 아이디 찾기, 비밀번호 재설정 Activity
 * @author Hyeyeon Park
 */
public class FindAccountActivity extends BindActivity<ActivityFindaccountBinding> {
    private FindAccountViewModel mViewModel;
    private FindAccountPagerAdapter mAdapter;
    private LoadingIndicatorUtil mLoadingIndicatorUtil;
    private int POSITION_FIND_ID = 0;
    private int POSITION_FIND_PWD = 1;

    @Override
    protected String getBaseTag() {
        return FindAccountActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_findaccount;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.FIND_ACCOUNT;
    }

    @Override
    protected void init() {
        mLoadingIndicatorUtil = new LoadingIndicatorUtil(this);
        initViewModel();
        initPagerAndTab();
        mBinding.tablayoutFindaccount.setTabTextColors(getResources().getColor(R.color.common_black), getResources().getColor(R.color.colorPrimary));
        mBinding.executePendingBindings();
    }

    private void initViewModel() {
        mViewModel = new FindAccountViewModel();
        mViewModel.setFindAccountListener(new OnFindAccountListener() {
            @Override
            public void showLoadingIndicator() {
                mLoadingIndicatorUtil.show();
            }

            @Override
            public void hideLoadingIndicator() {
                mLoadingIndicatorUtil.hide();
            }

            @Override
            public void redirectVerifyPhoneActivity() {
                startActivityForResult(new Intent(getApplicationContext(), VerifyPhoneActivity.class), Flag.RequestCode.VERIFY_PHONE);
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutFiindaccountContainer, message);
            }

            @Override
            public void showMessage(String message) {
                Toast.makeText(FindAccountActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeActivity(int resultCode) {
                finish();
            }

            @Override
            public void hideKeyboard() {
                CommonUtil.hideKeyboard(getApplicationContext(), mBinding.linearlayoutFiindaccountContainer);
            }

            @Override
            public void redirectSignUpActivity() {
                startActivity(new Intent(getApplicationContext(), JoinActivity.class));
                finish();
            }

            @Override
            public void redirectSignInActivity() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }

            @Override
            public void copyToClipboard(String text) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("email", text);
                manager.setPrimaryClip(clip);
                Toast.makeText(FindAccountActivity.this, "아이디가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccessGetIdentifyVerify() {
                if (mBinding.tablayoutFindaccount.getSelectedTabPosition() == POSITION_FIND_PWD) {
                    FindPasswordViewModel passwordViewModel = ((FindPasswordFragment) mAdapter.getItem(POSITION_FIND_PWD)).getmViewModel();
                    passwordViewModel.setMobile(mViewModel.user.getMobile());
                    passwordViewModel.setDi(mViewModel.di);
                    passwordViewModel.setResultVisibility(new ObservableInt(View.VISIBLE));
                    passwordViewModel.setUser(mViewModel.user);
                    passwordViewModel.notifyPropertyChanged(BR.resultVisibility);
                    passwordViewModel.notifyPropertyChanged(BR.user);
                }
                hideKeyboard();
                hideLoadingIndicator();
            }
        });
        mBinding.setViewModel(mViewModel);
        mBinding.includeFindaccountHeader.setViewModel(mViewModel);
    }

    private void initPagerAndTab() {
        // PAGER
        mAdapter = new FindAccountPagerAdapter(getSupportFragmentManager());

        FindIdFragment findIdFragment = new FindIdFragment();
        findIdFragment.setmVewModel(mViewModel);
        mAdapter.addFragment(findIdFragment);

        FindPasswordFragment findPasswordFragment = new FindPasswordFragment();
        findPasswordFragment.setmViewModel(new FindPasswordViewModel(new OnFindPasswordListener() {
            @Override
            public void showLoadingIndicator() {
                mLoadingIndicatorUtil.show();
            }

            @Override
            public void hideLoadingIndicator() {
                mLoadingIndicatorUtil.hide();
            }

            @Override
            public void setVerifyNumberViewEmpty() {
                if (mBinding.tablayoutFindaccount.getSelectedTabPosition() == POSITION_FIND_ID) {
                    //  FindAccountViewModel viewModel = ((FindAccountViewModel) mAdapter.getItem(POSITION_FIND_ID)).getmViewModel();

                } else {
                    FindPasswordFragment fragment = ((FindPasswordFragment) mAdapter.getItem(POSITION_FIND_PWD));
                    fragment.clearVerifyNumber();
                }
            }

            @Override
            public void redirectVerifyPhoneActivity() {
                startActivityForResult(new Intent(getApplicationContext(), VerifyPhoneActivity.class), Flag.RequestCode.VERIFY_PHONE);
            }

            @Override
            public void showMessage(String message) {
                ToastUtil.showMessage(message);
            }

            @Override
            public void closeActivity() {
                finish();
            }

            @Override
            public void hideKeyboard() {
                CommonUtil.hideKeyboard(getApplicationContext(), mBinding.linearlayoutFiindaccountContainer);
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutFiindaccountContainer, message);
            }

            @Override
            public void startTimer(String minute, String second) {
                FindPasswordViewModel viewModel = ((FindPasswordFragment) mAdapter.getItem(POSITION_FIND_PWD)).getmViewModel();
                viewModel.setTimerMinute(minute);
                viewModel.setTimerSecond(second);
                CountTimer.startVerifyNumberTimer(viewModel.getTimerSecond(), viewModel.getTimerMinute(), new OnTimerListener() {
                    @Override
                    public void changeSecond(String second) {
                        viewModel.setTimerSecond(second);
                    }

                    @Override
                    public void changeMinute(String minute) {
                        viewModel.setTimerMinute(minute);
                    }

                    @Override
                    public void notifyMinuteAndSecond() {
                        viewModel.notifyPropertyChanged(BR.timerSecond);
                        viewModel.notifyPropertyChanged(BR.timerMinute);
                    }
                });
            }

        }));
        mAdapter.addFragment(findPasswordFragment);

        mBinding.viewpagerFindaccount.setAdapter(mAdapter);

        // TAB
        mBinding.tablayoutFindaccount.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.viewpagerFindaccount.setCurrentItem(tab.getPosition());
//                stopTimer();
//                mViewModel.resetTimer();
//                mViewModel.setCheckedFindIdByInfo(false);
//                mViewModel.setCheckedFindIdByVerifyingPhone(false);
//                mViewModel.notifyPropertyChanged(BR.checkedFindIdByInfo);
//                mViewModel.notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mLoadingIndicatorUtil.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Flag.RequestCode.VERIFY_PHONE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mLoadingIndicatorUtil.show();
                    String name = data.getStringExtra("name");
                    String phoneNumber = data.getStringExtra("phoneNumber");
                    String di = data.getStringExtra("di");
                    Log.e("DI", di);

                    mViewModel.user.setPhoneNumber(phoneNumber);
                    mViewModel.user.setMobile(phoneNumber);
                    mViewModel.user.setName(name);
                    mViewModel.di = di;
                    mViewModel.getIdentityVerify(di);

                    //
                    if (mBinding.tablayoutFindaccount.getSelectedTabPosition() == POSITION_FIND_PWD) {
                        FindPasswordViewModel passwordViewModel = ((FindPasswordFragment) mAdapter.getItem(POSITION_FIND_PWD)).getmViewModel();
                        passwordViewModel.setMobile(mViewModel.user.getMobile());
                        passwordViewModel.setDi(mViewModel.di);
                        passwordViewModel.setResultVisibility(new ObservableInt(View.VISIBLE));
                        passwordViewModel.setUser(mViewModel.user);

                        passwordViewModel.notifyPropertyChanged(BR.resultVisibility);
                        passwordViewModel.notifyPropertyChanged(BR.user);
                    }
                }
            }
        }
    }
}
