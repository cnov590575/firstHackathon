package com.example.comp2100miniproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.PostDAO;
import userstate.MemberState;
import userstate.StateManager;

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
                adapter.setOnClickListener(new MessageAdapter.OnClickListener() {
                    @Override
                    public void onClick(int i, Message message) {
                        // 1. Get the specific row view from the RecyclerView layout manager
                        View rowView = recycler.getLayoutManager().findViewByPosition(i);

                        if (rowView != null) {
                            // 2. Look for the reaction anchor inside that specific row
                            View anchorButton = rowView.findViewById(R.id.reactions);

                            // Fallback to the whole row layout if R.id.reactions isn't found
                            View finalAnchor = (anchorButton != null) ? anchorButton : rowView;

                            // 3. Immediately display the popup window!
                            adapter.showButtonPopup(finalAnchor, message, ((MemberState) StateManager.getState()).user);
                        }
                    }
                });
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