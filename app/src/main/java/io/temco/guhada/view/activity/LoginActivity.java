package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.usermgmt.response.model.UserProfile;

import io.temco.guhada.R;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.listener.OnSnsLoginListener;
import io.temco.guhada.common.sns.SnsLoginModule;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.NaverUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.viewmodel.LoginViewModel;
import io.temco.guhada.databinding.ActivityLoginBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class LoginActivity extends BindActivity<ActivityLoginBinding> {
    private LoginViewModel mViewModel;
    private OnSnsLoginListener mLoginListener;

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
        mLoginListener = new OnSnsLoginListener() {
            @Override
            public void kakaoLogin(UserProfile result) {
                SnsLoginModule.kakaoJoin(result);
            }

            @Override
            public void redirectTermsActivity(int type, Object data) {
                mViewModel.setSnsUser(data);
                Intent intent = new Intent(LoginActivity.this, TermsActivity.class);
                startActivityForResult(intent, type);
            }

            @Override
            public void redirectMainActivity(Token data) {
                Preferences.setToken(data);

                Token token = Preferences.getToken();
                Toast.makeText(LoginActivity.this, "[LOGIN SUCCESS] " + token.getAccessToken(), Toast.LENGTH_SHORT).show();
            }
        };

        SnsLoginModule.initFacebookLogin();
        SnsLoginModule.initGoogleLogin();
        SnsLoginModule.initKakaoLogin(mLoginListener);
        SnsLoginModule.initNaverLogin(mBinding.buttonLoginNaver, mLoginListener);

        // INIT BINDING
        mViewModel = new LoginViewModel(new OnLoginListener() {
            @Override
            public void onGoogleLogin() {
                startActivityForResult(new Intent(SnsLoginModule.getGoogleClientInstance()), Flag.RequestCode.RC_GOOGLE_LOGIN);
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
                CommonUtil.showSnackBar(mBinding.linearlayoutLogin, message);
            }

            @Override
            public void closeActivity(int resultCode) {
                finish();
            }
        });
        mViewModel.toolBarTitle = getResources().getString(R.string.login_title);
        mBinding.setViewModel(mViewModel);
        mBinding.includeLoginHeader.setViewModel(mViewModel);
        mBinding.executePendingBindings();
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
        SnsLoginModule.handleActivityResultForGoogle(requestCode, data, mLoginListener);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Flag.RequestCode.KAKAO_LOGIN:
                    SnsLoginModule.kakaoJoin((UserProfile) mViewModel.getSnsUser());
                    break;
                case Flag.RequestCode.NAVER_LOGIN:
                    SnsLoginModule.naverLogin((NaverUser) mViewModel.getSnsUser());
                    break;
                case Flag.RequestCode.GOOGLE_LOGIN:
                    SnsLoginModule.googleLogin((GoogleSignInAccount) mViewModel.getSnsUser());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
