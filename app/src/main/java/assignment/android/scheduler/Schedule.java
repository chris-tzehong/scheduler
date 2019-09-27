package assignment.android.scheduler;

import java.util.Date;
import java.util.UUID;

public class Schedule {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private boolean mIsDone;

    public Schedule() {
        this(UUID.randomUUID());
    }

    public Schedule(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isDone() {
        return mIsDone;
    }

    public void setDone(boolean done) {
        mIsDone = done;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
