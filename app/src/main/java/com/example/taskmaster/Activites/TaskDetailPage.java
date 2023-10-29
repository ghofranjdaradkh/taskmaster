package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        ImageView imageView = findViewById(R.id.arrowImage);

        Intent intent = getIntent();
        if (intent != null) {
            String buttonTitle = intent.getStringExtra("buttonTitle");
            String description = intent.getStringExtra("description");

            if (buttonTitle != null && description != null) {
                titleTextView.setText(buttonTitle);
                descriptionTextView.setText(description);
            } else {
                Log.e("TaskDetailPage", "buttonTitle or description is null");
            }
        } else {
            Log.e("TaskDetailPage", "Intent is null");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(TaskDetailPage.this, MainActivity.class);
                startActivity(intentArrow);
            }
        });
    }
}