package io.temco.guhada.view.fragment.findaccount;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;

import java.util.Objects;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.viewmodel.FindAccountViewModel;
import io.temco.guhada.databinding.FragmentFindidBinding;
import io.temco.guhada.view.adapter.SpinnerAdapter;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class FindIdFragment extends BaseFragment<FragmentFindidBinding> {
    private FindAccountViewModel mVewModel;

    public FindIdFragment(FindAccountViewModel viewModel) {
        this.mVewModel = viewModel;
    }

    public FindAccountViewModel getmVewModel() {
        return mVewModel;
    }

    public void setmVewModel(FindAccountViewModel mVewModel) {
        this.mVewModel = mVewModel;
    }

    @Override
    protected String getBaseTag() {
        return FindIdFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_findid;
    }

    @Override
    protected void init() {
        mBinding.setViewModel(mVewModel);

        // BY INFO
        // setTextWatchers();

        // BY VERIFYING PHONE
        // initVerifyPhoneLayout();

        // RESULT
        mBinding.includeFindidResult.setViewModel(mVewModel);

        mBinding.executePendingBindings();
    }

//    private void initNationalitySpinner() {
//        String[] array = {"", "내국인", "외국인"};
//        SpinnerAdapter adapter = new SpinnerAdapter(Objects.requireNonNull(getContext()), R.layout.item_findaccount_nationalityspinner, array);
//        adapter.setDropDownViewResource(R.layout.item_findaccount_spinnerdropdown);
//
//        mBinding.includeFindidVerifyphone.setOnClickNationalitySpinner(v -> {
//            mBinding.includeFindidVerifyphone.spinnerVerifyphoneForeigner.performClick();
//        });
//        mBinding.includeFindidVerifyphone.spinnerVerifyphoneForeigner.setAdapter(adapter);
//        mBinding.includeFindidVerifyphone.spinnerVerifyphoneForeigner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    Objects.requireNonNull(mVewModel.mUser.get()).setNationality(1);
//                } else {
//                    Objects.requireNonNull(mVewModel.mUser.get()).setNationality(2);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//    private void initMobileSpinner() {
//        String[] array = {"", "SKT", "KT", "LG"};
//        SpinnerAdapter adapter = new SpinnerAdapter(Objects.requireNonNull(getContext()), R.layout.item_findaccount_mobilespinner, array);
//        adapter.setDropDownViewResource(R.layout.item_findaccount_spinnerdropdown);
//
//        mBinding.includeFindidVerifyphone.setOnClickMobileSpinner(v -> {
//            mBinding.includeFindidVerifyphone.spinnerVerifyphoneMobile.performClick();
//        });
//        mBinding.includeFindidVerifyphone.spinnerVerifyphoneMobile.setAdapter(adapter);
//        mBinding.includeFindidVerifyphone.spinnerVerifyphoneMobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                User user = mVewModel.mUser.get();
//                Objects.requireNonNull(user).setMobileCarriers(position);
//                mVewModel.mUser.set(user);
//              //  mVewModel.notifyPropertyChanged(BR.mUser);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//
//    private void initVerifyPhoneLayout() {
//        mBinding.includeFindidVerifyphone.setViewModel(mVewModel);
//        mBinding.includeFindidVerifyphone.edittextVerifyphoneName.setTextWatcher(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (CommonUtil.validateNumber(s.toString())) {
//                    User user = mVewModel.mUser.get();
//                    Objects.requireNonNull(user).setName(s.toString());
//                    mVewModel.mUser.set(user);
//                  //  mVewModel.notifyPropertyChanged(BR.mUser);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        mBinding.includeFindidVerifyphone.edittextVerifyphoneBirth.setTextWatcher(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                User user = mVewModel.mUser.get();
//                Objects.requireNonNull(user).setBirth(s.toString());
//                mVewModel.mUser.set(user);
//              //  mVewModel.notifyPropertyChanged(BR.mUser);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        mBinding.includeFindidVerifyphone.edittextVerifyphonePhone.setTextWatcher(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                User user = mVewModel.mUser.get();
//                Objects.requireNonNull(user).setPhoneNumber(s.toString());
//                mVewModel.mUser.set(user);
//              //  mVewModel.notifyPropertyChanged(BR.mUser);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        mBinding.includeFindidVerifyphone.edittextVerifyphoneNumber.setTextWatcher(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mVewModel.setVerifyNumber(s.toString());
//                mVewModel.notifyPropertyChanged(BR.verifyNumber);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        initNationalitySpinner();
//        initMobileSpinner();
//    }
}
