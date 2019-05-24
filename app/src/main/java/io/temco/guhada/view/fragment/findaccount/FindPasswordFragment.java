package io.temco.guhada.view.fragment.findaccount;

import android.text.Editable;
import android.text.TextWatcher;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.viewmodel.FindPasswordViewModel;
import io.temco.guhada.databinding.FragmentFindpasswordBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class FindPasswordFragment extends BaseFragment<FragmentFindpasswordBinding> {
    private FindPasswordViewModel mViewModel;

    public FindPasswordFragment() {
    }

    public FindPasswordFragment(FindPasswordViewModel mViewModel) {
        this.mViewModel = mViewModel;
    }

    public FindPasswordViewModel getmViewModel() {
        return mViewModel;
    }

    public void setmViewModel(FindPasswordViewModel mViewModel) {
        this.mViewModel = mViewModel;
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
        mBinding.setViewModel(mViewModel);
        mBinding.constraintlayoutFindpwdResult.bringToFront();
        setTextWatchers();
        mBinding.executePendingBindings();
    }

    private void setTextWatchers() {
        // BY EMAIL
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

        // BY PHONE NUMBER
        mBinding.edittextFindpwdPhonename.setTextWatcher(new TextWatcher() {
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
        mBinding.edittextFindpwdPhoneid.setTextWatcher(new TextWatcher() {
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
        mBinding.edittextFindpwdPhonenumber.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                User user = mViewModel.getUser();
                user.setPhoneNumber(s.toString());
                mViewModel.setUser(user);
                mViewModel.notifyPropertyChanged(BR.user);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextFindpwdPhoneverify.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setVerifyNumber(s.toString());
                mViewModel.notifyPropertyChanged(BR.verifyNumber);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // EMAIL - RESULT
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

    public void clearVerifyNumber(){
        mBinding.edittextFindpwdPhoneverify.setText("");
    }
}
