package com.example.comp2100miniproject;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dao.AllReactions;
import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Post;
import dao.model.User;
import persistentdata.DataManager;
import persistentdata.io.AndroidIOFactory;
import userstate.MemberState;
import userstate.StateManager;
import userstate.UserState;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // ✅ Init persistence first — readAll() clears DAOs before loading
        DataManager.init(new AndroidIOFactory(this));
        DataManager.getInstance().readAll();
        try {
            for (String name : new String[]{"users", "posts", "messages"}) {
                File f = new File(getFilesDir(), name + ".csv");
                long size = f.length();
                Log.d("Persistence", name + ".csv size: " + size + " bytes");
            }
        } catch (Exception e) {
            Log.e("Persistence", "Could not check file sizes: " + e.getMessage());
        }
        try {
            File f = new File(getFilesDir(), "users.csv");
            FileInputStream fis = new FileInputStream(f);
            byte[] buf = new byte[200];
            int read = fis.read(buf, 0, 200);
            fis.close();
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < read; i++) {
                hex.append(String.format("%02x ", buf[i]));
            }
            Log.d("Persistence", "users.csv raw bytes: " + hex);
        } catch (Exception e) {
            Log.e("Persistence", "Could not read raw bytes: " + e.getMessage());
        }
        Log.d("Persistence", "After readAll — users exist: " + UserDAO.getInstance().getAll().hasNext());
        Log.d("Persistence", "After readAll — posts exist: " + PostDAO.getInstance().getAll().hasNext());

        AllReactions.getAllReactions();
        AllReactions.getAllUserReactions();

        seedTestData(); // no-op after first launch if data loaded from disk


        UserDAO.getInstance().register("TestUser", "Hunter2");
        StateManager.login("TestUser", "Hunter2");

        RecyclerView recycler = findViewById(R.id.recyclerViewPosts);
        PostAdapter adapter = new PostAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void clearPersistedData() {
        for (String name : new String[]{"users", "posts", "messages", "reactions", "userReactions"}) {
            File f = new File(getFilesDir(), name + ".csv");
            if (f.exists()) {
                f.delete();
                Log.d("Persistence", "Deleted " + name + ".csv");
            }
        }
    }
    private void seedTestData() {
        boolean usersExist = UserDAO.getInstance().getAll().hasNext();
        boolean postsExist = PostDAO.getInstance().getAll().hasNext();
        Log.d("Persistence", "seedTestData — usersExist: " + usersExist + " postsExist: " + postsExist);

        if (usersExist) {
            Log.d("Persistence", "Data loaded from disk ✅ — skipping seed");
            return;
        }
        Log.d("Persistence", "No data found — seeding fresh data");
        RandomContentGenerator.populateRandomData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Called when app goes to background, user switches apps, locks screen, etc.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> DataManager.getInstance().writeAll());
    }

}