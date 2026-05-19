package com.example.comp2100miniproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import dao.AllReactions;
import dao.UserDAO;
import dao.model.Message;
import dao.model.User;
import sorteddata.SortedData;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Message[] localDataSet;
    private int length;
    public MessageAdapter(SortedData<Message> dataSet) {
        int size = 0;
        ArrayList<Message> list = new ArrayList<>();
        Iterator<Message> iterator = dataSet.getAll();
        while (iterator.hasNext()) {
            size += 1;
            list.add(iterator.next());
        }
        localDataSet = list.toArray(new Message[0]);
        length = size;
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
            messageAuthor.setText(UserDAO.getInstance().getByUUID(message.poster()).username());
            messageTime.setText(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(message.timestamp()))); //
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
        Message newMessage = localDataSet[position];
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
        return length;
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

