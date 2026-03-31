package acceso.datos.aa2.movies.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    // Calculate days between two dates
    public static long getDaysBetweenDates(LocalDate startDate, LocalDate endDate) {
        return Math.abs(startDate.until(endDate, ChronoUnit.DAYS));
    }

    // Calculate age from birth date
    public static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Check if a date is in the past
    public static boolean isPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    // Check if a date is in the future
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
}
