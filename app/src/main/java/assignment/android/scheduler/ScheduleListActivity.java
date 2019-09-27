package assignment.android.scheduler;

import android.support.v4.app.Fragment;

public class ScheduleListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ScheduleListFragment();
    }

    @Override
    public void onBackPressed() {
        // do nothing when the back key is pressed
        // avoid user to return to the add schedule page after a schedule has been added
    }
}
