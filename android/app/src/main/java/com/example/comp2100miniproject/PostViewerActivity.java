package com.example.comp2100miniproject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;

import java.util.ArrayList;
import java.util.Iterator;
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

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> finish());

        TextView titleText = findViewById(R.id.postTitle);
        TextView authorText = findViewById(R.id.postAuthor);

        String postId = getIntent().getStringExtra("postId");

        if (postId != null) {
            UUID id = UUID.fromString(postId);

            PostDAO dao = PostDAO.getInstance();
            Post selectedPost = null;

            Iterator<Post> it = dao.getAll();
            while (it.hasNext()) {
                Post p = it.next();
                if (p.getUUID().equals(id)) {
                    selectedPost = p;
                    break;
                }
            }

            if (selectedPost != null) {
                titleText.setText(selectedPost.topic);
                authorText.setText("User" + selectedPost.poster.toString().substring(0, 5));
            }
        }

        RecyclerView recycler = findViewById(R.id.replyRecycler);

        ArrayList<Message> messages = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                messages.add(new Message(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        "E17: A novel written by KID. It is the second entry in the series; " +
                                "it is preceded by N7, and followed by R11, the spin-off 12R: " +
                                "RC18"));
            }
            if (i % 3 == 1) {
                messages.add(new Message(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        "C is a Japanese novel by Key and released on April 28. " +
                                "While both of Key's first two previous works, K and A, had been released first for adults" +
                                " and then censored for the younger market, C was specifically made for all ages."));
            }
            if (i % 3 == 2) {
                messages.add(new Message(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        "WA2 is a trilogy of novels written by Leaf, " +
                                "and is arguably the sequel to Leaf's earlier work, WA. " +
                                "The first part of the trilogy, named WA2: Intro, was released on March 26, 2010."));
            }
        }


        MessageAdapter adapter = new MessageAdapter(messages);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private ArrayList<Message> localDataSet;

        public MessageAdapter(ArrayList<Message> dataSet) {
            localDataSet = dataSet;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final View view;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
            }

            public void display(Message message) {
                TextView replyAuthor = view.findViewById(R.id.replyAuthor);
                TextView replyContent = view.findViewById(R.id.replyContent);
                TextView timeStamp = view.findViewById(R.id.timeStamp);

                replyAuthor.setText("User" + message.poster().toString().substring(0, 5));
                replyContent.setText(message.message());
                timeStamp.setText("now");
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.fragment_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.display(localDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }
}