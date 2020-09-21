package com.ahsailabs.socketiochat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by ahmad s on 21/09/20.
 */
public class ChatBoxAdapter  extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {
    private List<Message> messageList;
    private String nickName;
    private final int VIEW_TYPE_ME = 0;
    private final int VIEW_TYPE_OTHERS = 1;

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;
        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            message = (TextView) view.findViewById(R.id.message);
        }
    }

    public ChatBoxAdapter(List<Message> messagesList, String nickName) {
        this.messageList = messagesList;
        this.nickName = nickName;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if(message.getNickname().equals(nickName)){
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHERS;
        }
    }

    @Override
    public ChatBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType==VIEW_TYPE_ME?R.layout.item_me: R.layout.item_other, parent, false);
        return new ChatBoxAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatBoxAdapter.MyViewHolder holder, final int position) {
        //binding the data from our ArrayList of object to the item_other.xml using the viewholder
        Message m = messageList.get(position);
        if(nickName.equals(m.getNickname())){
            holder.nickname.setTextColor(Color.RED);
        } else {
            holder.nickname.setTextColor(Color.BLACK);
        }
        holder.nickname.setText(m.getNickname()+ " : ");
        holder.message.setText(m.getMessage() );
    }
}
