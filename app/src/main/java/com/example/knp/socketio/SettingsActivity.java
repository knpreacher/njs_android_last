package com.example.knp.socketio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by knp on 2/6/17.
 */

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    EditText etIPA;
    EditText etPN;
    String linkUrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        preferences = getSharedPreferences("mpref",MODE_PRIVATE);

        etIPA = (EditText)findViewById(R.id.etIPA);
        etPN = (EditText)findViewById(R.id.etPN);
        if(preferences.getString("ipa","")!=null && preferences.getString("pn","")!=null){
            etIPA.setText(preferences.getString("ipa",""));
            etPN.setText(preferences.getString("pn",""));
        }

    }

    @Override
    public void onBackPressed() {
        String ipa = etIPA.getText().toString();
        String pn = etPN.getText().toString();
        super.onBackPressed();
        linkUrl = "http://"+ipa+":"+pn;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ipa",ipa);
        editor.putString("pn",pn);
        editor.putString("linkUrl", linkUrl);
        editor.commit();
        Intent intent = getIntent();
        intent.putExtra("url", linkUrl);

    }
}
