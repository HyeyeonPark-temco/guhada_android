package io.temco.guhada.common.sns;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

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
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.listener.OnSnsLoginListener;
import io.temco.guhada.common.sns.kakao.KakaoSessionCallback;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.NaverUser;
import io.temco.guhada.data.model.SnsUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.UserProfile;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.view.activity.VerifyPhoneActivity;

public class SnsLoginModule {
    private static OAuthLogin mNaverLoginModule;
    private static KakaoSessionCallback mKakaoSessionCallback;
    private static GoogleSignInClient mGoogleSignInClient;
    private static CallbackManager mFacebookCallback;
    private static ProfileTracker mFacebookTracker;

    // NAVER
    @SuppressLint("HandlerLeak")
    public static void initNaverLogin(OAuthLoginButton button, OnSnsLoginListener loginListener) {
        Resources resources = BaseApplication.getInstance().getResources();
        mNaverLoginModule = OAuthLogin.getInstance();
        mNaverLoginModule.init(BaseApplication.getInstance(), resources.getString(R.string.naver_oauth_client_id), resources.getString(R.string.naver_oauth_client_secret),
                resources.getString(R.string.naver_oauth_client_name));

        button.setOAuthLoginHandler(new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                Context context = BaseApplication.getInstance();
                if (success) {
                    String accessToken = mNaverLoginModule.getAccessToken(context);
                    String refreshToken = mNaverLoginModule.getRefreshToken(context);
                    long expireAt = mNaverLoginModule.getExpiresAt(context);
                    String tokenType = mNaverLoginModule.getTokenType(context);

                    CommonUtil.debug("[NAVER] LOGIN-SUCCESS: AccessToken " + accessToken);

                    // CALL NAVER USER PROFILE
                    OnServerListener listener = (successGetProfile, o) -> {
                        if (successGetProfile) {
                            NaverUser naverUser = (NaverUser) o;
                            SnsUser user = new SnsUser();
                            user.setEmail(naverUser.getEmail());
                            user.setSnsId(naverUser.getId());
                            user.setType("NAVER");
                            UserProfile profile = new UserProfile();
                            profile.setSnsId(user.getSnsId());
                            profile.setEmail(user.getEmail());
                            profile.setName(naverUser.getName());
                            profile.setImageUrl(naverUser.getProfileImage());
                            user.setUserProfile(profile);

                            LoginServer.naverLogin(user, (success1, o1) -> {
                                if (success1) {
                                    BaseModel model = (BaseModel) o1;
                                    switch (model.resultCode) {
                                        case Flag.ResultCode.SUCCESS:
                                            // 로그인 성공
                                            loginListener.redirectMainActivity((Token) model.data);
                                            break;
                                        case Flag.ResultCode.ALREADY_SIGNED_UP:
                                            // 회원가입 진행
                                            loginListener.redirectTermsActivity();
                                            break;
                                    }
                                } else {
                                    String message = (String) o1;
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            });

//                            // 네이버 프로필 호출 확인용
//                            Toast.makeText(context, naverUser.getEmail(), Toast.LENGTH_SHORT).show();
//                            CommonUtil.debug("[NAVER] PROFILE-SUCCESS: " + naverUser.getEmail());
                        } else {
                            CommonUtil.debug("[NAVER] PROFILE-FAILED: " + o.toString());
                        }
                    };

                    LoginServer.getNaverProfile(listener, accessToken);
                } else {
                    String errorCode = mNaverLoginModule.getLastErrorCode(context).getCode();
                    String errorDesc = mNaverLoginModule.getLastErrorDesc(context);
                    CommonUtil.debug("[NAVER] LOGIN-FAILED: " + errorDesc);

                    Toast.makeText(context, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // FACEBOOK
    public static void initFacebookLogin() {
        mFacebookCallback = CallbackManager.Factory.create();
        mFacebookTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                String message = oldProfile != null ? oldProfile.getName() : currentProfile.getName();
                Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
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

    public static void stopFacebookTracking() {
        if (mFacebookTracker.isTracking()) {
            mFacebookTracker.stopTracking();
        }
    }

    public static void handlerActivityResultForFacebook(int requestCode, int resultCode, @Nullable Intent data) {
        mFacebookCallback.onActivityResult(requestCode, resultCode, data);
    }

    // GOOGLE
    public static void initGoogleLogin() {
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(BaseApplication.getInstance(), mGoogleSignInOptions);
    }

    public static Intent getGoogleClientInstance() {
        return mGoogleSignInClient.getSignInIntent();
    }

    public static void handleActivityResultForGoogle(int requestCode, @Nullable Intent data) {
        if (requestCode == Info.RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // CONVERT GOOGLE ACCOUNT MODEL TO SNS USER MODEL
                    SnsUser snsUser = new SnsUser();
                    snsUser.setEmail(account.getEmail());
                    snsUser.setSnsId(account.getId());

                    UserProfile profile = new UserProfile();
                    profile.setEmail(account.getEmail());
                    profile.setFamilyName(account.getFamilyName());
                    profile.setGivenName(account.getGivenName());
                    profile.setSnsId(account.getId());
                    profile.setGivenName(account.getDisplayName());
                    profile.setImageUrl(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "");

                    snsUser.setUserProfile(profile);

                    // CALL GOOGLE LOGIN API
                    LoginServer.googleLogin((success, o) -> {
                        if (success) {
                            BaseModel result = (BaseModel) o;
                            if (result.resultCode == Flag.ResultCode.ALREADY_SIGNED_UP) {
                                // 가입된 계정 존재 --> 로그인

                            } else {
                                // 회원가입
                            }

                            Toast.makeText(BaseApplication.getInstance(), account.getEmail() + " 로그인", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaseApplication.getInstance(), o.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }, snsUser);

                    CommonUtil.debug("[GOOGLE] " + account.getEmail());
                }
            } catch (ApiException e) {
                CommonUtil.debug("[GOOGLE] " + e.getStatusCode() + "-" + e.getMessage());
            }
        }
    }

    // KAKAO
    public static void initKakaoLogin() {
        mKakaoSessionCallback = new KakaoSessionCallback();
        Session.getCurrentSession().addCallback(mKakaoSessionCallback);
    }

    public static void removeKakaoCallback() {
        Session.getCurrentSession().removeCallback(mKakaoSessionCallback);
    }

    public static void handleActivityResultForKakao(int requestCode, int resultCode, @Nullable Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

}
