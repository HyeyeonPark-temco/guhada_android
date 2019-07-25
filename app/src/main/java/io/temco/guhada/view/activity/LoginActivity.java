package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.Arrays;

import io.temco.guhada.R;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.listener.OnSnsLoginListener;
import io.temco.guhada.common.sns.SnsLoginModule;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.model.naver.NaverUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.viewmodel.account.LoginViewModel;
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
        initFacebookSdk();

        // INIT SNS LOGIN
        mLoginListener = new OnSnsLoginListener() {
            @Override
            public void kakaoLogin(UserProfile result) {
                SnsLoginModule.kakaoJoin(result, getSnsLoginServerListener());
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

            @Override
            public void showMessage(String message) {
                ToastUtil.showMessage(message);
            }
        };

        SnsLoginModule.initFacebookLogin(getSnsLoginServerListener());
        SnsLoginModule.initGoogleLogin();
        SnsLoginModule.initKakaoLogin(mLoginListener);
        SnsLoginModule.initNaverLogin(mBinding.buttonLoginNaver, mLoginListener, getSnsLoginServerListener());

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
                mBinding.buttonLoginFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
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
                ToastUtil.showMessage(message);
//                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutLogin, message);
            }

            @Override
            public void closeActivity(int resultCode) {
                setResult(resultCode);
                finish();
            }
        });
        mViewModel.toolBarTitle = getResources().getString(R.string.login_title);
        mBinding.setViewModel(mViewModel);
        mBinding.includeLoginHeader.setViewModel(mViewModel);
        mBinding.executePendingBindings();
    }

    private void initFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SnsLoginModule.removeKakaoCallback();
//        SnsLoginModule.stopFacebookTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        SnsLoginModule.handlerActivityResultForFacebook(requestCode, resultCode, data);
        SnsLoginModule.handleActivityResultForKakao(requestCode, resultCode, data);

        if (mViewModel.getSnsUser() == null) {
            SnsLoginModule.handleActivityResultForGoogle(requestCode, data, mLoginListener, getSnsLoginServerListener());
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Flag.RequestCode.KAKAO_LOGIN:
                    SnsLoginModule.kakaoJoin((UserProfile) mViewModel.getSnsUser(), getSnsLoginServerListener());
                    break;
                case Flag.RequestCode.NAVER_LOGIN:
                    SnsLoginModule.naverLogin((NaverUser) mViewModel.getSnsUser(), getSnsLoginServerListener());
                    break;
                case Flag.RequestCode.RC_GOOGLE_LOGIN:
                    SnsLoginModule.googleLogin((GoogleSignInAccount) mViewModel.getSnsUser(), getSnsLoginServerListener());
                    break;
                case Flag.RequestCode.FACEBOOK_LOGIN:

                    break;
            }
        } else {
            mViewModel.setSnsUser(null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private OnServerListener getSnsLoginServerListener() {
        return (success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                if (model.resultCode == Flag.ResultCode.SUCCESS) {
                    Token token = (Token) model.data;
                    Preferences.setToken(token);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(model.message);
                }
            } else {
                String message = (String) o;
                ToastUtil.showMessage(message);
            }
        };
    }
}
