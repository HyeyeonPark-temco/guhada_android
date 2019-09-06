package io.temco.guhada.view.activity.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import org.json.JSONException;
import org.json.JSONObject;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.ProductByList;
import io.temco.guhada.data.server.ProductServer;
import io.temco.guhada.databinding.ActivityMainBinding;
import io.temco.guhada.view.activity.BlockChainHistoryActivity;
import io.temco.guhada.view.activity.MainActivity;

/**
 * @param <B>
 * @author park jungho
 * <p>
 * nfc 기능 공통 Activity 넣음
 */
public abstract class BindActivity<B extends ViewDataBinding> extends BaseActivity {

    // -------- LOCAL VALUE --------
    public B mBinding;
    public boolean isClickAble = true;

    // NFC
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private final String TAG_COMPANY = "TAG_COMPANY";
    private final String TAG_ID = "TAG_ID";
    private final String COMPANY_NAME = "GUHADA";
    private Handler handler;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L(this.getClass().getSimpleName(),"onCreate");
        super.onCreate(savedInstanceState);
        try{
            mBinding = DataBindingUtil.setContentView(this, getLayoutId());
            // Init
            if (!"SplashActivity".equalsIgnoreCase(this.getClass().getSimpleName())) {
                initNfc();
            }
            init();
            handler = new Handler(this.getMainLooper());
        }catch (Exception e){
            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
        }
    }


    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // NFC
    private void initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // 미지원 기기
            if (CustomLog.INSTANCE.getFlag()) CustomLog.INSTANCE.L("BindActivity", this.getClass().getSimpleName());
            //if ("MainActivity".equalsIgnoreCase(this.getClass().getSimpleName())) showToast(R.string.common_message_nfc_unsupported);
        } else {
            if (mNfcAdapter.isEnabled()) {
                Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            } else {
                if ("MainActivity".equalsIgnoreCase(this.getClass().getSimpleName()))
                    showToast(R.string.common_message_nfc_off);
            }
        }
    }

    private void enableNfc() {
        if (mNfcAdapter != null && mPendingIntent != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    private void disableNfc() {
        if (mNfcAdapter != null && mPendingIntent != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void readData(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages == null) return;
            for (Parcelable message : messages) showBlockChainProduct((NdefMessage) message);
        }
    }

    private void showBlockChainProduct(NdefMessage msg) {
        try {
            for (NdefRecord r : msg.getRecords()) {
                JSONObject p = new JSONObject(new String(r.getPayload()));  // (*) 중요!
                if (CustomLog.INSTANCE.getFlag())
                    CustomLog.INSTANCE.L("showBlockChainProduct", p.toString());
                if (p.getString(TAG_COMPANY).equals(COMPANY_NAME)) {
                    String id = p.getString(TAG_ID); // get productId
                    getProductData(id);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(@StringRes int id) {
        Toast.makeText(this, getString(id), Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    private void getProductData(String id) {
        ProductServer.getProductByList(id, (success, o) -> {
            if (success) {
                BlockChainHistoryActivity.startActivity(this, (ProductByList) o);
            }
        });
    }

    ////////////////////////////////////////////////


    @Override
    protected void onResume() {
        super.onResume();
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L(this.getClass().getSimpleName(),"onResume");
        if (!"SplashActivity".equalsIgnoreCase(this.getClass().getSimpleName())) enableNfc();
        /**
         * @author park jungho
         *
         * 메인으로 이동
         */
        if (!"MainActivity".equalsIgnoreCase(this.getClass().getSimpleName())){
            if(((BaseApplication)getApplicationContext()).getMoveToMain() !=null &&
                    ((BaseApplication)getApplicationContext()).getMoveToMain().isMoveToMain()){
                if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("BindActivity",this.getClass().getSimpleName(),"onResume finish");
                setResult(Flag.RequestCode.GO_TO_MAIN);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L(this.getClass().getSimpleName(),"onPause");
        if (!"SplashActivity".equalsIgnoreCase(this.getClass().getSimpleName())) disableNfc();
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) readData(intent);
    }


    //////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == Flag.RequestCode.GO_TO_MAIN) {
            if (!"MainActivity".equalsIgnoreCase(this.getClass().getSimpleName())){
                setResult(Flag.RequestCode.GO_TO_MAIN);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    public void clickCheck(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isClickAble = true;
            }
        },500);
    }

}
