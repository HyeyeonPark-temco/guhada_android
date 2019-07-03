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
import io.temco.guhada.view.activity.CategorySubActivity;
import io.temco.guhada.view.activity.CustomViewActivity;
import io.temco.guhada.view.activity.FindAccountActivity;
import io.temco.guhada.view.activity.JoinActivity;
import io.temco.guhada.view.activity.LoginActivity;
import io.temco.guhada.view.activity.MainActivity;
import io.temco.guhada.view.activity.PaymentActivity;
import io.temco.guhada.view.activity.PaymentResultActivity;
import io.temco.guhada.view.activity.PaymentWebViewActivity;
import io.temco.guhada.view.activity.ProductDetailActivity;
import io.temco.guhada.view.activity.SplashActivity;
import io.temco.guhada.view.activity.TempLogoutActivity;
import io.temco.guhada.view.activity.TermsActivity;
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
                    Intent intent = new Intent(TestListActivity.this, ProductDetailActivity.class);
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
        list.add(""); // 16
        list.add(""); // 17
        list.add(""); // 18
        return list;
    }

    private void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
