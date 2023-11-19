package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.example.taskmaster.Adapter.ViewAdapter;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.Adapter.ViewAdapter;
import com.example.taskmaster.R;
//import com.example.taskmaster.dataBase.TaskdataBase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String DATABASE_NAME="NAME";
    //    TaskdataBase taskdataBase;
    List<Task> taskList = new ArrayList<>();
    ViewAdapter adapter;
    public static final String TAG = "AddTaskActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setRecyclerViewList();




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

        // ================================================
//        Team team1= new Team.Builder()
//                .name("TEAM1").build();
//
//        Team team2= new Team.Builder()
//                .name("TEAM2").build();
//        Team team3= new Team.Builder()
//                .name("TEAM3").build();
//        Amplify.API.mutate(
//                ModelMutation.create(team1),
//                sucsess->Log.i(TAG,"Successfully team"),
//                failure->Log.i(TAG,"failure team"+failure.getMessage())
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team2),
//                sucsess->Log.i(TAG,"Successfully team"),
//                failure->Log.i(TAG,"failure team" +failure.getMessage())
//        );
//        Amplify.API.mutate(
//                ModelMutation.create(team3),
//                sucsess->Log.i(TAG,"Successfully team"),
//                failure->Log.i(TAG,"failure team"+failure.getMessage())
//        );

//================================================
    }

    @SuppressLint({"NotifyDataSetChanged", "StringFormatInvalid"})
    @Override

    protected void onResume() {
        super.onResume();

        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String username = preferences.getString(SettingsPage.USERNAME_TAG, "No name");

        ((TextView) findViewById(R.id.textView10)).setText(getString(R.string.your_user_name, username));



String teamName=preferences.getString(SettingsPage.TEAM_TAG,"No team");

        ((TextView) findViewById(R.id.teamMain)).setText(getString(R.string.your_team_names, teamName));



setRecyclerViewList();
    }







    private void setRecyclerViewList(){

        RecyclerView recyclerView =(RecyclerView) findViewById(R.id.recyclerViewId);
        //set the LayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Read from DynamoDB
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG, "Read tasks successfully");
                    taskList.clear();
                    for (Task databaseProduct : success.getData()){
                        taskList.add(databaseProduct);
                    }
                    runOnUiThread(() ->{
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Did not read products successfully")
        );



        adapter = new ViewAdapter(taskList, this);
        recyclerView.setAdapter(adapter);


    }


    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        taskList = new ArrayList<>();}

}






