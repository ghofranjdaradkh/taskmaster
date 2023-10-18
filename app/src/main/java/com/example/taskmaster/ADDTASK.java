package com.example.taskmaster;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ADDTASK extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        TextView addTask = findViewById(R.id.textBar);
        Toast toast = Toast.makeText(this, "submitted", LENGTH_SHORT);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ADDTASK.this, MainActivity.class);
                startActivity(intent1);

                toast.show();
            }
        });


        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ADDTASK.this, "Submitted!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}