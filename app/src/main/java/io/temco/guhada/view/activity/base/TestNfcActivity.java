package io.temco.guhada.view.activity.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.ProductByList;
import io.temco.guhada.data.server.BlockChainServer;
import io.temco.guhada.data.server.ProductServer;
import io.temco.guhada.view.activity.BlockChainHistoryActivity;

public class TestNfcActivity extends AppCompatActivity {

    // -------- LOCAL VALUE --------
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    //
    private boolean mIsRead = false;
    private final String TAG_COMPANY = "TAG_COMPANY";
    private final String TAG_ID = "TAG_ID";
    private final String COMPANY_NAME = "GUHADA";
    //
    private String mTestId = "12529"; // productID
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mIsRead = false;

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableNfc();
    }

    @Override
    protected void onPause() {
        disableNfc();
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) return;
        if (mIsRead) {
            readData(intent);
        } else {
            writeData(intent);
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void init() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // 미지원 기기
            showMessage("NFC 미지원");
        } else {
            Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            if (mNfcAdapter.isEnabled()) {
                showMessage("NFC ON!");
            } else {
                showMessage("NFC OFF!");
            }
        }

        //
        // getProductData(mTestId);
    }

    /**
     * 기기에서 nfc 사용 설정
     */
    private void enableNfc() {
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    /**
     * 기기에서 nfc 미사용 설정
     */
    private void disableNfc() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * NFC READ
     * @param intent
     */
    private void readData(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages == null) return;
            for (Parcelable message : messages) showMessage((NdefMessage) message);
        }
    }

    private void showMessage(NdefMessage msg) {
        try {
            for (NdefRecord r : msg.getRecords()) {
                JSONObject p = new JSONObject(new String(r.getPayload()));  // (*) 중요!
                if (p.getString(TAG_COMPANY).equals(COMPANY_NAME)) {
                    String id = p.getString(TAG_ID); // get productId
                    CommonUtil.debug(id);
                    // Server
                    getProductData(id);
                    getBlockChainData(id);
                }
            }
            // if (Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)) {
            // if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
            // Uri u = record.toUri();
        } catch (JSONException e) {
            CommonUtil.debug(e.getMessage());
        }
    }

    /**
     * NFC WRITE/INSERT
     * 이번 시연 버전에서는 미사용
     * @param intent
     */
    private void writeData(Intent intent) {
        Tag t = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // Test
        writeTag(getTextAsNdef(mTestId), t);
    }

    /**
     * NFC 객체 생성
     * 현재 임의로 TAG_COMPANY, TAG_ID 추가; 추후 규격 변경 시 반영 필요
     * @param id product id
     * @return
     */
    private NdefMessage getTextAsNdef(String id) {
        try {
            JSONObject p = new JSONObject();
            p.put(TAG_COMPANY, COMPANY_NAME);
            p.put(TAG_ID, id);
            byte[] textBytes = p.toString().getBytes();
            NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                    "text/plain".getBytes(),
                    new byte[]{},
                    textBytes);
            return new NdefMessage(new NdefRecord[]{textRecord});
        } catch (JSONException e) {
            CommonUtil.debug(e.getMessage());
            return null;
        }
    }

    private void writeTag(NdefMessage message, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                writeNdefMessage(ndef, message);
            } else {
                writeNdefFormatable(NdefFormatable.get(tag), message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("write Failed!!!!!");
        }
    }

    /**
     * @param ndef NFC 규격
     * @param message
     * @throws IOException [line 212] writeNdefMessage() 호출 이전에 nfc 인식이 안될 경우 발생; 1초 이상 태깅 필요
     * @throws FormatException
     */
    private void writeNdefMessage(Ndef ndef, NdefMessage message) throws IOException, FormatException {
        int size = message.toByteArray().length;
        if (!ndef.isConnected()) {
            ndef.connect();
        }
        // ndef.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
//        CommonUtil.debug("" + size);
//        CommonUtil.debug("" + ndef.getMaxSize());
        if (!ndef.isWritable()) showMessage("can not write NFC tag");
        if (ndef.getMaxSize() < size) showMessage("NFC tag size too large");
        ndef.writeNdefMessage(message);
        ndef.close();
        showMessage("NFC tag is writted");
    }

    private void writeNdefFormatable(NdefFormatable format, NdefMessage message) throws IOException, FormatException {
        if (format != null) {
            if (!format.isConnected()) {
                format.connect();
            }
            format.format(message);
            format.close();
            showMessage("NFC tag is formatting");
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    /**
     * product 정보 가져오기 API 호출
     * 페이지 상단부
     * @param id product id
     */
    private void getProductData(String id) {
        ProductServer.getProductByList(id, (success, o) -> {
            if (success) {
                BlockChainHistoryActivity.startActivity(this, (ProductByList) o);
            } else {

            }
        });
    }

    private void getBlockChainData(String id) {
        BlockChainServer.getTransactionData(Integer.valueOf(id), (success, o) -> {
            if (success) {

            } else {

            }
        });
    }

    ////////////////////////////////////////////////
}
