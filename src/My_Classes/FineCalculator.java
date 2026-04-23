/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package My_Classes;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author Chrisitian Dedil
 */
public class FineCalculator {
     
   public static final double FINE_PER_DAY = 10.0;

    // 🇵🇭 Philippine Holidays (2026 sample)
    private static final Set<LocalDate> HOLIDAYS = new HashSet<>();

    static {
        HOLIDAYS.add(LocalDate.of(2026, 1, 1));  // New Year
        HOLIDAYS.add(LocalDate.of(2026, 2, 25)); // EDSA
        HOLIDAYS.add(LocalDate.of(2026, 4, 2));  // Good Friday
        HOLIDAYS.add(LocalDate.of(2026, 4, 9));  // Araw ng Kagitingan
        HOLIDAYS.add(LocalDate.of(2026, 5, 1));  // Labor Day
        HOLIDAYS.add(LocalDate.of(2026, 6, 12)); // Independence Day
        HOLIDAYS.add(LocalDate.of(2026, 8, 31)); // National Heroes Day
        HOLIDAYS.add(LocalDate.of(2026, 11, 1)); // All Saints
        HOLIDAYS.add(LocalDate.of(2026, 11, 30));// Bonifacio Day
        HOLIDAYS.add(LocalDate.of(2026, 12, 8)); // Immaculate Conception
        HOLIDAYS.add(LocalDate.of(2026, 12, 25));// Christmas
        HOLIDAYS.add(LocalDate.of(2026, 12, 30));// Rizal Day
        HOLIDAYS.add(LocalDate.of(2026, 12, 31));// New Year's Eve
    }

    public static long countWeekdaysLate(java.sql.Date due, java.sql.Date today) {
        LocalDate start = due.toLocalDate().plusDays(1);
        LocalDate end = today.toLocalDate();

        long count = 0;

        while (!start.isAfter(end)) {
            DayOfWeek day = start.getDayOfWeek();

            // ✅ Skip Sunday + Holidays ONLY
            if (day != DayOfWeek.SUNDAY && !HOLIDAYS.contains(start)) {
                count++;
            }

            start = start.plusDays(1);
        }

        return count;
    }

    public static double calculateFine(java.sql.Date due, java.sql.Date today) {
        long days = countWeekdaysLate(due, today);
        return days * FINE_PER_DAY;
    }
}
