package io.temco.guhada.view.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.auth.Session;

import io.temco.guhada.R;
import io.temco.guhada.common.KakaoSessionCallback;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.viewmodel.LoginViewModel;
import io.temco.guhada.databinding.ActivityLoginBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class LoginActivity extends BindActivity<ActivityLoginBinding> {
    private LoginViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private KakaoSessionCallback mKakaoSessionCallback;
    private CallbackManager mFacebookCallback;
    private ProfileTracker mFacebookTracker;
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
        initGoogleLogin();
        initKakaoLogin();
        initFacebookLogin();

        // INIT DATABINDING
        mViewModel = new LoginViewModel(new OnLoginListener() {
            @Override
            public void onGoogleLogin() {
                startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN_GOOGLE);
            }

            @Override
            public void onKakaoLogin() {
                mBinding.buttonLoginKakao.performClick();
            }

            @Override
            public void onFacebookLogin() {
//                LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile"));
                mBinding.buttonLoginFacebook.performClick();
            }
        });
        mViewModel.toolBarTitle = getResources().getString(R.string.login_title);
        mBinding.setViewModel(mViewModel);
        mBinding.includeLoginHeader.setViewModel(mViewModel);
        setIdAndPwdTextWatcher();
        mBinding.executePendingBindings();
    }

    private void initGoogleLogin() {
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
    }

    private void initKakaoLogin() {
        mKakaoSessionCallback = new KakaoSessionCallback();
        Session.getCurrentSession().addCallback(mKakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    private void initFacebookLogin() {
        mFacebookCallback = CallbackManager.Factory.create();
        mFacebookTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                String message = oldProfile != null ? oldProfile.getName() : currentProfile.getName();
                CommonUtil.debug("[FACEBOOK] TRACKOR: " + message);
            }
        };

        LoginManager.getInstance().registerCallback(mFacebookCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                CommonUtil.debug("[FACEBOOK] SUCCESS: " + loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                CommonUtil.debug("[FACEBOOK] CANCEL");
            }

            @Override
            public void onError(FacebookException error) {
                CommonUtil.debug("[FACEBOOK] ERROR: " + error.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaoSessionCallback);
        if(mFacebookTracker.isTracking()){
            mFacebookTracker.stopTracking();
        }
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
        // FACEBOOK
        mFacebookCallback.onActivityResult(requestCode, resultCode, data);

        // KAKAO
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // GOOGLE
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    CommonUtil.debug("[GOOGLE] " + account.getEmail());
                }
            } catch (ApiException e) {
                CommonUtil.debug("[GOOGLE] " + e.getStatusCode() + "-" + e.getMessage());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
