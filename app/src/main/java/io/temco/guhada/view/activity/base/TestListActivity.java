package io.temco.guhada.view.activity.base;

import android.content.Intent;
import android.os.Bundle;

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
import io.temco.guhada.view.activity.SplashActivity;
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

                case 5: // Main
                    startActivity(MainActivity.class);
                    break;

                case 6: // Category Sub
                    startActivity(CategorySubActivity.class);
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

        list.add("MAIN"); // 5
        list.add("CATEGORY_SUB"); // 6

        list.add(""); // 7
        list.add(""); // 8

        list.add(""); // 9
        list.add(""); // 10
        list.add(""); // 11
        list.add(""); // 12

        return list;
    }

    private void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
