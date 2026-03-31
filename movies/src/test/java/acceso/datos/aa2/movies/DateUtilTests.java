package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilTests {

    @Test
    public void testGetDaysBetweenDates() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 1);

        long actualDays = DateUtil.getDaysBetweenDates(startDate, endDate);
        assertEquals(31, actualDays);

        startDate = LocalDate.of(2025, 1, 15);
        endDate = LocalDate.of(2025, 1, 20);
        actualDays = DateUtil.getDaysBetweenDates(startDate, endDate);
        assertEquals(5, actualDays);
    }

    @Test
    public void testCalculateAge() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        int age = DateUtil.calculateAge(birthDate);
        assertEquals(26, age);
    }
}
