package io.temco.guhada.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnFindAccountListener;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnTimerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.data.viewmodel.FindAccountViewModel;
import io.temco.guhada.data.viewmodel.FindPasswordViewModel;
import io.temco.guhada.databinding.ActivityFindaccountBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.FindAccountPagerAdapter;
import io.temco.guhada.view.fragment.findaccount.FindIdFragment;
import io.temco.guhada.view.fragment.findaccount.FindPasswordFragment;

public class FindAccountActivity extends BindActivity<ActivityFindaccountBinding> {
    private FindAccountViewModel mViewModel;
    private FindAccountPagerAdapter mAdapter;
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
        initViewModel();
        initPagerAndTab();
        mBinding.tablayoutFindaccount.setTabTextColors(getResources().getColor(R.color.common_black), getResources().getColor(R.color.colorPrimary));
        mBinding.executePendingBindings();
    }

    private void initViewModel() {
        mViewModel = new FindAccountViewModel();
        mViewModel.setFindAccountListener(new OnFindAccountListener() {
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
            public void closeActivity() {
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

        });
        mBinding.setViewModel(mViewModel);
        mBinding.includeFindaccountHeader.setViewModel(mViewModel);
    }

    private void initPagerAndTab() {
        // PAGER
        mAdapter = new FindAccountPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new FindIdFragment(mViewModel));
        mAdapter.addFragment(new FindPasswordFragment(new FindPasswordViewModel(new OnFindPasswordListener() {
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
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                CommonUtil.startVerifyNumberTimer(viewModel.getTimerSecond(), viewModel.getTimerMinute(), new OnTimerListener() {
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
        })));

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Flag.RequestCode.VERIFY_PHONE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();
            }

            if (mBinding.tablayoutFindaccount.getSelectedTabPosition() == POSITION_FIND_PWD) {
                // RESET CHECK BOX
//                FindPasswordViewModel passwordViewModel = ((FindPasswordFragment) mAdapter.getItem(POSITION_FIND_PWD)).getmViewModel();
//                passwordViewModel.setCheckedFindIdByVerifyingPhone(false);
//                passwordViewModel.notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
            } else {
                // RESET CHECK BOX
//                FindAccountViewModel findAccountViewModel = ((FindIdFragment) mAdapter.getItem(POSITION_FIND_ID)).getmVewModel();
//                findAccountViewModel.setCheckedFindIdByVerifyingPhone(false);
//                findAccountViewModel.notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);

                // RESULT
                if (data != null) {
                    String name = data.getStringExtra("name");
                    String phoneNumber = data.getStringExtra("phoneNumber");

                    LoginServer.findUserId((success, o) -> {
                        if (success) {
                            BaseModel model = (BaseModel) o;
                            switch (model.resultCode) {
                                case Flag.ResultCode.SUCCESS:
                                    User user = (User) model.data;
                                    mViewModel.setResultVisibility(View.VISIBLE);
                                    mViewModel.setUser(user);
                                    mViewModel.notifyPropertyChanged(BR.resultVisibility);
                                    mViewModel.notifyPropertyChanged(BR.user);
                                    break;
                                case Flag.ResultCode.DATA_NOT_FOUND:
                                    String message = getResources().getString(R.string.findid_message_wronginfo);
                                    CommonUtil.showSnackBar(mBinding.linearlayoutFiindaccountContainer, message);
                                    break;
                            }
                        } else {
                            String message = (String) o;
                            CommonUtil.showSnackBar(mBinding.linearlayoutFiindaccountContainer, message);
                        }
                    }, name, phoneNumber);
                }

            }
        }
    }
}
