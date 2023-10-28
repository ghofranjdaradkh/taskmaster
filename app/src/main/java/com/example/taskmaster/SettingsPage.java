package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

//import com.example.taskmaster.Adapter.ViewAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SettingsPage extends AppCompatActivity {
public static final String USERNAME_TAG="username";
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);



        ImageView imageView=findViewById(R.id.arrowImageSetting);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(SettingsPage.this, MainActivity.class);
                startActivity(intentArrow);

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


