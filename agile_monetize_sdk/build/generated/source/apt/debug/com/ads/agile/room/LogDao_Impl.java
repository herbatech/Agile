package com.ads.agile.room;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
public final class LogDao_Impl implements LogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfLogEntity;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfSingleDeleteLog;

  public LogDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLogEntity = new EntityInsertionAdapter<LogEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `tblLog`(`id`,`event_type`,`app_id`,`event_id`,`value`,`android_id`,`time`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
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
        if (value.event_id == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.event_id);
        }
        if (value.value == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.value);
        }
        if (value.android_id == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.android_id);
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
  public void insertLog(final LogEntity logEntity) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfLogEntity.insert(logEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteLog(final LogEntity logEntity) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfLogEntity.handle(logEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void singleDeleteLog(final int id) {
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
    return __db.getInvalidationTracker().createLiveData(new String[]{"tblLog"}, new Callable<List<LogEntity>>() {
      @Override
      public List<LogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "event_type");
          final int _cursorIndexOfAppId = CursorUtil.getColumnIndexOrThrow(_cursor, "app_id");
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "event_id");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfAndroidId = CursorUtil.getColumnIndexOrThrow(_cursor, "android_id");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final List<LogEntity> _result = new ArrayList<LogEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final LogEntity _item;
            _item = new LogEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.event_type = _cursor.getString(_cursorIndexOfEventType);
            _item.app_id = _cursor.getString(_cursorIndexOfAppId);
            _item.event_id = _cursor.getString(_cursorIndexOfEventId);
            _item.value = _cursor.getString(_cursorIndexOfValue);
            _item.android_id = _cursor.getString(_cursorIndexOfAndroidId);
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
    });
  }
}
