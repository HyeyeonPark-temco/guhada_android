package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.sns.SnsLoginModule;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.viewmodel.LoginViewModel;
import io.temco.guhada.databinding.ActivityLoginBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class LoginActivity extends BindActivity<ActivityLoginBinding> {
    private LoginViewModel mViewModel;

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
        // INIT SNS LOGIN
        SnsLoginModule.initFacebookLogin();
        SnsLoginModule.initGoogleLogin();
        SnsLoginModule.initKakaoLogin();
        SnsLoginModule.initNaverLogin(mBinding.buttonLoginNaver);

        // INIT BINDING
        mViewModel = new LoginViewModel(new OnLoginListener() {
            @Override
            public void onGoogleLogin() {
                startActivityForResult(new Intent(SnsLoginModule.getGoogleClientInstance()), Info.RC_SIGN_IN_GOOGLE);
            }

            @Override
            public void onKakaoLogin() {
                mBinding.buttonLoginKakao.performClick();
            }

            @Override
            public void onFacebookLogin() {
                mBinding.buttonLoginFacebook.performClick();
            }

            @SuppressLint("HandlerLeak")
            @Override
            public void onNaverLogin() {
                mBinding.buttonLoginNaver.performClick();
            }

            @Override
            public void redirectJoinActivity() {
                startActivity(new Intent(getApplicationContext(), JoinActivity.class));
            }

            @Override
            public void redirectFindAccountActivity() {
                startActivity(new Intent(getApplicationContext(), FindAccountActivity.class));
            }

            @Override
            public void showMessage(String message) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutLogin, message, getResources().getColor(R.color.colorPrimary), (int) getResources().getDimension(R.dimen.height_header));
            }

            @Override
            public void closeActivity() {
                finish();
            }
        });
        mViewModel.toolBarTitle = getResources().getString(R.string.login_title);
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
    protected void onDestroy() {
        super.onDestroy();
        SnsLoginModule.removeKakaoCallback();
        SnsLoginModule.stopFacebookTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        SnsLoginModule.handlerActivityResultForFacebook(requestCode, resultCode, data);
        SnsLoginModule.handleActivityResultForKakao(requestCode, resultCode, data);
        SnsLoginModule.handleActivityResultForGoogle(requestCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
