package assignment.android.scheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class SchedulePagerActivity extends AppCompatActivity {

    private static final String EXTRA_SCHEDULE_ID = "assignment.android.scheduler.schedule_id";

    public static final Intent newIntent(Context packageContext, UUID scheduleId) {
        Intent intent = new Intent(packageContext, SchedulePagerActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        return intent;
    }

    private ViewPager mViewPager;
    private List<Schedule> mSchedules;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule_pager);

        UUID scheduleId = (UUID) getIntent().getSerializableExtra(EXTRA_SCHEDULE_ID);

        mViewPager = (ViewPager) findViewById(R.id.schedule_view_pager);

        // initialise the view pager for the left and right sliding of schedules
        mSchedules = ScheduleManager.get(this).getSchedules();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Schedule schedule = mSchedules.get(position);
                return ScheduleFragment.newInstance(schedule.getId());
            }

            @Override
            public int getCount() {
                return mSchedules.size();
            }
        });

        // return the current selected schedule in the view pager
        for (int i = 0; i < mSchedules.size(); i++) {
            if (mSchedules.get(i).getId().equals(scheduleId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
