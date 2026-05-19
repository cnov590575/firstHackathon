package com.example.comp2100miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Post;
import dao.PostDAO;
import com.example.comp2100miniproject.MessageAdapter;

import java.io.Serializable;
import java.util.UUID;


public class PostViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Intent intent = getIntent();
//        String choice = intent.getStringExtra("parentUUID");
//        UUID parentuuid = UUID.fromString(choice);
//        Post post = PostDAO.getInstance().getByUUID(parentuuid);


        Serializable serializableUuid = getIntent().getSerializableExtra("parentUUID");

        if (serializableUuid instanceof UUID) {
            UUID parentId = (UUID) serializableUuid;
            Post post = PostDAO.getInstance().getByUUID(parentId);

            if (post != null) {
                TextView titleText = findViewById(R.id.textViewPostName);
                TextView authorText = findViewById(R.id.textViewPostAuthor);
                titleText.setText(post.topic);
                authorText.setText(UserDAO.getInstance().getByUUID(post.poster).username());

                RecyclerView recycler = findViewById(R.id.recyclerViewReplies);
                MessageAdapter adapter = new MessageAdapter(post.messages);
                recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                // If the post wasn't found, close the activity to prevent a crash
                finish();
            }
        } else {
            finish();
        }


        Button button = findViewById(R.id.Exit);
        button.setOnClickListener(v -> finish());

    }


}