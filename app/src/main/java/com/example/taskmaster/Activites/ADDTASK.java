package com.example.taskmaster.Activites;

import static android.widget.Toast.LENGTH_SHORT;

import static com.example.taskmaster.Activites.MainActivity.DATABASE_NAME;

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
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.TaskState;
//import com.example.taskmaster.dataBase.TaskdataBase;


import java.util.List;

public class ADDTASK extends AppCompatActivity {
    //TaskdataBase taskdataBase;
    List<Task> TASKS = null;
    public static final String TAG = "AddTaskActivity";
    public static final String DATABASE_NAME = "NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);


        //create and setup the database
//        taskdataBase = Room.databaseBuilder(
//                        getApplicationContext(),
//                        TaskdataBase.class,
//                        DATABASE_NAME)
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();
//        TASKS= taskdataBase.TaskDAO().findAll();


        Spinner spinnerlist = findViewById(R.id.spinnerlsitforState);
        spinnerlist.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskState.values()
        ));


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


        Button submitButton = findViewById(R.id.submitButton);
        Intent goToMainActivity = new Intent(this, MainActivity.class);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ADDTASK.this, "Submitted!", Toast.LENGTH_SHORT).show();

                // Update the add task to add to DynamoDB
                // 1. Retrieve task details
                String taskTitle = ((EditText) findViewById(R.id.taskTitle)).getText().toString();
                String taskDescription = ((EditText) findViewById(R.id.editTextdescription)).getText().toString();
                String spinnerValue = spinnerlist.getSelectedItem().toString();


                String enumValue = spinnerValue.toUpperCase();

                // 2. Create a Task object using Amplify DataStore model
                Task task = Task.builder()
                        .name(taskTitle)
                        .description(taskDescription)
                        .state(com.amplifyframework.datastore.generated.model.TaskState.valueOf(enumValue))
                        .build();

                // 3. Use Amplify to mutate (create) the Task in DynamoDB
                Amplify.API.mutate(
                        ModelMutation.create(task),
                        successResponse -> {
                            Log.i(TAG, "Task saved successfully");
                            runOnUiThread(() -> {
                                Toast.makeText(ADDTASK.this, "Task saved successfully", Toast.LENGTH_SHORT).show();
                            });
                            startActivity(goToMainActivity);
                        },
                        failureResponse -> {
                            Log.e(TAG, "Failed to save task: " + failureResponse.toString());
                            runOnUiThread(() -> {
                                Toast.makeText(ADDTASK.this, "Failed to save task", Toast.LENGTH_SHORT).show();
                            });
                        }
                );

                ImageView imageView = findViewById(R.id.arrowImage2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentArrow = new Intent(ADDTASK.this, MainActivity.class);
                        startActivity(intentArrow);
                    }
                });
            }
        });
    }

}
