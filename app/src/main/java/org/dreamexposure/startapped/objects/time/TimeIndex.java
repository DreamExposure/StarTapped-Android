package org.dreamexposure.startapped.objects.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TimeIndex {
    private int month;
    private int year;

    public TimeIndex(int _month, int _year) {
        month = _month;
        year = _year;
    }

    //Getters
    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public DateTime getStart() {
        return new DateTime(year, month, 1, 0, 0, 0, DateTimeZone.UTC).withTimeAtStartOfDay();
    }

    public DateTime getStop() {
        return getStart().plusMonths(1);
    }

    //Setters
    public void setMonth(int _month) {
        if (_month < 0) {
            month = 0;
        } else if (_month > 11) {
            month = 11;
        } else {
            month = _month;
        }
    }

    public void setYear(int _year) {
        year = _year;
    }

    //Functions
    public void forwardOneMonth() {
        if (month == 11) {
            month = 0;
            year++;
        } else {
            month++;
        }
    }

    public void backwardOneMonth() {
        if (month == 0) {
            month = 11;
            year--;
        } else {
            month--;
        }
    }
}
