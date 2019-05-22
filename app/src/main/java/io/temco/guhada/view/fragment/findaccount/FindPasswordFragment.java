package io.temco.guhada.view.fragment.findaccount;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnTimerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.viewmodel.FindPasswordViewModel;
import io.temco.guhada.databinding.FragmentFindpasswordBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class FindPasswordFragment extends BaseFragment<FragmentFindpasswordBinding> {
    private FindPasswordViewModel mViewModel;

    public FindPasswordFragment(FindPasswordViewModel viewModel) {
        this.mViewModel = viewModel;
    }

    @Override
    protected String getBaseTag() {
        return FindPasswordFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_findpassword;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.stopTimer();
    }

    @Override
    protected void init() {
        mViewModel.setListener(new OnFindPasswordListener() {
            @Override
            public void hideKeyboard() {
                CommonUtil.hideKeyboard(getContext(), mBinding.linearLayout2);
            }

            @Override
            public void showMessage(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeActivity() {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearLayout2, message, getResources().getColor(R.color.colorPrimary), (int) getResources().getDimension(R.dimen.height_header));
            }

            @Override
            public void showResultView() {

            }

            @Override
            public void startTimer(String minute, String second) {
                mViewModel.setTimerMinute(minute);
                mViewModel.setTimerSecond(second);
                CommonUtil.startVerifyNumberTimer(mViewModel.getTimerSecond(), mViewModel.getTimerMinute(), new OnTimerListener() {
                    @Override
                    public void changeSecond(String second) {
                        mViewModel.setTimerSecond(second);
                    }

                    @Override
                    public void changeMinute(String minute) {
                        mViewModel.setTimerMinute(minute);
                    }

                    @Override
                    public void notifyMinuteAndSecond() {
                        mViewModel.notifyPropertyChanged(BR.timerSecond);
                        mViewModel.notifyPropertyChanged(BR.timerMinute);
                    }
                });
            }
        });
        mBinding.setViewModel(mViewModel);
        mBinding.constraintlayoutFindpwdResult.bringToFront();
        setTextWatchers();
        mBinding.executePendingBindings();
    }

    private void setTextWatchers() {
        // EMAIL - FIND ACCOUNT
        mBinding.edittextFindpwdEmailname.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                User user = mViewModel.getUser();
                user.setName(s.toString());
                mViewModel.setUser(user);
                mViewModel.notifyPropertyChanged(BR.user);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextFindpwdEmailid.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                User user = mViewModel.getUser();
                user.setEmail(s.toString());
                mViewModel.setUser(user);
                mViewModel.notifyPropertyChanged(BR.user);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextFindpwdEmailverify.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count < 7) {
                    mViewModel.setVerifyNumber(s.toString());
                    mViewModel.notifyPropertyChanged(BR.verifyNumber);
                } else {
                    // 6자리 초과 시 입력되지 않음
                    // 숫자가 아니면 입력되지 않음
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // EMAIL - CHANGE PASSWORD
        mBinding.edittextFindpwdNewpwd.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setNewPassword(s.toString());
                mViewModel.notifyPropertyChanged(BR.newPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextFindpwdNewpwdconfirm.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setNewPasswordConfirm(s.toString());
                mViewModel.notifyPropertyChanged(BR.newPasswordConfirm);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
