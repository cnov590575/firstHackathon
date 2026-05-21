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

import userstate.StateManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Defensive guard: if we somehow land here without a session
        // (e.g. process recreation cleared state, or a bad deep-link),
        // bounce back to the login screen instead of crashing the adapters,
        // which cast the current state to MemberState.
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

        // NOTE: AllReactions init, RandomContentGenerator.populateRandomData(),
        // and the test-user login that previously lived here have moved to
        // LoginActivity, which is now the launcher. By the time we reach this
        // activity, a user is authenticated and data is populated.

        RecyclerView recycler = findViewById(R.id.recyclerViewPosts);
        PostAdapter adapter = new PostAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(v -> {
            StateManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            // Clear the back stack so pressing Back from the login screen
            // doesn't pop us back into a now-stale MainActivity.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}