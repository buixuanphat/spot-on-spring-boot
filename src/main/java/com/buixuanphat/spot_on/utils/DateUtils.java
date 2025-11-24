package com.buixuanphat.spot_on.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtils {
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static LocalDate stringtoLocalDate(String s)
    {
        try
        {
            return LocalDate.parse(s, FORMATTER_DATE);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static String localDateToString(LocalDate date)
    {
        try
        {
            return date.format(FORMATTER_DATE);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static String instantToString(Instant instant) {
        try {
            LocalDate ldt = LocalDate.ofInstant(instant, ZoneId.systemDefault());
            return localDateToString(ldt);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


    public static Instant stringToInstant(String s) {
        try {
            LocalDate date;
            if(s.contains(":"))
            {
                date = LocalDate.parse(s, FORMATTER_DATETIME);
            }
            else
            {
                date = LocalDate.parse(s, FORMATTER_DATE);
            }
            return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }





}
