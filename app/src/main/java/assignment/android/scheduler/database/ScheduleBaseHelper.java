package assignment.android.scheduler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.view.ViewPager;

import assignment.android.scheduler.Schedule;
import assignment.android.scheduler.database.ScheduleDbSchema.ScheduleTable;

public class ScheduleBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "scheduleBase.db";

    public ScheduleBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + ScheduleTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                ScheduleTable.Cols.UUID + ", " +
                ScheduleTable.Cols.TITLE + ", " +
                ScheduleTable.Cols.DATE + ", " +
                ScheduleTable.Cols.DESCRIPTION + ", " +
                ScheduleTable.Cols.DONE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
