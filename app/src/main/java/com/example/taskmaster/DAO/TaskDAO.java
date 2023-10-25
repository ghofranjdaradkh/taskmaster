package com.example.taskmaster.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taskmaster.Task;

import java.util.List;

@Dao
public interface TaskDAO {

    @Insert
    public void insertTask(Task Task);

    @Query("select * from Task")
    public List<Task> findAll();

    @Query("select * from Task ORDER BY title ASC")
    public List<Task> findAllSortedByName();

    @Query("select * from Task where id = :id")
    Task findByAnId(long id);

}
