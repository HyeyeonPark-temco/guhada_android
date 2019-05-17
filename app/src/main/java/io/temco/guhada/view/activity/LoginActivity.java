package io.temco.guhada.view.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.viewmodel.LoginViewModel;
import io.temco.guhada.databinding.ActivityLoginBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class LoginActivity extends BindActivity<ActivityLoginBinding> {
    private LoginViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN_GOOGLE = 10001;

    @Override
    protected String getBaseTag() {
        return LoginActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.LOGIN;
    }

    @Override
    protected void init() {
        // CommonUtil.showSnackBar(mBinding.linearlayoutLogin, "test", getResources().getColor(R.color.colorPrimary), 50);

        // GOOGLE INIT
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        // INIT DATABINDING
        mViewModel = new LoginViewModel(() -> startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN_GOOGLE));
        mBinding.setViewModel(mViewModel);
        mBinding.includeLoginHeader.setViewModel(mViewModel);
        setIdAndPwdTextWatcher();
        mBinding.executePendingBindings();
    }

    private void setIdAndPwdTextWatcher() {
        mBinding.edittextviewLoginId.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBinding.edittextviewLoginPwd.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setPwd(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // SUCCESS
                    CommonUtil.debug(account.getEmail());
                }
            } catch (ApiException e) {
                CommonUtil.debug(e.getMessage());
            }
        }
    }
}
