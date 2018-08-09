package it.brigio345.nimbus.utils;

import java.util.GregorianCalendar;

public class DateConverter {
    private static final String[] MONTHS = {"GENNAIO", "FEBBRAIO", "MARZO", "APRILE", "MAGGIO",
            "GIUGNO", "LUGLIO", "AGOSTO", "SETTEMBRE", "OTTOBRE", "NOVEMBRE", "DICEMBRE"};

    public static GregorianCalendar convertDate(String stringDate) {
        String[] strings = stringDate.split(" ");

        int month;
        for (month = 0; month < MONTHS.length; month++)
            if (MONTHS[month].equals(strings[2]))
                break;

        return new GregorianCalendar(Integer.parseInt(strings[3]),
                month, Integer.parseInt(strings[1]), 23, 59);
    }
}