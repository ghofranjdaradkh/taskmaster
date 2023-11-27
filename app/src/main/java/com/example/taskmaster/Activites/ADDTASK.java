package com.example.taskmaster.Activites;

import static android.widget.Toast.LENGTH_SHORT;

import static java.util.Arrays.stream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
//import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.datastore.generated.model.TaskState;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;
//import com.example.taskmaster.dataBase.TaskdataBase;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ADDTASK extends AppCompatActivity {
    //TaskdataBase taskdataBase;
    List<Task> TASKS = null;
    Spinner teamsSpinner = null;
    Spinner Statespinner = null;
    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    public static final String TAG = "AddTaskActivity";
    public static final String DATABASE_NAME = "NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        teamFuture=new CompletableFuture<>();
        setUpSpinner();
        saveButton();

        Toast toast = Toast.makeText(this, "submitted", LENGTH_SHORT);
        TextView addTask = findViewById(R.id.textBar);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ADDTASK.this, MainActivity.class);
                startActivity(intent1);

                toast.show();


            }

        });

        ImageView imageView = findViewById(R.id.arrowImage2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(ADDTASK.this, MainActivity.class);
                startActivity(intentArrow);
            }
        });

    }


    public void setUpSpinner() {

        teamsSpinner = findViewById(R.id.addteam);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Team Successfully");
                    ArrayList<String> teamName = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    if (success.getData() != null) {
                        for (Team team : success.getData()) {
                            teams.add(team);
                            teamName.add(team.getName());
                            Log.d(TAG, "setupSpinners() returned: " + team.getName());
                        }
                        teamFuture.complete(teams);
                        runOnUiThread(() -> {
                            teamsSpinner.setAdapter(new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    teamName
                            ));
                        });
                    } else {
                        Log.e(TAG, "Success response data is null");
                    }
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.e(TAG, "Failed to read teams successfully: " + failure.toString());
                }
        );


        Statespinner = (Spinner) findViewById(R.id.spinnerlsitforState);
        Statespinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskState.values()
        ));
    }


    public void saveButton() {

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            String name = ((EditText) findViewById(R.id.taskTitle)).getText().toString();
            String description = ((EditText) findViewById(R.id.editTextdescription)).getText().toString();
            String selectedTeamString = teamsSpinner.getSelectedItem().toString();
            List<Team> teams = null;
            try {
                teams = teamFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, " InterruptedException while getting teams");
            } catch (ExecutionException ee) {
                Log.e(TAG, " ExecutionException while getting teams");
            }

            assert teams != null;
            Team selectedTeam = teams.stream().filter(c -> c.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

            Task newTask = Task.builder()
                    .name(name)
                    .description(description)
                    .state((TaskState) Statespinner.getSelectedItem())
                    .teamPerson(selectedTeam)
                    .build();

            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    successResponse -> {
                        Log.i(TAG, "AddTaskActivity.onCreate(): made a task successfully");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ADDTASK.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    },
                    failureResponse -> {
                        Log.e(TAG, "AddTaskActivity.onCreate(): failed with this response" + failureResponse);
                        Toast.makeText(this, "Failed to add task. Please try again.", Toast.LENGTH_SHORT).show();
                    }
            );
            Snackbar.make(findViewById(R.id.addactivity), "Task saved!", Snackbar.LENGTH_SHORT).show();

        });
    }}