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
        RecyclerView list = findViewById(R.id.test_list);
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

                case 2: // Main
                    startActivity(MainActivity.class);
                    break;

                case 3:
                    startActivity(LoginActivity.class);
                    break;
            }
        });
        list.setAdapter(adapter);
    }

    private List<String> getScreenList() {
        List<String> list = new ArrayList<>();

        list.add("CUSTOM_VIEW");

        list.add("SPLASH");
        list.add("MAIN");

        list.add("LOGIN");

        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");

        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");

        return list;
    }

    private void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
