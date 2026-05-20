package com.example.comp2100miniproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
                TextView dateText = findViewById(R.id.textViewPostDate);
                
                titleText.setText(post.topic);
                authorText.setText(UserDAO.getInstance().getByUUID(post.poster).username());
                
                java.util.Iterator<dao.model.Message> it = post.messages.getAll();
                if (it.hasNext()) {
                    long timestamp = it.next().timestamp();
                    dateText.setText(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(new java.util.Date(timestamp)));
                } else {
                    dateText.setText("No date");
                }

                android.widget.ImageView profilePic = findViewById(R.id.msgProfilePic);
                if (profilePic != null && post.poster != null) {
                    int[] profilePics = {R.drawable.profilepic1, R.drawable.profilepic2, R.drawable.profilepic3, R.drawable.profilepic4, R.drawable.profilepic5};
                    int index = (post.poster.hashCode() & 0x7fffffff) % 5;
                    profilePic.setImageResource(profilePics[index]);
                }

                // Setup reactions for the main post
                try {
                    userstate.MemberState state = (userstate.MemberState) userstate.StateManager.getState();
                    dao.model.User user = state.user;

                    int[] counts = dao.AllReactions.postMsgReactions(post.getUUID());
                    android.widget.Button angryCounter = findViewById(R.id.angryCounter);
                    android.widget.Button cryCounter = findViewById(R.id.cryCounter);
                    android.widget.Button smileCounter = findViewById(R.id.smileCounter);
                    android.widget.Button heartCounter = findViewById(R.id.heartCounter);
                    android.widget.Button thumbsUpCounter = findViewById(R.id.thumbsUpCounter);

                    android.widget.ImageView angryEmoji = findViewById(R.id.angryEmoji);
                    android.widget.ImageView cryEmoji = findViewById(R.id.cryEmoji);
                    android.widget.ImageView smileEmoji = findViewById(R.id.smileEmoji);
                    android.widget.ImageView heartEmoji = findViewById(R.id.heartEmoji);
                    android.widget.ImageView thumbsUpEmoji = findViewById(R.id.thumbsUpEmoji);

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
                        if (viewToAnimate != null) {
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

                RecyclerView recycler = findViewById(R.id.recyclerViewReplies);
                MessageAdapter adapter = new MessageAdapter(post.messages);
                recycler.setLayoutManager(new LinearLayoutManager(this));
                recycler.setHasFixedSize(true);
                recycler.setAdapter(adapter);
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