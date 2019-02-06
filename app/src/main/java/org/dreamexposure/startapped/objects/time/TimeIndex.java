package org.dreamexposure.startapped.objects.time;

import org.joda.time.DateTime;

public class TimeIndex {
    private long before;

    private long latest;
    private long oldest;

    public TimeIndex() {
        DateTime now = DateTime.now();
        before = now.getMillis();
    }

    //Getters
    public long getBefore() {
        return before;
    }

    public long getLatest() {
        return latest;
    }

    public long getOldest() {
        return oldest;
    }

    //Setters
    public void setBefore(long _before) {
        before = _before;
    }

    public void setLatest(long _latest) {
        latest = _latest;
    }

    public void setOldest(long _oldest) {
        oldest = _oldest;
    }
}
