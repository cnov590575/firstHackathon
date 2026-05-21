package com.example.comp2100miniproject;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dao.AllReactions;
import dao.UserDAO;
import dao.model.Message;
import dao.model.User;
import sorteddata.SortedData;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final List<Message> localDataSet;

    public MessageAdapter(SortedData<Message> dataSet) {
        localDataSet = new ArrayList<>();
        Iterator<Message> iterator = dataSet.getAll();
        while (iterator.hasNext()) {
            localDataSet.add(iterator.next());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        public void display(Message message) {
            TextView messageBody = view.findViewById(R.id.MessageContent);
            TextView messageAuthor = view.findViewById(R.id.Author);
            TextView messageTime = view.findViewById(R.id.Date);

            messageBody.setText(message.message());
            User author = UserDAO.getInstance().getByUUID(message.poster());
            messageAuthor.setText(author != null ? author.username() : "Anonymous");            messageTime.setText(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(new java.util.Date(message.timestamp()))); //

            android.widget.ImageView profilePic = view.findViewById(R.id.msgProfilePic);
            if (profilePic != null && message.poster() != null) {
                int[] profilePics = {R.drawable.profilepic1, R.drawable.profilepic2, R.drawable.profilepic3, R.drawable.profilepic4, R.drawable.profilepic5};
                int index = (message.poster().hashCode() & 0x7fffffff) % 5;
                profilePic.setImageResource(profilePics[index]);
            }

            // Update reaction counters in the fragment view to match actual counts
            try {
                userstate.MemberState state = (userstate.MemberState) userstate.StateManager.getState();
                User user = state.user;

                AllReactions.getAllUserReactions().put(user.getUUID(), (AllReactions.getAllUserReactions().get(user.getUUID())==null) ? new HashMap<>() : AllReactions.getAllUserReactions().get(user.getUUID()));
                AllReactions.getAllUserReactions().get(user.getUUID()).put(message.id(), (AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id())==null) ? new Boolean[]{false, false, false, false, false} : AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id()));
                int[] counts = AllReactions.postMsgReactions(message.id());
                android.widget.Button angryCounter = view.findViewById(R.id.angryCounter);
                angryCounter.setBackgroundTintList((AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id())[0]) ? ColorStateList.valueOf(Color.RED) : ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                android.widget.Button cryCounter = view.findViewById(R.id.cryCounter);
                cryCounter.setBackgroundTintList((AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id())[1]) ? ColorStateList.valueOf(Color.RED) : ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                android.widget.Button smileCounter = view.findViewById(R.id.smileCounter);
                smileCounter.setBackgroundTintList((AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id())[2]) ? ColorStateList.valueOf(Color.RED) : ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                android.widget.Button heartCounter = view.findViewById(R.id.heartCounter);
                heartCounter.setBackgroundTintList((AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id())[3]) ? ColorStateList.valueOf(Color.RED) : ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                android.widget.Button thumbsUpCounter = view.findViewById(R.id.thumbsUpCounter);
                thumbsUpCounter.setBackgroundTintList((AllReactions.getAllUserReactions().get(user.getUUID()).get(message.id())[4]) ? ColorStateList.valueOf(Color.RED) : ColorStateList.valueOf(Color.parseColor("#9E9E9E")));


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

                    if (viewToAnimate != null) {
                        View finalViewToAnimate = viewToAnimate;
                        finalViewToAnimate.animate().scaleX(1.3f).scaleY(1.3f).rotation(10f).setDuration(100).withEndAction(() -> {
                            finalViewToAnimate.animate().rotation(-10f).setDuration(100).withEndAction(() -> {
                                finalViewToAnimate.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(100).start();
                            }).start();
                        }).start();
                    }

                    if (reactionType != -1) {
                        if (AllReactions.react(user.getUUID(), message.id(), reactionType)) {
                            AllReactions.decrementReaction(message.id(), reactionType);
                            viewToColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                        } else {
                            AllReactions.incrementReaction(message.id(), reactionType);
                            viewToColor.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                        // Refresh counts
                        int[] updatedCounts = AllReactions.postMsgReactions(message.id());
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
                // If reactions cannot be fetched, keep placeholders as-is
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Message newMessage = localDataSet.get(position);
        viewHolder.display(newMessage);
        Button reactionButton = viewHolder.itemView.findViewById(R.id.reactions);
        reactionButton.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, newMessage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(int i, Message message);
    }

    //TODO: Callum look at this below
    public void showButtonPopup(View anchorView, Message message, User user) {
        LayoutInflater inflater = LayoutInflater.from(anchorView.getContext());;
        View popupView = inflater.inflate(R.layout.buttonpopup, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // Allows tapping outside to dismiss
        );

        Button btnA = popupView.findViewById(R.id.button);
        Button btnB = popupView.findViewById(R.id.button2);
        Button btnC = popupView.findViewById(R.id.button3);
        Button btnD = popupView.findViewById(R.id.button4);
        Button btnE = popupView.findViewById(R.id.button5);
        Button btnX = popupView.findViewById(R.id.button6);

        btnA.setText(String.valueOf(AllReactions.postMsgReactions(message.id())[0]));
        btnB.setText(String.valueOf(AllReactions.postMsgReactions(message.id())[1]));
        btnC.setText(String.valueOf(AllReactions.postMsgReactions(message.id())[2]));
        btnD.setText(String.valueOf(AllReactions.postMsgReactions(message.id())[3]));
        btnE.setText(String.valueOf(AllReactions.postMsgReactions(message.id())[4]));

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AllReactions.react(user.getUUID(), message.id(), 0)){
                    AllReactions.decrementReaction(message.id(), 0);
                }
                else {
                    AllReactions.incrementReaction(message.id(), 0);
                }
                popupWindow.dismiss();
            }
        });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AllReactions.react(user.getUUID(), message.id(), 1)){
                    AllReactions.decrementReaction(message.id(), 1);
                }
                else {
                    AllReactions.incrementReaction(message.id(), 1);
                }
                popupWindow.dismiss();
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AllReactions.react(user.getUUID(), message.id(), 2)){
                    AllReactions.decrementReaction(message.id(), 2);
                }
                else {
                    AllReactions.incrementReaction(message.id(), 2);
                }
                popupWindow.dismiss();
            }
        });

        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AllReactions.react(user.getUUID(), message.id(), 3)){
                    AllReactions.decrementReaction(message.id(), 3);
                }
                else {
                    AllReactions.incrementReaction(message.id(), 3);
                }
                popupWindow.dismiss();
            }
        });

        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AllReactions.react(user.getUUID(), message.id(), 4)){
                    AllReactions.decrementReaction(message.id(), 4);
                }
                else {
                    AllReactions.incrementReaction(message.id(), 4);
                }
                popupWindow.dismiss();
            }
        });

        btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(anchorView, 0, 10);
    }
}

