package com.yuikibis.chat;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 15/06/07.
 */
public class ChatListAdapter extends BaseAdapter {
    private Query mQuery;
    private int mLayout;
    private String mUserTrip;
    private LayoutInflater mInflater;
    private List<ChatBundle> mChatList;
    private Map<String, ChatBundle> mChatBundleMap;
    private ChildEventListener mListener;
    private Resources mRes;

    private static class ViewHolder {
        TextView authorText;
        TextView messageText;
        TextView timeText;

        public ViewHolder(View view) {
            this.authorText = (TextView) view.findViewById(R.id.author);
            this.messageText = (TextView) view.findViewById(R.id.message);
            this.timeText = (TextView) view.findViewById(R.id.time);
        }
    }

    public ChatListAdapter(Query query, int layout, Activity activity, String userTrip) {
        this.mRes = activity.getResources();
        this.mQuery = query;
        this.mLayout = layout;
        this.mUserTrip = userTrip;
        mInflater = activity.getLayoutInflater();
        mChatList = new ArrayList<>();
        mChatBundleMap = new HashMap<>();
        mListener = mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatBundle chatBundle = dataSnapshot.getValue(ChatBundle.class);
                mChatBundleMap.put(dataSnapshot.getKey(), chatBundle);

                if (s == null) {
                    mChatList.add(0, chatBundle);
                } else {
                    ChatBundle previousChatBundle = mChatBundleMap.get(s);
                    int previousIndex = mChatList.indexOf(previousChatBundle);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mChatList.size()) {
                        mChatList.add(chatBundle);
                    } else {
                        mChatList.add(nextIndex, chatBundle);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String chatBundleKey = dataSnapshot.getKey();
                ChatBundle oldChatBundle = mChatBundleMap.get(chatBundleKey);
                ChatBundle newChatBundle = dataSnapshot.getValue(ChatBundle.class);
                int index = mChatList.indexOf(oldChatBundle);

                mChatList.set(index, newChatBundle);
                mChatBundleMap.put(chatBundleKey, newChatBundle);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String chatBundleKey = dataSnapshot.getKey();
                ChatBundle oldChatBundle = mChatBundleMap.get(chatBundleKey);
                mChatList.remove(oldChatBundle);
                mChatBundleMap.remove(chatBundleKey);

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                String chatBundleKey = dataSnapshot.getKey();
                ChatBundle oldChatBundle = mChatBundleMap.get(chatBundleKey);
                ChatBundle newChatBundle = dataSnapshot.getValue(ChatBundle.class);
                int index = mChatList.indexOf(oldChatBundle);
                mChatList.remove(index);
                if (s == null) {
                    mChatList.add(0, newChatBundle);
                } else {
                    ChatBundle previousChatBundle = mChatBundleMap.get(s);
                    int previousIndex = mChatList.indexOf(previousChatBundle);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mChatList.size()) {
                        mChatList.add(newChatBundle);
                    } else {
                        mChatList.add(nextIndex, newChatBundle);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void dataClean() {
        mQuery.removeEventListener(mListener);
        mChatList.clear();
        mChatBundleMap.clear();
    }

    @Override
    public int getCount() {
        return mChatList.size();
    }

    @Override
    public Object getItem(int i) {
        return mChatList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

            convertView.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.authorText.setTextColor(mRes.getColor(R.color.primary_text_default_material_light));
        }

        ChatBundle chatBundle = mChatList.get(position);

        String author = chatBundle.getAuthorName();
        if (!author.equals(MainActivity.OP_NAME)) {
            String authorTrip = author.substring(author.length() - 7, author.length());
            if (authorTrip.equals(mUserTrip)) {
                convertView.setBackgroundColor(Color.parseColor("#EAF6FD"));
            }
            holder.authorText.setTextColor(Color.parseColor(authorTrip));
        } else {
            convertView.setBackgroundColor(mRes.getColor(R.color.background_material_light));
            holder.authorText.setTextColor(Color.GRAY);
        }
        holder.authorText.setText(author + ": ");
        holder.messageText.setText(chatBundle.getMessage());
        holder.timeText.setText(chatBundle.getTime());

        return convertView;
    }
}
