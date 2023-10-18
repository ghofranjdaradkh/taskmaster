package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class SettingsPage extends AppCompatActivity {
public static final String USERNAME_TAG="username";
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);


        TextView addTask = findViewById(R.id.textBar2);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SettingsPage.this, MainActivity.class);
                startActivity(intent1);
            }
        });


                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Button Savebutton=findViewById(R.id.Savebutton);
        Savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor PreferenceEditor=sharedPreferences.edit();
                EditText usernameeditor=findViewById(R.id.editTextUserName);
                String usernameString=usernameeditor.getText().toString();

PreferenceEditor.putString(USERNAME_TAG,usernameString);
PreferenceEditor.apply();


                Snackbar.make(findViewById(R.id.settingPage),"UserNameSaves",Snackbar.LENGTH_SHORT).show();
            }
        });


    }
}