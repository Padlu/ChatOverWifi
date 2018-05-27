package com.example.abhishekpadalkar.chatoverwifi;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abhishekpadalkar on 4/19/18.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    public ArrayList<Message> thisChat;

    public MessageAdapter(Context context, int resource, ArrayList<Message> thisChat){
        super(context, resource, thisChat);

        this.thisChat = new ArrayList<>();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_list_item, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.device_name);

        TextView msgTextView = (TextView) convertView.findViewById(R.id.group_message);

        TextView timeTextView = (TextView) convertView.findViewById(R.id.time);

        final Message message = getItem(position);

        nameTextView.setText(message.getName());

        msgTextView.setText(message.getMsg());

        timeTextView.setText(message.getTimestamp());

        return convertView;
    }
}
