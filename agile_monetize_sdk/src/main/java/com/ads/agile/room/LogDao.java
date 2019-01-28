package com.ads.agile.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LogDao {

    @Insert
    void insertLog(LogEntity logEntity);

    @Delete
    void deleteLog(LogEntity logEntity);

    @Query("DELETE FROM tblLog WHERE id=:id")
    void singleDeleteLog(int id);

    @Query("SELECT * FROM tblLog")
    LiveData<List<LogEntity>> getLiveListAllLog();

    @Query("SELECT COUNT(*) FROM tblLog")
    int getCountLog();
}