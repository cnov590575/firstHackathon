package com.example.comp2100miniproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

import dao.AllReactions;
import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Post;
import dao.model.User;
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

        AllReactions.getAllReactions();
        AllReactions.getAllUserReactions();

        UserDAO.getInstance().register("TestUser", "Hunter2");
        StateManager.login("TestUser", "Hunter2");

        RandomContentGenerator.populateRandomData();

        RecyclerView recycler = findViewById(R.id.recyclerViewPosts);
        PostAdapter adapter = new PostAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}