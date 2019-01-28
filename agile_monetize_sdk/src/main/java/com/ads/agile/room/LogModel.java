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

    public LogModel(@NonNull Application application) {
        super(application);
        logDatabase = LogDatabase.getLogDatabaseInstance(application);
        logDao = logDatabase.logDoa();
        liveList = logDao.getLiveListAllLog();
        //list = logDao.getListAllLog();
    }

    public LiveData<List<LogEntity>> getLiveListAllLog() {
        return liveList;
    }

    public void insertLog(LogEntity logEntity) {
        new InsertAsyncTask(logDao).execute(logEntity);
    }

    public void deleteLog(LogEntity logEntity) {
        new DeleteAsyncTask(logDao).execute(logEntity);
    }

    public void singleDeleteLog(int id) {
        new DeleteSingleAsyncTask(logDao, id).execute(logEntity);
    }

    /*public int getCountLog()
    {
        return count;
    }*/

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared called");
    }

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

//    private class getDataAsynTask extends AsyncTask<LogEntity, Void, List<LogEntity>> {
//
//        LogDao logDao;
//        List<LogEntity> liveList;
//
//        public getDataAsynTask(LogDao logDao,List<LogEntity> liveList) {
//            this.logDao = logDao;
//            this.liveList = liveList;
//        }
//
//        @Override
//        protected List<LogEntity> doInBackground(LogEntity... logEntities) {
//            this.liveList = this.logDao.getLiveListAllLog();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<LogEntity> liveList) {
//            super.onPostExecute(liveList);
//        }
//    }
}