package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.taskmaster.Adapter.ViewAdapter;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.Adapter.ViewAdapter;
import com.example.taskmaster.R;
//import com.example.taskmaster.dataBase.TaskdataBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String Main_ID_TAG = "task ID";
    public static final String DATABASE_NAME = "NAME";
    //    TaskdataBase taskdataBase;
    List<Task> taskList = new ArrayList<>();
    ViewAdapter adapter;
    public static final String TAG = "AddTaskActivity";
    String TeamName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setRecyclerViewList();
        setUpLoginAndLogoutButton();
        setupButton();

        String emptyFilename= "emptyTestFileName";
        File emptyFile = new File(getApplicationContext().getFilesDir(), emptyFilename);

        try {
            //createbufferWriter
            BufferedWriter emptyFileBufferedWriter= new BufferedWriter(new FileWriter(emptyFile));

            emptyFileBufferedWriter.append("Some text here from ghofran\nAnother lib from ghofran");
//to make sure every thing is save use "close"
            emptyFileBufferedWriter.close();
        }catch (IOException ioe){
            Log.i(TAG, "could not write locally with filename: "+ emptyFilename);
        }

        String emptyFileS3Key = "someFileOnS3.txt";
        Amplify.Storage.uploadFile(
                emptyFileS3Key,
                emptyFile,
                success ->
                {
                    Log.i(TAG, "S3 upload succeeded and the Key is: " + success.getKey());
                },
                failure ->
                {
                    Log.i(TAG, "S3 upload failed! " + failure.getMessage());
                }
        );


// ================================================
        Team team1= new Team.Builder()
                .name("TEAM1").build();

        Team team2= new Team.Builder()
                .name("TEAM2").build();
        Team team3= new Team.Builder()
                .name("TEAM3").build();
        Amplify.API.mutate(
                ModelMutation.create(team1),
                sucsess->Log.i(TAG,"Successfully team"),
                failure->Log.i(TAG,"failure team"+failure.getMessage())
        );

        Amplify.API.mutate(
                ModelMutation.create(team2),
                sucsess->Log.i(TAG,"Successfully team"),
                failure->Log.i(TAG,"failure team" +failure.getMessage())
        );
        Amplify.API.mutate(
                ModelMutation.create(team3),
                sucsess->Log.i(TAG,"Successfully team"),
                failure->Log.i(TAG,"failure team"+failure.getMessage())
        );

//========================================

    }

    @SuppressLint({"NotifyDataSetChanged", "StringFormatInvalid"})
    @Override

    protected void onResume() {


        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String userName = preferences.getString(SettingsPage.USERNAME_TAG, "No name");

        ((TextView) findViewById(R.id.textView10)).setText(getString(R.string.your_user_name, userName));


        String teamName = preferences.getString(SettingsPage.TEAM_TAG, "No team");

        ((TextView) findViewById(R.id.teamMain)).setText(getString(R.string.your_team_names, teamName));

        TeamName = ((TextView) findViewById(R.id.teamMain)).getText().toString();

//        setRecyclerViewList();



        Amplify.API.query(
                ModelQuery.list(Task.class//TASK.TEAM_PERSON.eq(TeamName)
                ), success ->
                {
                    Log.i(TAG, "Read tasks recycleview successfully");
                    taskList.clear();
                    if (success.getData() != null) {
                        for (Task databaseProduct : success.getData()) {
                            taskList.add(databaseProduct);
                            Log.d("TeamName", "setUpTaskRecyclerView() returned: " + databaseProduct.getName());
                        }
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                        });
                        Log.e(TAG, "Success response data is null");
                    }
                }, failure -> Log.i(TAG, "Did not read products successfully"));




        AuthUser authUser = Amplify.Auth.getCurrentUser();
        String username="";
        if (authUser == null){
            Button loginButton = (Button) findViewById(R.id.TaskAppLoginButton);
            loginButton.setVisibility(View.VISIBLE);
            Button logoutButton = (Button) findViewById(R.id.TaskAppLogoutButton);
            logoutButton.setVisibility(View.INVISIBLE);
        }else{
            username = authUser.getUsername();
            Log.i(TAG, "Username is: "+ username);
            Button loginButton = (Button) findViewById(R.id.TaskAppLoginButton);
            loginButton.setVisibility(View.INVISIBLE);
            Button logoutButton = (Button) findViewById(R.id.TaskAppLogoutButton);
            logoutButton.setVisibility(View.VISIBLE);

            String username2 = username; // ugly way for lambda hack
            Amplify.Auth.fetchUserAttributes(
                    success ->
                    {
                        Log.i(TAG, "Fetch user attributes succeeded for username: "+username2);
                        for (AuthUserAttribute userAttribute: success){
                            if(userAttribute.getKey().getKeyString().equals("email")){
                                String userEmail = userAttribute.getValue();
                                runOnUiThread(() ->
                                {
                                    ((TextView)findViewById(R.id.textView10)).setText(userEmail);
                                });
                            }
                        }
                    },
                    failure ->
                    {
                        Log.i(TAG, "Fetch user attributes failed: "+failure.toString());
                    }
            );
        }

    }




    @SuppressLint("NotifyDataSetChanged")
    private void setRecyclerViewList() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewId);
        //set the LayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Log.i("TAG", "setUpTaskRecyclerView1111: " + TeamName);
        // Read from DynamoDB
//        Amplify.API.query(
//                ModelQuery.list(Task.class//TASK.TEAM_PERSON.eq(TeamName)
//                ), success ->
//                {
//                    Log.i(TAG, "Read tasks recycleview successfully");
//                    taskList.clear();
//                    if (success.getData() != null) {
//                        for (Task databaseProduct : success.getData()) {
//                            taskList.add(databaseProduct);
//                            Log.d("TeamName", "setUpTaskRecyclerView() returned: " + databaseProduct.getName());
//                        }
//                        runOnUiThread(() -> {
//                            adapter.notifyDataSetChanged();
//                        });
//                        Log.e(TAG, "Success response data is null");
//                    }
//                }, failure -> Log.i(TAG, "Did not read products successfully"));


        adapter = new ViewAdapter(taskList, this);
        recyclerView.setAdapter(adapter);


    }


    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        taskList = new ArrayList<>();
    }


    private void setUpLoginAndLogoutButton() {
        Button loginButton = (Button) findViewById(R.id.TaskAppLoginButton);
        loginButton.setOnClickListener(v ->
        {
            Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLogInIntent);
        });

        Button logoutButton = (Button) findViewById(R.id.TaskAppLogoutButton);
        logoutButton.setOnClickListener(v ->
        {
            Amplify.Auth.signOut(
                    () ->
                    {
                        Log.i(TAG, "Logout succeeded");
                        runOnUiThread(() ->
                        {
                            ((TextView) findViewById(R.id.textView10)).setText("");
                        });
                        Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(goToLogInIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Logout failed");
                        runOnUiThread(() ->
                        {
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_LONG);
                        });
                    }
            );
        });

    }

public  void setupButton (){

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
            Intent intentsub1 = new Intent(MainActivity.this, EditTask.class);
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
            Intent intentsub2 = new Intent(MainActivity.this, EditTask.class);
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
            Intent intentsub3 = new Intent(MainActivity.this, EditTask.class);
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
}


}





