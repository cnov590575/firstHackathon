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

        DataManager.init(new AndroidIOFactory(this));
        AllReactions.getAllReactions();
        AllReactions.getAllUserReactions();


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            DataManager.getInstance().readAll();
            try {
                File f = new File(getFilesDir(), "userReactions.csv");
                FileInputStream fis = new FileInputStream(f);
                byte[] buf = new byte[300];
                int read = fis.read(buf, 0, 300);
                fis.close();
                Log.d("Persistence", "userReactions.csv: " + new String(buf, 0, read));
            } catch (Exception e) {
                Log.e("Persistence", "Could not read userReactions: " + e.getMessage());
            }
            seedTestData();

            UserDAO.getInstance().register("TestUser", "Hunter2");
            StateManager.login("TestUser", "Hunter2");

            runOnUiThread(() -> {
                RecyclerView recycler = findViewById(R.id.recyclerViewPosts);
                PostAdapter adapter = new PostAdapter();
                recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        });
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

}