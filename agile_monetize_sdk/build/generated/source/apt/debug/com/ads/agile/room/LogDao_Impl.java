package com.ads.agile.room;

import android.arch.lifecycle.ComputableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.InvalidationTracker.Observer;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import android.support.annotation.NonNull;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class LogDao_Impl implements LogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfLogEntity;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfSingleDeleteLog;

  public LogDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLogEntity = new EntityInsertionAdapter<LogEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `tblLog`(`id`,`event_type`,`app_id`,`value`,`android_id`,`Date_time`,`time`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, LogEntity value) {
        stmt.bindLong(1, value.id);
        if (value.event_type == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.event_type);
        }
        if (value.app_id == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.app_id);
        }
        if (value.value == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.value);
        }
        if (value.android_id == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.android_id);
        }
        if (value.Date_time == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.Date_time);
        }
        if (value.time == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.time);
        }
      }
    };
    this.__deletionAdapterOfLogEntity = new EntityDeletionOrUpdateAdapter<LogEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `tblLog` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, LogEntity value) {
        stmt.bindLong(1, value.id);
      }
    };
    this.__preparedStmtOfSingleDeleteLog = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM tblLog WHERE id=?";
        return _query;
      }
    };
  }

  @Override
  public void insertLog(LogEntity logEntity) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfLogEntity.insert(logEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteLog(LogEntity logEntity) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfLogEntity.handle(logEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void singleDeleteLog(int id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfSingleDeleteLog.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      _stmt.bindLong(_argIndex, id);
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfSingleDeleteLog.release(_stmt);
    }
  }

  @Override
  public LiveData<List<LogEntity>> getLiveListAllLog() {
    final String _sql = "SELECT * FROM tblLog";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<LogEntity>>() {
      private Observer _observer;

      @Override
      protected List<LogEntity> compute() {
        if (_observer == null) {
          _observer = new Observer("tblLog") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfEventType = _cursor.getColumnIndexOrThrow("event_type");
          final int _cursorIndexOfAppId = _cursor.getColumnIndexOrThrow("app_id");
          final int _cursorIndexOfValue = _cursor.getColumnIndexOrThrow("value");
          final int _cursorIndexOfAndroidId = _cursor.getColumnIndexOrThrow("android_id");
          final int _cursorIndexOfDateTime = _cursor.getColumnIndexOrThrow("Date_time");
          final int _cursorIndexOfTime = _cursor.getColumnIndexOrThrow("time");
          final List<LogEntity> _result = new ArrayList<LogEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final LogEntity _item;
            _item = new LogEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.event_type = _cursor.getString(_cursorIndexOfEventType);
            _item.app_id = _cursor.getString(_cursorIndexOfAppId);
            _item.value = _cursor.getString(_cursorIndexOfValue);
            _item.android_id = _cursor.getString(_cursorIndexOfAndroidId);
            _item.Date_time = _cursor.getString(_cursorIndexOfDateTime);
            _item.time = _cursor.getString(_cursorIndexOfTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }
}
