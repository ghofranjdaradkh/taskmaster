package com.example.taskmaster.Activites;

import static android.widget.Toast.LENGTH_SHORT;

import static com.example.taskmaster.Activites.MainActivity.DATABASE_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.TaskState;
//import com.example.taskmaster.dataBase.TaskdataBase;
import com.example.taskmaster.model.Task;

import java.util.List;

public class ADDTASK extends AppCompatActivity {
//TaskdataBase taskdataBase;
    List<Task> TASKS=null;
    public static final String DATABASE_NAME="NAME";
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



        Spinner spinnerlist=findViewById(R.id.spinnerlsitforState);
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
        Intent goToMainActivity = new Intent(this,MainActivity.class);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ADDTASK.this, "Submitted!", Toast.LENGTH_SHORT).show();

                Task task = new Task(
                        ((EditText) findViewById(R.id.taskTitle)).getText().toString(),
                        ((EditText) findViewById(R.id.editTextdescription)).getText().toString(),
                        TaskState.fromString(spinnerlist.getSelectedItem().toString()));
//                taskdataBase.TaskDAO().insertTask(task);
                toast.show();
                startActivity(goToMainActivity);
            }
        });


        ImageView imageView=findViewById(R.id.arrowImage2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(ADDTASK.this, MainActivity.class);
                startActivity(intentArrow);

            }
        });
    }


}