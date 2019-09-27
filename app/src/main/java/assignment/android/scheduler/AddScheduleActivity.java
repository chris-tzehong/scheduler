package assignment.android.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddScheduleActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener, TimePickerFragment.OnTimeSelectedListener {

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTIme";
    private static final int CODE_REQUEST_PHOTO = 0;

    private EditText mAddTitle;
    private EditText mAddDescription;
    private TextView mDateLabel;
    private TextView mTimeLabel;
    private Button mPickDate;
    private Button mPickTime;
    private Button mAddSchedule;
    private Button mAddPhoto;
    private TextView mPhotoLabel;
    private Schedule mSchedule;
    private File mPhotoFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        mAddTitle = (EditText) findViewById(R.id.addScheduleTitle);
        mAddDescription = (EditText) findViewById(R.id.addScheduleDescription);
        mPickDate = (Button) findViewById(R.id.btnAddSchedulePickDate);
        mPickTime = (Button) findViewById(R.id.btnAddSchedulePickTime);
        mAddSchedule = (Button) findViewById(R.id.btnAddSchedule);
        mAddPhoto = (Button) findViewById(R.id.btnAddScheduleAddPhoto);
        mDateLabel = (TextView) findViewById(R.id.addScheduleDate);
        mTimeLabel = (TextView) findViewById(R.id.addScheduleTime);

        // create a new schedule object to store data
        mSchedule = new Schedule();

        // show up a date picker on screen to choose the schedule date
        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(new Date());
                datePickerFragment.show(AddScheduleActivity.this.getSupportFragmentManager(), DIALOG_DATE);
            }
        });

        // show up a time picker on screen to choose the schedule time
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(new Date());
                timePickerFragment.show(AddScheduleActivity.this.getSupportFragmentManager(), DIALOG_TIME);
            }
        });

        // add a photo for the schedule
        mAddPhoto = (Button) findViewById(R.id.btnAddScheduleAddPhoto);
        mPhotoLabel = (TextView) findViewById(R.id.addSchedulePhoto);
        mPhotoFile = ScheduleManager.get(this).getPhotoFile(mSchedule);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // check if the device allows for picture taking
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getPackageManager()) != null;
        if (!canTakePhoto) {
            mPhotoLabel.setText(R.string.camera_disabled);
        } else {
            mPhotoLabel.setText(R.string.add_schedule_no_photo);
        }
        mAddPhoto.setEnabled(canTakePhoto);

        // start the camera intent
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(AddScheduleActivity.this, "assignment.android.scheduler.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, CODE_REQUEST_PHOTO);
            }
        });

        // add the schedule into the database
        mAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // verify that the title, date and time exists before the schedule can be added
                if (mAddTitle.getText().toString().equals("")) {
                    mAddTitle.setError("Please enter a title for your task");
                } else if (mDateLabel.getText().toString().equals(getString(R.string.add_schedule_no_date))) {
                    Toast.makeText(AddScheduleActivity.this, R.string.date_not_picked_toast, Toast.LENGTH_SHORT).show();
                } else if (mTimeLabel.getText().toString().equals(getString(R.string.add_schedule_no_time))) {
                    Toast.makeText(AddScheduleActivity.this, R.string.time_not_picked_toast, Toast.LENGTH_SHORT).show();
                } else {
                    mSchedule.setTitle(mAddTitle.getText().toString());
                    mSchedule.setDescription(mAddDescription.getText().toString());
                    ScheduleManager.get(getApplicationContext()).addSchedule(mSchedule);

                    Intent intent = new Intent(AddScheduleActivity.this, ScheduleListActivity.class);
                    startActivity(intent);
                }

            }
        });

    }

    // when a date is picked from the date picker, the label will changed
    @Override
    public void onDateSelected(Date date) {
        mDateLabel = (TextView) findViewById(R.id.addScheduleDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Date existingDate = mSchedule.getDate();
        Calendar existingCalendar = Calendar.getInstance();
        existingCalendar.setTime(existingDate);
        existingCalendar.set(Calendar.YEAR, year);
        existingCalendar.set(Calendar.MONTH, month);
        existingCalendar.set(Calendar.DAY_OF_MONTH, day);

        Date updatedDate = existingCalendar.getTime();
        mSchedule.setDate(updatedDate);

        String dateFormat = "EEEE, MMMM dd";
        String displayedDate = DateFormat.format(dateFormat, mSchedule.getDate()).toString();
        mDateLabel.setText(displayedDate);
    }

    // when a time is picked from the time picker, the label will changed
    @Override
    public void onTimeSelected(Date date) {
        mTimeLabel = (TextView) findViewById(R.id.addScheduleTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        Date existingTime = mSchedule.getDate();
        Calendar existingCalendar = Calendar.getInstance();
        existingCalendar.setTime(existingTime);
        existingCalendar.set(Calendar.HOUR_OF_DAY, hour);
        existingCalendar.set(Calendar.MINUTE, minute);

        Date updatedTime = existingCalendar.getTime();
        mSchedule.setDate(updatedTime);

        String twelveHourString = null;
        if (hour < 12) {
            twelveHourString = getString(R.string.twelve_hour_am);
        } else {
            twelveHourString = getString(R.string.twelve_hour_pm);
        }
        String timeFormat = "hh : mm";
        String formattedTime = DateFormat.format(timeFormat, mSchedule.getDate()).toString();
        String displayedTime = getString(R.string.time_format, formattedTime, twelveHourString);
        mTimeLabel.setText(displayedTime);
    }

    // close file access for camera intent after photo has been taken and stored
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == CODE_REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(AddScheduleActivity.this, "assignment.android.scheduler.fileprovider", mPhotoFile);
            revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mPhotoLabel.setText(R.string.add_schedule_photo_successfully_taken);
        }
    }
}
