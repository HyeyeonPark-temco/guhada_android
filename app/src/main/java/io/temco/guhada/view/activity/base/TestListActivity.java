package io.temco.guhada.view.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.PrintWriterPrinter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.view.activity.CartActivity;
import io.temco.guhada.view.activity.CustomViewActivity;
import io.temco.guhada.view.activity.DeliveryDetailActivity;
import io.temco.guhada.view.activity.FindAccountActivity;
import io.temco.guhada.view.activity.JoinActivity;
import io.temco.guhada.view.activity.LoginActivity;
import io.temco.guhada.view.activity.LuckyDrawJoinActivity;
import io.temco.guhada.view.activity.MainActivity;
import io.temco.guhada.view.activity.PaymentActivity;
import io.temco.guhada.view.activity.PaymentResultActivity;
import io.temco.guhada.view.activity.PaymentWebViewActivity;
import io.temco.guhada.view.activity.ProductFragmentDetailActivity;
import io.temco.guhada.view.activity.SearchZipWebViewActivity;
import io.temco.guhada.view.activity.SellerInfoActivity;
import io.temco.guhada.view.activity.ShippingTrackingActivity;
import io.temco.guhada.view.activity.SplashActivity;
import io.temco.guhada.view.activity.SuccessCancelOrderActivity;
import io.temco.guhada.view.activity.SuccessRequestRefundActivity;
import io.temco.guhada.view.activity.TempLogoutActivity;
import io.temco.guhada.view.activity.TermsActivity;
import io.temco.guhada.view.activity.VerifyActivity;
import io.temco.guhada.view.activity.VerifyPhoneActivity;
import io.temco.guhada.view.activity.WriteClaimActivity;
import io.temco.guhada.view.adapter.base.TestListAdapter;
import retrofit2.http.Path;

public class TestListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_list);

        initList();
    }

    private void initList() {
        RecyclerView list = findViewById(R.id.list_contents);
        // Option
        list.setHasFixedSize(true);
        // Layout
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        list.setLayoutManager(manager);
        // Adapter
        TestListAdapter adapter = new TestListAdapter();
        adapter.setItem(getScreenList());
        adapter.setOnTestItemListener(position -> {
            switch (position) {
                case 0: // Custom View
                    startActivity(CustomViewActivity.class);
                    break;

                case 1: // Splash
                    startActivity(SplashActivity.class);
                    break;


                case 2: // Login
                    startActivity(LoginActivity.class);
                    break;

                case 3: // Join
                    startActivity(JoinActivity.class);
                    break;
                case 4: // Find Account
                    startActivity(FindAccountActivity.class);
                    break;

                case 5: // Terms
                    startActivity(TermsActivity.class);
                    break;

                case 6: // Verify Phone
                    startActivity(VerifyPhoneActivity.class);
                    break;


                case 7: // Main
                    startActivity(MainActivity.class);
                    break;

                case 8: // Product Detail
                    Intent intent = new Intent(TestListActivity.this, ProductFragmentDetailActivity.class);
                    intent.putExtra(Info.INTENT_DEAL_ID, Integer.parseInt(getResources().getString(R.string.temp_productId)));
                    startActivity(intent);
                    break;

                case 9: // Product Detail
                    startActivity(WriteClaimActivity.class);
                    break;

                case 10: // Logout (only clear access Token in SP)
                    startActivity(TempLogoutActivity.class);
                    break;

                case 11: // Payment
                    startActivity(PaymentActivity.class);
                    break;

                case 12: // Payment Result
                    startActivity(PaymentResultActivity.class);
                    break;

                case 13: // Payment WebView
                    startActivity(PaymentWebViewActivity.class);
                    break;

                case 14: // Test Read NFC
                    startActivity(new Intent(this, TestNfcActivity.class).putExtra("isRead", true));
                    break;
                case 15: // Test Write NFC
                    startActivity(new Intent(this, TestNfcActivity.class).putExtra("isRead", false));
                    break;

                case 16: // Search Zip WebView
                    startActivity(SearchZipWebViewActivity.class);
                    break;

                case 17:
                    startActivity(CartActivity.class);
                    break;
                case 18:
                    startActivity(DeliveryDetailActivity.class);
                    break;
                case 19:
                    startActivity(SuccessCancelOrderActivity.class);
                    break;
                case 20:
                    startActivity(SellerInfoActivity.class);
                    break;
                case 21:
                    startActivity(VerifyActivity.class);
                    break;
                case 22:
                    startActivity(ShippingTrackingActivity.class);
                    break;
                case 23:
                    startActivity(LuckyDrawJoinActivity.class);
                    break;
                case 24:
                    startActivity(SuccessRequestRefundActivity.class);
                    break;
            }
        });
        list.setAdapter(adapter);
    }

    private List<String> getScreenList() {
        List<String> list = new ArrayList<>();

        list.add("CUSTOM_VIEW"); // 0
        list.add("SPLASH"); // 1

        list.add("LOGIN"); // 2
        list.add("JOIN"); //3
        list.add("FIND_ACCOUNT"); // 4
        list.add("TERMS"); // 5
        list.add("VERIFY_PHONE"); // 6

        list.add("MAIN"); // 7
        list.add("PRODUCT_DETAIL"); // 8
        list.add("WRITE_CLAIM"); // 9

        list.add("##### LOGOUT #####"); // 10
        list.add("PAYMENT"); // 11
        list.add("PAYMENT_RESULT"); // 12
        list.add("PAYMENT_WEBVIEW"); // 13

        list.add("Test Read NFC "); // 14
        list.add("Test Write NFC "); // 15
        list.add("SEARCH_ZIP_WEBVIEW"); // 16
        list.add("CART"); // 17
        list.add("DELIVERY_DETAIL"); // 18
        list.add("SUCCESS_CANCEL_ORDER"); // 19
        list.add("SELELR_INFO"); // 20
        list.add("VERIFY"); // 21
        list.add("SHIPPING_TRACKING"); // 22
        list.add("LUCKY DRAW JOIN"); // 23
        list.add("SUCCESS REQUEST REFUND"); // 24
        return list;
    }

    private void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
