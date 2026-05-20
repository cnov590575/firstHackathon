package com.example.comp2100miniproject;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;

import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    int size;
    public PostAdapter() {
        this.size = -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        public void display(Post post) {
            TextView postBody = view.findViewById(R.id.textViewPostName);
            TextView postAuthor = view.findViewById(R.id.textViewPostAuthor);

            // 1. Check if the post itself exists
            if (post == null) {
                if (postBody != null) postBody.setText("Loading...");
                return;
            }

            // 2. Set the topic safely
            if (postBody != null) postBody.setText(post.topic);

            // 3. Check if the User/Author exists
            if (postAuthor != null) {
                User author = UserDAO.getInstance().getByUUID(post.poster);
                postAuthor.setText(author != null ? author.username() : "Anonymous");
            }

            android.widget.ImageView profilePic = view.findViewById(R.id.msgProfilePic);
            if (profilePic != null && post.poster != null) {
                int[] profilePics = {R.drawable.profilepic1, R.drawable.profilepic2, R.drawable.profilepic3, R.drawable.profilepic4, R.drawable.profilepic5};
                int index = (post.poster.hashCode() & 0x7fffffff) % 5;
                profilePic.setImageResource(profilePics[index]);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_homescreen_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Post post = PostDAO.getInstance().getAtIndex(position);

        viewHolder.display(post);

        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostViewerActivity.class);
            intent.putExtra("parentUUID", post.getUUID());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (this.size == -1) {
            Iterator<Post> iterator = PostDAO.getInstance().getAll();
            this.size += 1;
            while (iterator.hasNext() && this.size < 500) {
                this.size += 1;
            }
        }
        return size;
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(int i, Message message);
    }
}
