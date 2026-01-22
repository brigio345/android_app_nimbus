package it.brigio345.nimbus.utils;

import java.util.GregorianCalendar;

public class DateConverter {
    private static final String[] MONTHS = {"GENNAIO", "FEBBRAIO", "MARZO", "APRILE", "MAGGIO",
            "GIUGNO", "LUGLIO", "AGOSTO", "SETTEMBRE", "OTTOBRE", "NOVEMBRE", "DICEMBRE"};

    public static class InvalidStringDateException extends Exception {
        public InvalidStringDateException(String message) {
            super(message);
        }
    }

    public static GregorianCalendar convertDate(String stringDate) throws InvalidStringDateException {
        String[] strings = stringDate.split(" ");

        if (strings.length != 4)
            throw new InvalidStringDateException("Unexpected number of split elements.");

        String dayStr = strings[1];
        String monthStr = strings[2];
        String yearStr = strings[3];

        int month;
        for (month = 0; month < MONTHS.length; month++)
            if (MONTHS[month].equals(monthStr))
                break;

        try {
            return new GregorianCalendar(Integer.parseInt(yearStr),
                    month, Integer.parseInt(dayStr), 23, 59);
        } catch (NumberFormatException nfe) {
            throw new InvalidStringDateException("Invalid date format.");
        }
    }
}
