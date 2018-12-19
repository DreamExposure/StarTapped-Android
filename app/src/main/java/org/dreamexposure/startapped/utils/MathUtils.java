package org.dreamexposure.startapped.utils;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author NovaFox161
 * Date Created: 12/18/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class MathUtils {
    public static int determineAge(String birthday) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(birthday);

            LocalDate birth = new LocalDate(date);
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birth, now);

            return age.getYears();
        } catch (ParseException ignore) {
        }
        return -1;
    }
}
