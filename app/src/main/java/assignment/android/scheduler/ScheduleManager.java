package assignment.android.scheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import assignment.android.scheduler.database.ScheduleBaseHelper;
import assignment.android.scheduler.database.ScheduleCursorWrapper;
import assignment.android.scheduler.database.ScheduleDbSchema;
import assignment.android.scheduler.database.ScheduleDbSchema.ScheduleTable;

public class ScheduleManager {

    private static ScheduleManager sScheduleManager;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    // creates a singleton object for database management
    public static ScheduleManager get(Context context) {
        if (sScheduleManager == null) {
            sScheduleManager = new ScheduleManager(context);
        }

        return sScheduleManager;
    }

    // constructor should be private to limit the initialisation of class
    private ScheduleManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ScheduleBaseHelper(mContext).getWritableDatabase();
    }

    // add a schedule into the database
    public void addSchedule(Schedule s) {
        ContentValues values = getContentValues(s);
        mDatabase.insert(ScheduleTable.NAME, null, values);
    }

    // get an array containing all the schedules in the database
    public List<Schedule> getSchedules() {
        List<Schedule> schedules = new ArrayList<>();

        ScheduleCursorWrapper cursor = querySchedule(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                schedules.add(cursor.getSchedule());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return schedules;
    }

    // return a specific schedule with the mentioned UUID
    public Schedule getSchedule(UUID id) {
        ScheduleCursorWrapper cursor = querySchedule(ScheduleTable.Cols.UUID + " = ?",
                new String[] {id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getSchedule();
        } finally {
            cursor.close();
        }
    }

    // get the directory of the schedule photo
    public File getPhotoFile(Schedule s) {
        File fileDir = mContext.getFilesDir();
        return new File(fileDir, s.getPhotoFilename());
    }

    // update the schedule in the database
    public void updateSchedule(Schedule s) {
        String uuidString = s.getId().toString();
        ContentValues values = getContentValues(s);

        mDatabase.update(ScheduleTable.NAME, values,
                ScheduleTable.Cols.UUID + " =?",
                new String[] {uuidString});
    }

    // delete a schedule from the database
    public void deleteSchedule(Schedule s) {
        String uuidString = s.getId().toString();

        mDatabase.delete(ScheduleTable.NAME,
                ScheduleTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    // create a cursor wrapper to simplify the query process
    private ScheduleCursorWrapper querySchedule(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(ScheduleTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return new ScheduleCursorWrapper(cursor);
    }

    // create a content values object based on the schedule to simplify database transactions
    private static ContentValues getContentValues(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(ScheduleTable.Cols.UUID, schedule.getId().toString());
        values.put(ScheduleTable.Cols.TITLE, schedule.getTitle());
        values.put(ScheduleTable.Cols.DATE, schedule.getDate().getTime());
        values.put(ScheduleTable.Cols.DESCRIPTION, schedule.getDescription());
        values.put(ScheduleTable.Cols.DONE, schedule.isDone() ? 1 : 0);

        return values;
    }
}
