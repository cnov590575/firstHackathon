package com.example.comp2100miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.example.comp2100miniproject.dao.model.Post;

import java.util.ArrayList;
import java.util.UUID;

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

        PostDAO dao = PostDAO.getInstance();
        if (!dao.getAll().hasNext()) {
            for (int i = 0; i < 20; i++) {
                if (i % 3 == 0) dao.add(new Post(UUID.randomUUID(), UUID.randomUUID(), "Books of the Year!"));
                if (i % 3 == 1) dao.add(new Post(UUID.randomUUID(), UUID.randomUUID(), "Novel Recommendations"));
                if (i % 3 == 2) dao.add(new Post(UUID.randomUUID(), UUID.randomUUID(), "Authors of Choice"));
            }
        }

        RecyclerView recycler = findViewById(R.id.postRecycler);

        ArrayList<Post> posts = new ArrayList<>();
        dao.getAll().forEachRemaining(posts::add);

        PostAdapter adapter = new PostAdapter(posts);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

        private ArrayList<Post> posts;

        public PostAdapter(ArrayList<Post> posts) {
            this.posts = posts;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            Button title;

            public ViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.postTitleMain);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Post post = posts.get(position);

            holder.title.setText(post.topic);

            holder.title.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PostViewerActivity.class);
                intent.putExtra("postId", post.getUUID().toString());
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }
}