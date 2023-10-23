package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button addTask = findViewById(R.id.ADDTASK);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, ADDTASK.class);
                startActivity(intent1);
            }
        });

        Button allTask = findViewById(R.id.ALLTASKS);
        allTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, ALLTASKS.class);
                startActivity(intent2);
            }
        });


      Button  taskButton1 = findViewById(R.id.buttonsub1);

      taskButton1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intentsub1=new Intent(MainActivity.this, TaskDetailPage.class);
              startActivity(intentsub1);
          }
      });

        Button taskButton2 = findViewById(R.id.buttonsub2);
taskButton2.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intentsub2=new Intent(MainActivity.this, TaskDetailPage.class);
            startActivity(intentsub2);
        }
    });

        Button taskButton3 = findViewById(R.id.buttonsub3);
        taskButton3.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View view) {
        Intent intentsub3=new Intent(MainActivity.this, TaskDetailPage.class);
        startActivity(intentsub3);
    }
});
Button setting =findViewById(R.id.settingID);
setting.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intentsetting=new Intent(MainActivity.this, SettingsPage.class);
        startActivity(intentsetting);
    }

});

    }

    RecyclerView recyclerView = findViewById(R.id.recyclerView);

 RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
recyclerView.setLayoutManager(layoutManager);

