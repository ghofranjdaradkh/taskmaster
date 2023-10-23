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
import android.widget.TextView;

import com.example.taskmaster.Adapter.ViewAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

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

        //TODO: step 2-2: Make some data items
        List<Product> products = new ArrayList<>();

        products.add(new Product("Pens"));
        products.add(new Product("Pencils"));
        products.add(new Product("Binders"));
        products.add(new Product("Mice"));
        products.add(new Product("Keyboard"));
        products.add(new Product("Flash Drives"));
        products.add(new Product("Monitors"));
        products.add(new Product("Printer"));
        products.add(new Product("Mouse"));
        products.add(new Product("HeadSet"));
        products.add(new Product("Tape"));
        products.add(new Product("HeadSet Holder"));
        products.add(new Product("HeadSet Holder"));
        products.add(new Product("HeadSet Holder"));
        products.add(new Product("HeadSet Holder"));

        //TODO: step 1-5: create and attach the RecyclerView.Adapter
        //TODO: step 2-3: Hand in data items
        //TODO: step 3-2: Hand in the Activity context
        ViewAdapter adapter = new ViewAdapter(products, this);
        productListRecyclerView.setAdapter(adapter);


    }
}

}