package assignment.android.scheduler.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import java.util.Date;
import java.util.UUID;

import assignment.android.scheduler.Schedule;
import assignment.android.scheduler.database.ScheduleDbSchema.ScheduleTable;

public class ScheduleCursorWrapper extends CursorWrapper {

    public ScheduleCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Schedule getSchedule() {
        String uuidString = getString(getColumnIndex(ScheduleTable.Cols.UUID));
        String title = getString(getColumnIndex(ScheduleTable.Cols.TITLE));
        long date = getLong(getColumnIndex(ScheduleTable.Cols.DATE));
        String description = getString(getColumnIndex(ScheduleTable.Cols.DESCRIPTION));
        int isDone = getInt(getColumnIndex(ScheduleTable.Cols.DONE));

        Schedule schedule = new Schedule(UUID.fromString(uuidString));
        schedule.setTitle(title);
        schedule.setDate(new Date(date));
        schedule.setDescription(description);
        schedule.setDone(isDone != 0);

        return schedule;
    }
}
