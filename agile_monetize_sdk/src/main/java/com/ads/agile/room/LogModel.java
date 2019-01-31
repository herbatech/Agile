package com.ads.agile.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LogModel extends AndroidViewModel {

    private String TAG = this.getClass().getSimpleName();
    private LogDao logDao;
    private LogEntity logEntity;
    private LogDatabase logDatabase;
    private LiveData<List<LogEntity>> liveList;

    /**
     *
     * @param application to init the database
     * @see LogDatabase
     */
    public LogModel(@NonNull Application application) {
        super(application);
        logDatabase = LogDatabase.getLogDatabaseInstance(application);
        logDao = logDatabase.logDoa();
        liveList = logDao.getLiveListAllLog();
    }

    /**
     * get all data from the database
     * @return
     */
    public LiveData<List<LogEntity>> getLiveListAllLog() {
        return liveList;
    }

    /**
     * insert the record into database
     * @param logEntity
     * @see LogDao
     */
    public void insertLog(LogEntity logEntity) {
        new InsertAsyncTask(logDao).execute(logEntity);
    }

    /**
     *
     * @param logEntity
     * @see LogDao
     */
    public void deleteLog(LogEntity logEntity) {
        new DeleteAsyncTask(logDao).execute(logEntity);
    }

    /**
     *
     * @param id
     * @see LogDao
     */
    public void singleDeleteLog(int id) { new DeleteSingleAsyncTask(logDao, id).execute(logEntity);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared called");
    }

    /**
     * background thread to insert the record into database
     */
    private class InsertAsyncTask extends AsyncTask<LogEntity, Void, Void> {

        LogDao logDao;

        public InsertAsyncTask(LogDao logDao) {
            this.logDao = logDao;
        }

        @Override
        protected Void doInBackground(LogEntity... logEntities) {
            this.logDao.insertLog(logEntities[0]);
            return null;
        }
    }

    /**
     * background thread to delete the record from database
     */
    private class DeleteAsyncTask extends AsyncTask<LogEntity, Void, Void> {

        LogDao logDao;

        public DeleteAsyncTask(LogDao logDao) {
            this.logDao = logDao;
        }

        @Override
        protected Void doInBackground(LogEntity... logEntities) {
            this.logDao.deleteLog(logEntities[0]);
            return null;
        }
    }

    /**
     * background thread to delete single record from database
     */
    private class DeleteSingleAsyncTask extends AsyncTask<LogEntity, Void, Void> {

        LogDao logDao;
        int id;

        public DeleteSingleAsyncTask(LogDao logDao, int id) {
            this.logDao = logDao;
            this.id = id;
        }

        @Override
        protected Void doInBackground(LogEntity... logEntities) {
            this.logDao.singleDeleteLog(id);
            return null;
        }
    }

}