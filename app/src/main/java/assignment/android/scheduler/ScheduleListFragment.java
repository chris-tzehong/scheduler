package assignment.android.scheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ScheduleListFragment extends Fragment {

    private RecyclerView mScheduleRecyclerView;
    private ScheduleAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        // inflate recycler view to store list items
        mScheduleRecyclerView = (RecyclerView) view.findViewById(R.id.schedule_recycler_view);
        mScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    // always update the data when this fragment comes foreground
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // inflate the action menu button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_schedule_list, menu);
    }

    // allows addition of new schedule
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_schedule:
                Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // update the data when this function is called
    private void updateUI() {
        ScheduleManager scheduleManager = ScheduleManager.get(getActivity());
        List<Schedule> schedules = scheduleManager.getSchedules();

        if (mAdapter == null) {
            mAdapter = new ScheduleAdapter(schedules);
            mScheduleRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setSchedules(schedules);
            mAdapter.notifyDataSetChanged();
        }

    }

    // adapter class to load the schedule list and bind data to the view holder
    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleHolder> {

        private List<Schedule> mSchedules;

        public ScheduleAdapter(List<Schedule> schedules) {
            mSchedules = schedules;
        }

        @NonNull
        @Override
        public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ScheduleHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScheduleHolder holder, int position) {
            Schedule schedule = mSchedules.get(position);
            holder.bind(schedule);
        }

        @Override
        public int getItemCount() {
            return mSchedules.size();
        }

        public void setSchedules(List<Schedule> schedules) {
            mSchedules = schedules;
        }
    }

    // view holder class to hold the view for each item
    private class ScheduleHolder extends RecyclerView.ViewHolder {

        private Schedule mSchedule;

        private TextView mListScheduleTitle;
        private TextView mListScheduleDate;
        private ImageView mImageScheduleDone;

        public ScheduleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_schedule, parent, false));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = SchedulePagerActivity.newIntent(getActivity(), mSchedule.getId());
                    startActivity(intent);
                }
            });

            mListScheduleTitle = (TextView) itemView.findViewById(R.id.listScheduleTitle);
            mListScheduleDate = (TextView) itemView.findViewById(R.id.listScheduleDate);
            mImageScheduleDone = (ImageView) itemView.findViewById(R.id.imageScheduleDone);
        }

        public void bind(Schedule schedule) {
            mSchedule = schedule;
            mListScheduleTitle.setText(mSchedule.getTitle());
            mListScheduleDate.setText(mSchedule.getDate().toString());
            mImageScheduleDone.setVisibility(mSchedule.isDone() ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
