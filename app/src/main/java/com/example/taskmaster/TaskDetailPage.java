package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

public class TaskDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_page);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("taskMaster");
        setSupportActionBar(toolbar);
    }
        TextView addTask = findViewById(R.id.textBar);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSE = new Intent(TaskDetailPage.this, MainActivity.class);
                startActivity(intentSE);
            }
        });
    }