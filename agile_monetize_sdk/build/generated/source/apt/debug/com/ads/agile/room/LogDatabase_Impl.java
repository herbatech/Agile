package com.ads.agile.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class LogDatabase_Impl extends LogDatabase {
  private volatile LogDao _logDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `tblLog` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `event_type` TEXT NOT NULL, `app_id` TEXT NOT NULL, `value` TEXT NOT NULL, `android_id` TEXT NOT NULL, `Date_time` TEXT NOT NULL, `time` TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"5bbd94576dbb521c37b4d57248d27a2c\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `tblLog`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsTblLog = new HashMap<String, TableInfo.Column>(7);
        _columnsTblLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1));
        _columnsTblLog.put("event_type", new TableInfo.Column("event_type", "TEXT", true, 0));
        _columnsTblLog.put("app_id", new TableInfo.Column("app_id", "TEXT", true, 0));
        _columnsTblLog.put("value", new TableInfo.Column("value", "TEXT", true, 0));
        _columnsTblLog.put("android_id", new TableInfo.Column("android_id", "TEXT", true, 0));
        _columnsTblLog.put("Date_time", new TableInfo.Column("Date_time", "TEXT", true, 0));
        _columnsTblLog.put("time", new TableInfo.Column("time", "TEXT", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTblLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTblLog = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTblLog = new TableInfo("tblLog", _columnsTblLog, _foreignKeysTblLog, _indicesTblLog);
        final TableInfo _existingTblLog = TableInfo.read(_db, "tblLog");
        if (! _infoTblLog.equals(_existingTblLog)) {
          throw new IllegalStateException("Migration didn't properly handle tblLog(com.ads.agile.room.LogEntity).\n"
                  + " Expected:\n" + _infoTblLog + "\n"
                  + " Found:\n" + _existingTblLog);
        }
      }
    }, "5bbd94576dbb521c37b4d57248d27a2c", "66ad8199c8956a34abd1952a055f47f4");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "tblLog");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `tblLog`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public LogDao logDoa() {
    if (_logDao != null) {
      return _logDao;
    } else {
      synchronized(this) {
        if(_logDao == null) {
          _logDao = new LogDao_Impl(this);
        }
        return _logDao;
      }
    }
  }
}
