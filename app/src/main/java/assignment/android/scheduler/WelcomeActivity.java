package assignment.android.scheduler;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends AppCompatActivity {

    // the time constant in milliseconds for the welcome page to time out
    private final static int TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        // after the time constant has been over, the welcome page will automatically be replaced
        // by the schedule list activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(WelcomeActivity.this, ScheduleListActivity.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }
}
