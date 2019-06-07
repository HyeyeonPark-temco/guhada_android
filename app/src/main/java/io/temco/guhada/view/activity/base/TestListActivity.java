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
import io.temco.guhada.view.activity.CategorySubActivity;
import io.temco.guhada.view.activity.FindAccountActivity;
import io.temco.guhada.view.activity.JoinActivity;
import io.temco.guhada.view.activity.LoginActivity;
import io.temco.guhada.view.activity.MainActivity;
import io.temco.guhada.view.activity.ProductDetailActivity;
import io.temco.guhada.view.activity.SplashActivity;
import io.temco.guhada.view.activity.TermsActivity;
import io.temco.guhada.view.activity.VerifyPhoneActivity;
import io.temco.guhada.view.activity.WriteClaimActivity;
import io.temco.guhada.view.adapter.base.TestListAdapter;

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
                    // startActivity(TestCustomViewActivity.class);
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
                    startActivity(ProductDetailActivity.class);
                    break;

                case 9: // Product Detail
                    startActivity(WriteClaimActivity.class);
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

        list.add(""); // 10
        list.add(""); // 11
        list.add(""); // 12

        return list;
    }

    private void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
