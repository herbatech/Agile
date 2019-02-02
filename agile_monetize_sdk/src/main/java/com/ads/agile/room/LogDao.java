package com.ads.agile.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface LogDao {

    /**
     * to insert the record into room database
     *
     * @param logEntity
     * @see LogDatabase
     */
    @Insert
    void insertLog(LogEntity logEntity);

    /**
     * delete the single object
     *
     * @param logEntity
     */
    @Delete
    void deleteLog(LogEntity logEntity);

    /**
     * delete the particular record form the database
     *
     * @param id
     */
    @Query("DELETE FROM tblLog WHERE id=:id")
    void singleDeleteLog(int id);

    /**
     * get all data from the database
     *
     * @return
     */
    @Query("SELECT * FROM tblLog")
    LiveData<List<LogEntity>> getLiveListAllLog();

}