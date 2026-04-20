/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package My_Classes;


import java.time.DayOfWeek;
import java.time.LocalDate;
/**
 *
 * @author Chrisitian Dedil
 */
public class FineCalculator {
     public static final double FINE_PER_DAY = 10.0;

    public static long countWeekdaysLate(java.sql.Date due, java.sql.Date today) {
        LocalDate start = due.toLocalDate().plusDays(1);
        LocalDate end = today.toLocalDate();

        long count = 0;

        while (!start.isAfter(end)) {
            DayOfWeek day = start.getDayOfWeek();

            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
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
