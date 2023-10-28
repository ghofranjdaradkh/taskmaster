package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.example.taskmaster.Adapter.ViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
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


        Button taskButton1 = findViewById(R.id.buttonsub1);
        String buttonTitle1 = taskButton1.getText().toString();
        taskButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentsub1 = new Intent(MainActivity.this, TaskDetailPage.class);
                intentsub1.putExtra("buttonTitle", buttonTitle1);
                intentsub1.putExtra("description", "Lorem Ipsum Description Goes Here");
                startActivity(intentsub1);
            }
        });

        Button taskButton2 = findViewById(R.id.buttonsub2);
        taskButton2.setOnClickListener(new View.OnClickListener() {
            String buttonTitle2 = taskButton2.getText().toString();
            @Override
            public void onClick(View view) {
                Intent intentsub2 = new Intent(MainActivity.this, TaskDetailPage.class);
                intentsub2.putExtra("buttonTitle", buttonTitle2);
                intentsub2.putExtra("description", "Lorem Ipsum Description Goes Here");
                startActivity(intentsub2);

            }
        });

        Button taskButton3 = findViewById(R.id.buttonsub3);
        String buttonTitle3 = taskButton3.getText().toString();
        taskButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentsub3 = new Intent(MainActivity.this, TaskDetailPage.class);
                intentsub3.putExtra("buttonTitle", buttonTitle3);
                intentsub3.putExtra("description", "Lorem Ipsum Description Goes Here");
                startActivity(intentsub3);
            }
        });
        Button setting = findViewById(R.id.settingID);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentsetting = new Intent(MainActivity.this, SettingsPage.class);
                startActivity(intentsetting);

            }

        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
        @Override

        protected void onResume() {
            super.onResume();

            String username = preferences.getString(SettingsPage.USERNAME_TAG, "No name");

            ((TextView)findViewById(R.id.textView10)).setText(getString(R.string.your_user_name, username));
        }






//    setUpOrderButton();
//    RecyclerView recyclerView = findViewById(R.id.recyclerView);
//    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//    List<Task> taskList = new ArrayList<>();
//
//        taskList.add(new void Task("Task 1", "Description for Task 1",TaskState.NEW));
//        taskList.add(new void Task("Task 2", "Description for Task 2",TaskState.ASSIGNED));
//        taskList.add(new void Task("Task 3", "Description for Task 3",TaskState.IN_PROGRESS));
//        taskList.add(new void Task("Task 4", "Description for Task 4",TaskState.NEW));
//        taskList.add(new void Task("Task 5", "Description for Task 5",TaskState.COMPLETED));
//
//    ViewAdapter adapter = new ViewAdapter(taskList, this);
//        recyclerView.setAdapter(adapter);
}
