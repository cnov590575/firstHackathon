package com.example.comp2100miniproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import dao.UserDAO;
import dao.model.Message;
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

        viewHolder.itemView.setOnClickListener(v -> {
            onClickListener.onClick(position, newMessage);
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
}

