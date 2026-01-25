package it.brigio345.nimbus.utils;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConverter {
    private static final List<String> MONTHS = Arrays.asList("GENNAIO", "FEBBRAIO", "MARZO", "APRILE", "MAGGIO",
            "GIUGNO", "LUGLIO", "AGOSTO", "SETTEMBRE", "OTTOBRE", "NOVEMBRE", "DICEMBRE");

    public static class InvalidStringDateException extends Exception {
        public InvalidStringDateException(String message) {
            super(message);
        }
    }

    public static GregorianCalendar convertDate(String stringDate) throws InvalidStringDateException {
        Pattern pattern = Pattern.compile("^\\w+\\s(\\d{1,2})\\s([A-Z]+)\\s(\\d{4})$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(stringDate.trim());

        if (!matcher.matches())
            throw new InvalidStringDateException("Date string does not match expected format.");

        String dayStr = matcher.group(1);
        String monthStr = matcher.group(2);
        String yearStr = matcher.group(3);

        int month = MONTHS.indexOf(monthStr.toUpperCase());

        if (month == -1)
            throw new InvalidStringDateException("Invalid month: " + monthStr);

        try {
            int day = Integer.parseInt(dayStr);
            int year = Integer.parseInt(yearStr);
            return new GregorianCalendar(year, month, day, 23, 59);
        } catch (NumberFormatException e) {
            throw new InvalidStringDateException("Invalid date format.");
        }
    }
}
