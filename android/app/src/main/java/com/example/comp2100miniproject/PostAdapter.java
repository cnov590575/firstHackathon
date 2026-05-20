package com.example.comp2100miniproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
            TextView postDate = view.findViewById(R.id.textViewPostDate);

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
            
            if (postDate != null) {
                java.util.Iterator<dao.model.Message> it = post.messages.getAll();
                if (it.hasNext()) {
                    long timestamp = it.next().timestamp();
                    postDate.setText(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(new java.util.Date(timestamp)));
                } else {
                    postDate.setText("No date");
                }
            }

            android.widget.ImageView profilePic = view.findViewById(R.id.msgProfilePic);
            if (profilePic != null && post.poster != null) {
                int[] profilePics = {R.drawable.profilepic1, R.drawable.profilepic2, R.drawable.profilepic3, R.drawable.profilepic4, R.drawable.profilepic5};
                int index = (post.poster.hashCode() & 0x7fffffff) % 5;
                profilePic.setImageResource(profilePics[index]);
            }

            // Reaction UI Logic
            try {
                userstate.MemberState state = (userstate.MemberState) userstate.StateManager.getState();
                User user = state.user;

                int[] counts = dao.AllReactions.postMsgReactions(post.getUUID());
                android.widget.Button angryCounter = view.findViewById(R.id.angryCounter);
                android.widget.Button cryCounter = view.findViewById(R.id.cryCounter);
                android.widget.Button smileCounter = view.findViewById(R.id.smileCounter);
                android.widget.Button heartCounter = view.findViewById(R.id.heartCounter);
                android.widget.Button thumbsUpCounter = view.findViewById(R.id.thumbsUpCounter);

                android.widget.ImageView angryEmoji = view.findViewById(R.id.angryEmoji);
                android.widget.ImageView cryEmoji = view.findViewById(R.id.cryEmoji);
                android.widget.ImageView smileEmoji = view.findViewById(R.id.smileEmoji);
                android.widget.ImageView heartEmoji = view.findViewById(R.id.heartEmoji);
                android.widget.ImageView thumbsUpEmoji = view.findViewById(R.id.thumbsUpEmoji);

                if (counts != null && counts.length >= 5) {
                    angryCounter.setText(String.valueOf(counts[0]));
                    cryCounter.setText(String.valueOf(counts[1]));
                    smileCounter.setText(String.valueOf(counts[2]));
                    heartCounter.setText(String.valueOf(counts[3]));
                    thumbsUpCounter.setText(String.valueOf(counts[4]));
                }

                View.OnClickListener onReactionClick = v -> {
                    int reactionType = -1;
                    View viewToAnimate = null;
                    View viewToColor = null;
                    if (v == angryEmoji || v == angryCounter) { reactionType = 0; viewToAnimate = angryEmoji; viewToColor = angryCounter; }
                    else if (v == cryEmoji || v == cryCounter) { reactionType = 1; viewToAnimate = cryEmoji; viewToColor = cryCounter;}
                    else if (v == smileEmoji || v == smileCounter) { reactionType = 2; viewToAnimate = smileEmoji; viewToColor = smileCounter;}
                    else if (v == heartEmoji || v == heartCounter) { reactionType = 3; viewToAnimate = heartEmoji; viewToColor = heartCounter;}
                    else if (v == thumbsUpEmoji || v == thumbsUpCounter) { reactionType = 4; viewToAnimate = thumbsUpEmoji; viewToColor = thumbsUpCounter;}

                    if(viewToAnimate != null) {
                        View finalViewToAnimate = viewToAnimate;
                        finalViewToAnimate.animate().scaleX(1.3f).scaleY(1.3f).rotation(10f).setDuration(100).withEndAction(() -> {
                            finalViewToAnimate.animate().rotation(-10f).setDuration(100).withEndAction(() -> {
                                finalViewToAnimate.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(100).start();
                            }).start();
                        }).start();
                    }

                    if (reactionType != -1) {
                        if (dao.AllReactions.react(user.getUUID(), post.getUUID(), reactionType)) {
                            dao.AllReactions.decrementReaction(post.getUUID(), reactionType);
                            viewToColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                        } else {
                            dao.AllReactions.incrementReaction(post.getUUID(), reactionType);
                            viewToColor.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                        int[] updatedCounts = dao.AllReactions.postMsgReactions(post.getUUID());
                        if (updatedCounts != null && updatedCounts.length >= 5) {
                            angryCounter.setText(String.valueOf(updatedCounts[0]));
                            cryCounter.setText(String.valueOf(updatedCounts[1]));
                            smileCounter.setText(String.valueOf(updatedCounts[2]));
                            heartCounter.setText(String.valueOf(updatedCounts[3]));
                            thumbsUpCounter.setText(String.valueOf(updatedCounts[4]));
                        }
                    }
                };

                angryEmoji.setOnClickListener(onReactionClick);
                angryCounter.setOnClickListener(onReactionClick);
                cryEmoji.setOnClickListener(onReactionClick);
                cryCounter.setOnClickListener(onReactionClick);
                smileEmoji.setOnClickListener(onReactionClick);
                smileCounter.setOnClickListener(onReactionClick);
                heartEmoji.setOnClickListener(onReactionClick);
                heartCounter.setOnClickListener(onReactionClick);
                thumbsUpEmoji.setOnClickListener(onReactionClick);
                thumbsUpCounter.setOnClickListener(onReactionClick);

            } catch (Exception ignored) {
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
