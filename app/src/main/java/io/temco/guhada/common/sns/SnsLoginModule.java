package io.temco.guhada.common.sns;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Preferences;
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
                            // 이메일 중복 체크
                            LoginServer.checkEmail((success12, obj) -> {
                                if (success12) {
                                    BaseModel model = (BaseModel) obj;
                                    if (model.resultCode == Flag.ResultCode.SUCCESS) {
                                        // (중복X) 가입되지 않은 이메일 - 약관 동의 Activity 호출
                                        loginListener.redirectTermsActivity(Flag.RequestCode.NAVER_LOGIN, naverUser);
                                    } else {
                                        // (중복O) 가입된 이메일 - /naverLogin 호출
                                        naverLogin(naverUser);
                                    }
                                } else {
                                    Toast.makeText(context, (String) obj, Toast.LENGTH_SHORT).show();
                                }
                            }, naverUser.getEmail());
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
        LoginManager.getInstance().registerCallback(mFacebookCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    facebookLogin(object);
                });

                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,name,email");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();

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

    private void initFacebookTracker(){
        mFacebookTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                String message = oldProfile != null ? oldProfile.getName() : currentProfile.getName();
                Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
                CommonUtil.debug("[FACEBOOK] TRACKOR: " + message);
            }
        };
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

    public static void handleActivityResultForGoogle(int requestCode, @Nullable Intent data, OnSnsLoginListener loginListener) {
        if (requestCode == Flag.RequestCode.RC_GOOGLE_LOGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    LoginServer.checkEmail((success, o) -> {
                        if (success) {
                            BaseModel model = (BaseModel) o;
                            if (model.resultCode == Flag.ResultCode.SUCCESS) {
                                // (중복X) 가입되지 않은 이메일 - 약관 동의 Activity 호출
                                loginListener.redirectTermsActivity(requestCode, account);
                            } else {
                                // (중복O) 가입된 이메일 - /googleLogin 호출
                                googleLogin(account);
                            }
                        } else {
                            Toast.makeText(BaseApplication.getInstance(), (String) o, Toast.LENGTH_SHORT).show();
                        }
                    }, account.getEmail());
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

    public static void kakaoJoin(com.kakao.usermgmt.response.model.UserProfile result) {
        SnsUser user = createSnsUser(result.getEmail(), String.valueOf(result.getId()), "KAKAO", result.getNickname(), result.getProfileImagePath());
        LoginServer.kakaoLogin(user, getSnsLoginListener());
    }

    public static void naverLogin(NaverUser naverUser) {
        SnsUser user = createSnsUser(naverUser.getEmail(), naverUser.getId(), "NAVER", naverUser.getName(), naverUser.getProfileImage());
        LoginServer.naverLogin(user, getSnsLoginListener());
    }

    public static void googleLogin(GoogleSignInAccount account) {
        if (account != null) {
            SnsUser user = createSnsUser(account.getEmail(), account.getId(), "GOOGLE", account.getDisplayName(), account.getPhotoUrl() != null ? account.getPhotoUrl().getPath() : "");
            LoginServer.googleLogin(getSnsLoginListener(), user);
        }
    }

    public static void facebookLogin(JSONObject object) {
        try {
            String email = object.getString("email");
            String name = object.getString("name");
            String picture = object.getString("picture");
            String snsId = object.getString("id");
            SnsUser user = createSnsUser(email, snsId, "FACEBOOK", name, picture);
            LoginServer.facebookLogin(getSnsLoginListener(), user);
        } catch (JSONException e) {
            CommonUtil.debug("[FACEBOOK] EXCEPTION: " + e.getMessage());
        }
    }

    private static SnsUser createSnsUser(String email, String snsId, String snsType, String name, String profileUrl) {
        SnsUser user = new SnsUser();
        user.setEmail(email);
        user.setSnsId(snsId);
        user.setType(snsType);

        UserProfile profile = new UserProfile();
        profile.setSnsId(snsId);
        profile.setEmail(email);
        profile.setName(name);
        profile.setImageUrl(profileUrl);
        user.setUserProfile(profile);

        return user;
    }

    private static OnServerListener getSnsLoginListener() {
        return (success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                if (model.resultCode == Flag.ResultCode.SUCCESS) {
                    Token token = (Token) model.data;
                    Preferences.setToken(token);
                    Toast.makeText(BaseApplication.getInstance().getApplicationContext(), "[LOGIN SUCCESS] " + token.getAccessToken(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BaseApplication.getInstance(), model.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                String message = (String) o;
                Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
