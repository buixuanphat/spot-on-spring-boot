package com.buixuanphat.spot_on.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtils {
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");


    public static LocalDate stringtoLocalDate(String s)
    {
        try
        {
            return LocalDate.parse(s, FORMATTER_DATE);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
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
            log.error(e.getMessage());
            return null;
        }
    }

    public static String instantToString(Instant instant) {
        try {
            return instant
                    .atZone(VIETNAM_ZONE)
                    .format(FORMATTER_DATETIME);
        } catch (Exception e) {
            log.error(e.getMessage());
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


    public static LocalTime stringToLocalTime(String s) {
        try {
            return LocalTime.parse(s, FORMATTER_TIME);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    public static String localTimeToString(LocalTime time) {
        try {
            return time.format(FORMATTER_TIME);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }






}
