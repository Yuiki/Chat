package com.yuikibis.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created on 15/06/07.
 */
public class PrefActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
    }
}