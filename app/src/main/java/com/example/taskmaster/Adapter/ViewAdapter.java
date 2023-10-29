package com.example.taskmaster.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Activites.TaskDetailPage;
import com.example.taskmaster.R; // Import your R class
import com.example.taskmaster.TaskState;
import com.example.taskmaster.model.Task;

import java.util.List;


public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.viewholderlist> {
List<Task> taskList;
Context callingActivity;
    public ViewAdapter(List<Task> taskList,Context callingActivity) {
        this.taskList = taskList;
        this.callingActivity=callingActivity;
    }

    @NonNull
    @Override
    public viewholderlist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//to display the layout fragment
        View taskFragment=LayoutInflater.from(parent.getContext()).inflate(R.layout.task_fragment,parent,false);
        return new viewholderlist(taskFragment);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewholderlist holder, int position) {
        //retrive
        TextView fragmentTextView = holder.itemView.findViewById(R.id.fragmentTextView);
        TextView textviewState = holder.itemView.findViewById(R.id.StateFragment);

        Task task = taskList.get(position);
        String taskTitle = task.getTitle();
        fragmentTextView.setText((position + 1) + ". " + taskTitle);

        TaskState taskState = task.getState();
        textviewState.setText("State: " + taskState.toString());
//make it clickable
        View listViewHolder = holder.itemView;
        listViewHolder.setOnClickListener(view -> {
            Intent goToTaskDetailsIntent = new Intent(callingActivity, TaskDetailPage.class);
            goToTaskDetailsIntent.putExtra("TaskTitle", taskTitle);
            callingActivity.startActivity(goToTaskDetailsIntent);
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class  viewholderlist extends RecyclerView.ViewHolder {

        public viewholderlist(@NonNull View itemView) {
            super(itemView);
        }
    }
}
