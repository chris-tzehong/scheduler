package assignment.android.scheduler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.view.SupportActionModeWrapper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class ScheduleFragment extends Fragment {

    private static final String ARG_SCHEDULE_ID = "schedule_id";

    public static ScheduleFragment newInstance(UUID scheduleId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHEDULE_ID, scheduleId);

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private Schedule mSchedule;
    private TextView mTitleField;
    private TextView mDateField;
    private TextView mTimeField;
    private TextView mDescriptionField;
    private CheckBox mDoneCheckBox;
    private Button mJumpToFirst;
    private Button mJumpToLast;
    private ViewPager mViewPager;
    private ImageView mImageView;
    private File mPhotoFile;
    private ConstraintLayout mConstraintLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // receive the schedule ID and retrieve from database
        UUID scheduleId = (UUID) getArguments().getSerializable(ARG_SCHEDULE_ID);
        mSchedule = ScheduleManager.get(getActivity()).getSchedule(scheduleId);
    }

    @Override
    public void onPause() {
        super.onPause();
        // update the schedule when the foreground activity changes
        ScheduleManager.get(getActivity()).updateSchedule(mSchedule);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // delete the currently viewing schedule
            case R.id.delete_schedule:
                ScheduleManager.get(getActivity()).deleteSchedule(mSchedule);
                Intent intent = new Intent(getActivity(), ScheduleListActivity.class);
                startActivity(intent);
                return true;
            case R.id.share_schedule: // share the currently viewing schedule in text form
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getSimpleSchedule());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.simple_schedule_subject));
                i = Intent.createChooser(i, getString(R.string.share_simple_schedule));
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        // retrieve the hour value of current schedule
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSchedule.getDate());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // set up dynamic background
        mConstraintLayout = (ConstraintLayout) v.findViewById(R.id.scheduleConstraintLayout);
        int[] backgroundImages = new int[4];
        backgroundImages[0] = R.drawable.background_night;
        backgroundImages[1] = R.drawable.background_wolf;
        backgroundImages[2] = R.drawable.background_star;
        backgroundImages[3] = R.drawable.background_starry;

        int randomBackground = new Random().nextInt(backgroundImages.length);
        mConstraintLayout.setBackgroundResource(backgroundImages[randomBackground]);

        // set up the schedule details
        mTitleField = (TextView) v.findViewById(R.id.scheduleTitle);
        mTitleField.setText(mSchedule.getTitle());

        mDateField = (TextView) v.findViewById(R.id.scheduleDate);
        String dateFormat = "EEEE, MMMM dd";
        String displayedDate = DateFormat.format(dateFormat, mSchedule.getDate()).toString();
        mDateField.setText(displayedDate);

        mTimeField = (TextView) v.findViewById(R.id.scheduleTime);
        String twelveHourString = null;
        if (hour < 12) {
            twelveHourString = getString(R.string.twelve_hour_am);
        } else {
            twelveHourString = getString(R.string.twelve_hour_pm);
        }
        String timeFormat = "hh : mm";
        String formattedTime = DateFormat.format(timeFormat, mSchedule.getDate()).toString();
        String displayedTime = getString(R.string.time_format, formattedTime, twelveHourString);
        mTimeField.setText(displayedTime);

        mDescriptionField = (TextView) v.findViewById(R.id.scheduleDescription);
        if (mSchedule.getDescription().equals("")) {
            mDescriptionField.setText(R.string.schedule_no_description) ;
        } else {
            mDescriptionField.setText(mSchedule.getDescription());
        }

        // the checkbox can be checked/unchecked to indicate schedule status
        mDoneCheckBox = (CheckBox) v.findViewById(R.id.checkBoxIsDone);
        mDoneCheckBox.setChecked(mSchedule.isDone());
        mDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mSchedule.setDone(b);
            }
        });

        // allow left and right sliding of interface to access other schedules
        mViewPager = (ViewPager) getActivity().findViewById(R.id.schedule_view_pager);
        // directly jump to the first schedule in the list
        mJumpToFirst = (Button) v.findViewById(R.id.btnFirstSchedule);
        mJumpToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        // directly jump to the last schedule in the list
        mJumpToLast = (Button) v.findViewById(R.id.btnLastSchedule);
        mJumpToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(ScheduleManager.get(getActivity()).getSchedules().size() - 1);
            }
        });

        // show the image if there is any photo taken during addition of task, else it is invisible
        mPhotoFile = ScheduleManager.get(getActivity()).getPhotoFile(mSchedule);
        mImageView = (ImageView) v.findViewById(R.id.imageSchedulePhoto);
        updatePhotoView();

        return v;
    }

    // produce a simple text form of schedule for sharing purpose
    private String getSimpleSchedule() {
        String solvedString = null;
        if (mSchedule.isDone()) {
            solvedString = getString(R.string.schedule_done);
        } else {
            solvedString = getString(R.string.schedule_not_done);
        }

        String dateFormat = "EEE, MMM dd, hh : mm";
        String dateString = DateFormat.format(dateFormat, mSchedule.getDate()).toString();

        String timeFormatString = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSchedule.getDate());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 12) {
            timeFormatString = getString(R.string.twelve_hour_pm);
        } else {
            timeFormatString = getString(R.string.twelve_hour_am);
        }

        String descriptionString = null;
        if (mSchedule.getDescription().equals("")) {
            descriptionString = getString(R.string.schedule_no_description);
        } else {
            descriptionString = mSchedule.getDescription();
        }

        String simpleSchedule = getString(R.string.simple_schedule, mSchedule.getTitle(),
                dateString, timeFormatString, descriptionString, solvedString);

        return simpleSchedule;
    }

    // hide or show the image view
    // if image available, retrieve from designated path
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mImageView.setVisibility(View.INVISIBLE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.getPath());
            mImageView.setImageBitmap(bitmap);
        }
    }
}
