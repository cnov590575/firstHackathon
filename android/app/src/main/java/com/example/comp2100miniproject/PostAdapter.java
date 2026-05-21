package com.example.comp2100miniproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import dao.AllReactions;
import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import userstate.MemberState;
import userstate.StateManager;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    // Load all posts into a list once — avoids O(n) getAtIndex on every bind
    private final List<Post> posts;

    public PostAdapter() {
        this.posts = new ArrayList<>();
        Iterator<Post> it = PostDAO.getInstance().getAll();
        while (it.hasNext()) posts.add(it.next());
    }

    // Cache all views in ViewHolder — avoids findViewById on every bind
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView postBody, postAuthor, postDate;
        final ImageView profilePic, angryEmoji, cryEmoji, smileEmoji, heartEmoji, thumbsUpEmoji;
        final Button angryCounter, cryCounter, smileCounter, heartCounter, thumbsUpCounter;

        public ViewHolder(View view) {
            super(view);
            postBody       = view.findViewById(R.id.textViewPostName);
            postAuthor     = view.findViewById(R.id.textViewPostAuthor);
            postDate       = view.findViewById(R.id.textViewPostDate);
            profilePic     = view.findViewById(R.id.msgProfilePic);
            angryEmoji     = view.findViewById(R.id.angryEmoji);
            cryEmoji       = view.findViewById(R.id.cryEmoji);
            smileEmoji     = view.findViewById(R.id.smileEmoji);
            heartEmoji     = view.findViewById(R.id.heartEmoji);
            thumbsUpEmoji  = view.findViewById(R.id.thumbsUpEmoji);
            angryCounter   = view.findViewById(R.id.angryCounter);
            cryCounter     = view.findViewById(R.id.cryCounter);
            smileCounter   = view.findViewById(R.id.smileCounter);
            heartCounter   = view.findViewById(R.id.heartCounter);
            thumbsUpCounter = view.findViewById(R.id.thumbsUpCounter);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_homescreen_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        Post post = posts.get(position); // O(1) instead of O(n)

        if (post == null) {
            vh.postBody.setText("Loading...");
            return;
        }

        vh.postBody.setText(post.topic);

        User author = UserDAO.getInstance().getByUUID(post.poster);
        vh.postAuthor.setText(author != null ? author.username() : "Anonymous");

        Iterator<Message> it = post.messages.getAll();
        if (it.hasNext()) {
            long timestamp = it.next().timestamp();
            vh.postDate.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(new Date(timestamp)));
        } else {
            vh.postDate.setText("No date");
        }

        if (post.poster != null) {
            int[] profilePics = {R.drawable.profilepic1, R.drawable.profilepic2, R.drawable.profilepic3,
                    R.drawable.profilepic4, R.drawable.profilepic5};
            vh.profilePic.setImageResource(profilePics[(post.poster.hashCode() & 0x7fffffff) % 5]);
        }

        // Reactions
        try {
            MemberState state = (MemberState) StateManager.getState();
            Log.d("Persistence", "current user UUID: " + state.user.getUUID());
            Log.d("Persistence", "userReactions keys: " + AllReactions.getAllUserReactions().keySet());
            User user = state.user;


            HashMap<UUID, Boolean[]> userMap = AllReactions.getAllUserReactions().get(user.getUUID());
            Boolean[] existing = userMap != null ? userMap.get(post.getUUID()) : null;
            Log.d("Persistence", "before computeIfAbsent: " + java.util.Arrays.toString(existing));
            AllReactions.getAllUserReactions()
                    .computeIfAbsent(user.getUUID(), k -> new HashMap<>())
                    .computeIfAbsent(post.getUUID(), k -> new Boolean[]{false, false, false, false, false});


            Boolean[] userReacted = AllReactions.getAllUserReactions().get(user.getUUID()).get(post.getUUID());
           // Log.d("Persistence", "post " + post.getUUID() + " userReacted: " + java.util.Arrays.toString(userReacted));

            int[] counts = AllReactions.postMsgReactions(post.getUUID());

            Button[] counters = {vh.angryCounter, vh.cryCounter, vh.smileCounter, vh.heartCounter, vh.thumbsUpCounter};
            View[] emojis    = {vh.angryEmoji,   vh.cryEmoji,   vh.smileEmoji,   vh.heartEmoji,   vh.thumbsUpEmoji};

            for (int i = 0; i < 5; i++) {
                counters[i].setText(String.valueOf(counts[i]));
                counters[i].setBackgroundTintList(ColorStateList.valueOf(
                        userReacted[i] ? Color.RED : Color.parseColor("#9E9E9E")));
            }

            // Use arrays in lambda to avoid creating a new listener object per reaction button
            for (int i = 0; i < 5; i++) {
                final int reactionType = i;
                final Button counter = counters[i];
                final View emoji = emojis[i];
                View.OnClickListener listener = v -> {
                    emoji.animate().scaleX(1.3f).scaleY(1.3f).rotation(10f).setDuration(100)
                            .withEndAction(() -> emoji.animate().rotation(-10f).setDuration(100)
                                    .withEndAction(() -> emoji.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(100).start()).start()).start();

                    if (AllReactions.react(user.getUUID(), post.getUUID(), reactionType)) {
                        AllReactions.decrementReaction(post.getUUID(), reactionType);
                        counter.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                    } else {
                        AllReactions.incrementReaction(post.getUUID(), reactionType);
                        counter.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    }
                    Boolean[] we = AllReactions.getAllUserReactions().get(user.getUUID()).get(post.getUUID());
                    Log.d("Persistence", "after react: " + java.util.Arrays.toString(we));
                    int[] updated = AllReactions.postMsgReactions(post.getUUID());
                    for (int j = 0; j < 5; j++) counters[j].setText(String.valueOf(updated[j]));
                };
                counters[i].setOnClickListener(listener);
                emojis[i].setOnClickListener(listener);
            }
        } catch (Exception ignored) {}

        vh.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostViewerActivity.class);
            intent.putExtra("parentUUID", post.getUUID()); // UUID not String
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}