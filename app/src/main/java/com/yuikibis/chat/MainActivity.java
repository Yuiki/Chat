package com.yuikibis.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String CHAT_SERVER_URL = "https://vivid-inferno-5397.firebaseio.com/";
    public static final String OP_NAME = "[お知らせ]";

    private String mUserName;
    private String mUserTrip;
    private Firebase mFirebase;
    private ChatListAdapter mChatListAdapter;
    private ValueEventListener mFirebaseConnectedListener;
    private EditText mChatColumn;

    private static Boolean isLogIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebase = new Firebase(CHAT_SERVER_URL).child("yuiki_chat_2");

        createUser();

        mChatColumn = (EditText) findViewById(R.id.chat_column);
        mChatColumn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String sendMessage = mChatColumn.getText().toString();
                    if (sendMessage.length() > 1000) {
                        Toast.makeText(MainActivity.this, "1000文字以上は投稿できません。\n現在:" + sendMessage.length() + "文字", Toast.LENGTH_SHORT).show();
                    }

                    boolean isSucceededTransmission = sendMessage(sendMessage);
                    if (isSucceededTransmission) {
                        mChatColumn.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        final ListView chatList = (ListView) findViewById(R.id.chat_list);
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView author = (TextView) view.findViewById(R.id.author);
                String replyName = author.getText().subSequence(0, (author.getText().length() - 2)).toString();
                if (!replyName.equals(OP_NAME)) {
                    mChatColumn.setText("@" + replyName + " " + mChatColumn.getText());
                    mChatColumn.setSelection(mChatColumn.getText().length());
                }
            }
        });

        mChatListAdapter = new ChatListAdapter(mFirebase.limitToLast(1000), R.layout.chat_message, this, mUserTrip);
        chatList.setAdapter(mChatListAdapter);

        mFirebaseConnectedListener = mFirebase.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();

                if (connected) {
                    userLogIn();
                    Toast.makeText(MainActivity.this, "接続しました", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "チャットサーバーに接続します", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        createUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        userLogOut();
        mChatListAdapter.dataClean();
    }

    private boolean createUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserTrip = preferences.getString("user_trip", null);
        if (mUserTrip == null) {
            Random rand = new Random();
            String randHex = Integer.toHexString(rand.nextInt(16777215));
            for (int i = randHex.length(); i < 6; i++) {
                randHex = "0" + randHex;
            }
            mUserTrip = "#" + randHex;
            preferences.edit().putString("user_trip", mUserTrip).apply();
        }
        mUserName = preferences.getString("user_name", "user");
        if (mUserName != null && mUserName.length() > 10 || mUserName != null && mUserName.matches("^\\s*$")) {
            mUserName = "user";
        }
        preferences.edit().putString("user_name", mUserName).apply();
        mUserName += mUserTrip;

        setTitle("Yuiki Chat - " + mUserName);
        return true;
    }

    private boolean userLogIn() {
        if (!isLogIn) {
            isLogIn = true;
            sendMessage(OP_NAME, mUserName + "が入室しました。");
            return true;
        }
        return false;
    }

    private boolean userLogOut() {
        if (isLogIn) {
            isLogIn = false;
            sendMessage(OP_NAME, mUserName + "が退室しました。");
            mFirebase.getRoot().child(".info/connected").removeEventListener(mFirebaseConnectedListener);
            return true;
        }
        return false;
    }

    private boolean sendMessage(String message) {
        return sendMessage(mUserName, message);
    }

    private boolean sendMessage(String author, String message) {
        if (message.equals("") || message.matches("^\\s*$")) {
            return false;
        }

        ChatBundle chatBundle = new ChatBundle(author, message, getNowTime());
        mFirebase.push().setValue(chatBundle);
        return true;
    }

    private String getNowTime() {
        long t = System.currentTimeMillis();
        return android.text.format.DateFormat.format("MM/dd kk:mm", t).toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
