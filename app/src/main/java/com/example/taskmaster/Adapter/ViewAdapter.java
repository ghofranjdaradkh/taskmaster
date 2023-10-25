package com.example.taskmaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R; // Import your R class
import com.example.taskmaster.Task;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private Context context;

    public ViewAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_blank, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getState().ordinal());


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle;
        public TextView taskDescription;

        public TaskViewHolder(View view) {
            super(view);
            taskTitle = view.findViewById(R.id.taskTitle);
            taskDescription = view.findViewById(R.id.taskDescription);
        }
    }
}
