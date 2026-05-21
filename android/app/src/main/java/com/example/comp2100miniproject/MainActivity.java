package com.example.comp2100miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import persistentdata.DataManager;
import userstate.StateManager;
public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!StateManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recycler = findViewById(R.id.recyclerViewPosts);
        PostAdapter adapter = new PostAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recycler.setAdapter(adapter);

        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(v -> {
            DataManager.getInstance().writeAll(); // save before logging out
            StateManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recycler != null && recycler.getAdapter() != null) {
            recycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> DataManager.getInstance().writeAll());
    }
}