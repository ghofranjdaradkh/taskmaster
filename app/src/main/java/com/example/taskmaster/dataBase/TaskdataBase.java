package com.example.taskmaster.dataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.taskmaster.DAO.TaskDAO;
import com.example.taskmaster.Task;

@Database(entities = {Task.class},version = 1)
@TypeConverters(TaskConverter.class)
public abstract class TaskdataBase extends RoomDatabase {

    public abstract TaskDAO TaskDAO();
}
