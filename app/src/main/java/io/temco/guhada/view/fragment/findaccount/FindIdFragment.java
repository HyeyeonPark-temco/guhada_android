package io.temco.guhada.view.fragment.findaccount;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import java.util.Objects;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.data.viewmodel.FindAccountViewModel;
import io.temco.guhada.databinding.FragmentFindidBinding;
import io.temco.guhada.view.adapter.SpinnerAdapter;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class FindIdFragment extends BaseFragment<FragmentFindidBinding> {
    private FindAccountViewModel viewModel;

    public FindIdFragment(FindAccountViewModel viewModel) {
        this.viewModel = viewModel;
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
        mBinding.setViewModel(viewModel);

        // VERIFY PHONE
        mBinding.includeFindidVerifyphone.setViewModel(viewModel);
        mBinding.includeFindidResult.setViewModel(viewModel);

        initNationalitySpinner();
        initMobileSpinner();
        setTextWatchers();

        mBinding.executePendingBindings();
    }

    private void initNationalitySpinner() {
        String[] array = {"", "내국인", "외국인"};
        SpinnerAdapter adapter = new SpinnerAdapter(Objects.requireNonNull(getContext()), R.layout.item_findaccount_nationalityspinner, array);
        adapter.setDropDownViewResource(R.layout.item_findaccount_spinnerdropdown);

        mBinding.includeFindidVerifyphone.setOnClickNationalitySpinner(v -> {
            mBinding.includeFindidVerifyphone.spinnerVerifyphoneForeigner.performClick();
        });
        mBinding.includeFindidVerifyphone.spinnerVerifyphoneForeigner.setAdapter(adapter);
        mBinding.includeFindidVerifyphone.spinnerVerifyphoneForeigner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    viewModel.setForeigner(true);
                } else {
                    viewModel.setForeigner(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initMobileSpinner() {
        String[] array = {"", "SKT", "KT", "LG"};
        SpinnerAdapter adapter = new SpinnerAdapter(Objects.requireNonNull(getContext()), R.layout.item_findaccount_mobilespinner, array);
        adapter.setDropDownViewResource(R.layout.item_findaccount_spinnerdropdown);

        mBinding.includeFindidVerifyphone.setOnClickMobileSpinner(v -> {
            mBinding.includeFindidVerifyphone.spinnerVerifyphoneMobile.performClick();
        });
        mBinding.includeFindidVerifyphone.spinnerVerifyphoneMobile.setAdapter(adapter);
        mBinding.includeFindidVerifyphone.spinnerVerifyphoneMobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // MOBILE
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setTextWatchers() {
        mBinding.edittextFindidName.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setName(s.toString());
                viewModel.notifyPropertyChanged(BR.name);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextFindidPhone.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPhoneNumber(s.toString());
                viewModel.notifyPropertyChanged(BR.phoneNumber);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
