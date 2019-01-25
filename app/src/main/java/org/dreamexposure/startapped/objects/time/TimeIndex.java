package org.dreamexposure.startapped.objects.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TimeIndex {
    private int month;
    private int year;

    public TimeIndex() {
        DateTime now = DateTime.now();
        this.month = now.getMonthOfYear();
        this.year = now.getYear();
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
        if (_month < 1) {
            month = 1;
        } else if (_month > 12) {
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
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
    }

    public void backwardOneMonth() {
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
    }
}
