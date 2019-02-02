package com.ads.agile.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;


@Database(entities = LogEntity.class, version = 2, exportSchema = false)
public abstract class LogDatabase extends RoomDatabase {

    public abstract LogDao logDoa();

    private static volatile LogDatabase logDatabaseInstance;

    /**
     *
     * @param context
     * @return the instance of LogDatabase
     */
    static LogDatabase getLogDatabaseInstance(final Context context) {
        if (logDatabaseInstance == null) {
            synchronized (LogDatabase.class) {
                if (logDatabaseInstance == null) {
                    logDatabaseInstance = Room.databaseBuilder(context, LogDatabase.class, "db_Log.db").addMigrations().build();
                }
            }
        }
        return logDatabaseInstance;
    }

    /**
     * migration from v1 to v2
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.d("LogDatabase", "migrate 1,2 called");
            //database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER,`name` TEXT, PRIMARY KEY(`id`))");
            //database.execSQL("ALTER TABLE `tblLog` ADD COLUMN `product_id` TEXT");
            database.execSQL("CREATE TABLE sample (product_id TEXT, time TEXT) ");
            database.execSQL("INSERT INTO sample SELECT product_id, time from tblLog ");
        }
    };

    /**
     * migration from v2 to v3
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.d("LogDatabase", "migrate 2,3 called");
            //database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER,`name` TEXT, PRIMARY KEY(`id`))");
            //database.execSQL("ALTER TABLE `tblLog` ADD COLUMN `product_id` TEXT");
            //database.execSQL("CREATE TABLE sample (product_id TEXT, time TEXT) ");
            //database.execSQL("INSERT INTO sample SELECT product_id, time from tblLog ");
            database.execSQL("CREATE TABLE sample2 (product_id TEXT, time TEXT) ");
            database.execSQL("INSERT INTO sample2 SELECT product_id, time from tblLog ");
        }
    };

    /**
     * migration from v3 to v4
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.d("LogDatabase", "migrate 3,4 called");
            //database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER,`name` TEXT, PRIMARY KEY(`id`))");
            database.execSQL("ALTER TABLE `tblLog` ADD COLUMN `product_id` TEXT");
        }
    };
}