package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taskmaster.R;

public class TaskDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_page);



        TextView titleTextView = findViewById(R.id.titleTextView);


        Intent intent = getIntent();
        String buttonTitle = intent.getStringExtra("buttonTitle");


        titleTextView.setText(buttonTitle);

        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        Intent intentDes = getIntent();
        String description = intentDes.getStringExtra("description");
        descriptionTextView.setText(description);

        ImageView imageView=findViewById(R.id.arrowImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(TaskDetailPage.this, MainActivity.class);
            startActivity(intentArrow);

            }
        });

//        Toolbar toolbar = findViewById(R.id.toolbar2);
//        toolbar.setTitle("taskMaster");
//        setSupportActionBar(toolbar);
    }
//        TextView addTask = findViewById(R.id.textBar);
//        addTask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentSE = new Intent(TaskDetailPage.this, MainActivity.class);
//                startActivity(intentSE);
//            }
//        });
    }