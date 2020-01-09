package io.temco.guhada.common.sns;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
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
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.listener.OnSnsLoginListener;
import io.temco.guhada.common.sns.kakao.KakaoSessionCallback;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.naver.NaverUser;
import io.temco.guhada.data.model.user.SnsUser;
import io.temco.guhada.data.model.user.UserProfile;
import io.temco.guhada.data.server.UserServer;

public class SnsLoginModule {
    public static OAuthLogin mNaverLoginModule;
    private static KakaoSessionCallback mKakaoSessionCallback;
    private static GoogleSignInClient mGoogleSignInClient;
    private static CallbackManager mFacebookCallback;
    private static ProfileTracker mFacebookProfileTracker;
    private static AccessTokenTracker mFacebookAccessTokenTracker;

    // NAVER
    @SuppressLint("HandlerLeak")
    public static void initNaverLogin(OAuthLoginButton button, OnSnsLoginListener loginListener, OnServerListener serverListener) {
        Resources resources = BaseApplication.getInstance().getResources();
        mNaverLoginModule = OAuthLogin.getInstance();
        mNaverLoginModule.showDevelopersLog(true);
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
                    OAuthLoginState state = mNaverLoginModule.getState(context);
                    if (CustomLog.getFlag())
                        CustomLog.L("[NAVER] LOGIN-SUCCESS: AccessToken " + accessToken);

                    // CALL NAVER USER PROFILE
                    OnServerListener listener = (successGetProfile, o) -> {
                        if (successGetProfile) {
                            NaverUser naverUser = (NaverUser) o;
                            UserServer.checkExistSnsUser((successCheckExist, obj) -> {
                                if (successCheckExist) {
                                    BaseModel model = (BaseModel) obj;
                                    switch (model.resultCode) {
                                        case Flag.ResultCode.SUCCESS:
                                            naverLogin(naverUser, serverListener);
                                            break;
                                        case Flag.ResultCode.DATA_NOT_FOUND:
                                            loginListener.redirectTermsActivity(Flag.RequestCode.NAVER_LOGIN, naverUser, naverUser.getEmail());
                                            break;
                                        default:
                                            ToastUtil.showMessage(((BaseModel) obj).message);
                                            break;
                                    }
                                } else {
                                    if (obj instanceof BaseModel)
                                        ToastUtil.showMessage(((BaseModel) obj).message);
                                    else
                                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror));
                                }
                            }, "NAVER", ((NaverUser) o).getId(), naverUser.getEmail());
                        } else {
                            String message = o.equals("") ? "네이버 로그인 오류" : o.toString();
                            if (CustomLog.getFlag())
                                CustomLog.L("[NAVER] PROFILE-FAILED: " + message);
                        }
                    };

                    UserServer.getNaverProfile(listener);
                } else {
                    String errorCode = mNaverLoginModule.getLastErrorCode(context).getCode();
                    String errorDesc = mNaverLoginModule.getLastErrorDesc(context);
                    if (CustomLog.getFlag())
                        CustomLog.L("[NAVER] LOGIN-FAILED: " + "errorCode:" + errorCode + ", errorDesc:" + errorDesc);
                }
            }
        });
    }

    // FACEBOOK
    public static void initFacebookLogin(FacebookCallback<LoginResult> facebookCallback) {
        mFacebookCallback = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallback, facebookCallback);
    }

    private void initFacebookProfileTracker() {
        mFacebookProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                String message = oldProfile != null ? oldProfile.getName() : currentProfile.getName();
                Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
                CommonUtil.debug("[FACEBOOK] TRACKOR: " + message);
            }
        };
    }

    public static void stopFacebookProfileTracking() {
        if (mFacebookProfileTracker.isTracking()) {
            mFacebookProfileTracker.stopTracking();
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

    public static void handleActivityResultForGoogle(int requestCode, @Nullable Intent data, OnSnsLoginListener loginListener, OnServerListener serverListener) {
        if (requestCode == Flag.RequestCode.RC_GOOGLE_LOGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getId() != null && account.getEmail() != null) {
                    UserServer.checkExistSnsUser((success, o) -> {
                        if (success) {
                            BaseModel model = (BaseModel) o;
                            switch (model.resultCode) {
                                case Flag.ResultCode.SUCCESS:
                                    googleLogin(account, serverListener);
                                    break;
                                case Flag.ResultCode.DATA_NOT_FOUND:
                                    loginListener.redirectTermsActivity(requestCode, account, account.getEmail());
                                    break;
                                default:
                                    ToastUtil.showMessage(((BaseModel) o).message);
                                    break;
                            }
                        } else {
                            if (o instanceof BaseModel)
                                ToastUtil.showMessage(((BaseModel) o).message);
                            else
                                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror));
                        }
                    }, "GOOGLE", account.getId(), account.getEmail());
                } else {
                    ToastUtil.showMessage("로그인 정보가 부족합니다.");
                }
            } catch (ApiException e) {
                CommonUtil.debug("[GOOGLE] " + e.getStatusCode() + "-" + e.getMessage());
            }
        }
    }

    // KAKAO
    public static void initKakaoLogin(OnSnsLoginListener loginListener) {
        mKakaoSessionCallback = new KakaoSessionCallback(loginListener);
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

    public static void kakaoLogin(com.kakao.usermgmt.response.model.UserProfile result, OnServerListener serverListener) {
        SnsUser user = createSnsUser(result.getEmail(), String.valueOf(result.getId()), "KAKAO", result.getNickname(), result.getProfileImagePath());
        UserServer.kakaoLogin(user, serverListener);
    }

    public static void naverLogin(NaverUser naverUser, OnServerListener serverListener) {
        SnsUser user = createSnsUser(naverUser.getEmail(), naverUser.getId(), "NAVER", naverUser.getName(), naverUser.getProfileImage());
        UserServer.naverLogin(user, serverListener);
    }

    public static void googleLogin(GoogleSignInAccount account, OnServerListener snsLoginListener) {
        if (account != null) {
            if (CustomLog.INSTANCE.getFlag())
                CustomLog.INSTANCE.L("googleLogin", "account.getEmail()", account.getEmail(), "account.getId()", account.getId());
            SnsUser user = createSnsUser(account.getEmail(), account.getId(), "GOOGLE", account.getDisplayName(), account.getPhotoUrl() != null ? account.getPhotoUrl().getPath() : "");
            UserServer.googleLogin(snsLoginListener, user);
        }
    }

    public static void facebookLogin(SnsUser user, OnServerListener serverListener) {
        UserServer.facebookLogin(serverListener, user);
    }

    private static SnsUser createSnsUser(String email, String snsId, String snsType, String name, String profileUrl) {
        SnsUser user = new SnsUser();
        user.setEmail(email);
        user.setSnsId(snsId);
        user.setSnsType(snsType);

        UserProfile profile = new UserProfile();
        profile.setSnsId(snsId);
        profile.setEmail(email);
        profile.setName(name);
        profile.setImageUrl(profileUrl);
        user.setUserProfile(profile);
        return user;
    }

    public static void logoutForKakao(OnServerListener listener) {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                listener.onResult(true, "SUCCESS KAKAOTALK LOGOUT");
            }

            @Override
            public void onNotSignedUp() {
                super.onNotSignedUp();
                listener.onResult(false, "NOT KAKAOTALK SGINED UP");
            }
        });
    }

    public static void unlinkForKakao(OnServerListener listener) {
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                listener.onResult(false, "NOT KAKAOTALK LOGGED IN");
            }

            @Override
            public void onNotSignedUp() {
                listener.onResult(false, "NOT KAKAOTALK SGINED UP");
            }

            @Override
            public void onSuccess(Long result) {
                listener.onResult(true, "SUCCESS KAKAOTALK UNLINK");
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                listener.onResult(false, "FAILED KAKAOTALK UNLINK : " + errorResult.getErrorMessage());
            }
        });
    }

    public static void logoutForGoogle(OnServerListener listener) {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listener.onResult(true, "SUCCESS GOOGLE LOGOUT");
                } else {
                    listener.onResult(true, "FAILED GOOGLE LOGOUT");
                }
            });
        } else {
            listener.onResult(false, "NOT GOOGLE LOGGED IN");
        }
    }

    public static void logoutForFacebook(OnServerListener listener) {
        if (mFacebookAccessTokenTracker == null) {
            mFacebookAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    if (currentAccessToken == null) {
                        listener.onResult(true, "SUCCESS FACEBOOK LOGOUT");
                    }
                }
            };
        }

        LoginManager.getInstance().logOut();
        listener.onResult(true, "SUCCESS FACEBOOK LOGOUT");
    }

    public static void logoutNaver() {
        Context context = BaseApplication.getInstance();
        if (context != null && mNaverLoginModule != null)
            mNaverLoginModule.logout(context);
    }

    public static void logoutSNS() {
        logoutForFacebook((success, o) -> {
        });
        logoutForGoogle((success, o) -> {
        });
        logoutForKakao((success, o) -> {
        });
//        logoutNaver();
    }
}
