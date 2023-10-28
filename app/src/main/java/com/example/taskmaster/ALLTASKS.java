package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ALLTASKS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alltasks);


        TextView addTask = findViewById(R.id.textBar2);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ALLTASKS.this, MainActivity.class);
                startActivity(intent1);
            }
        });
        ImageView imageView=findViewById(R.id.arrowImage3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(ALLTASKS.this, MainActivity.class);
                startActivity(intentArrow);

            }
        });
    }
}