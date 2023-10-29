package com.example.taskmaster;

import androidx.annotation.NonNull;

public enum TaskState {
         NEW("new"),
        ASSIGNED("assigned"),
       IN_PROGRESS("inProgress"),
      COMPLETED("completed");
         private final String taskText;

    TaskState(String taskText) {
        this.taskText = taskText;
    }

    public String getTaskText() {
        return taskText;
    }

    public static TaskState fromString(String possibleTaskState){
        for(TaskState state : TaskState.values()){
            if (state.taskText.equals(possibleTaskState)){
                return state;
            }
        }

        return null;
    }

    @NonNull
    @Override
    public String toString(){
        if(taskText == null){
            return "";
        }
        return taskText;
    }

}

