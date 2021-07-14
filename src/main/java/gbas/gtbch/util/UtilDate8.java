package gbas.gtbch.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.HashMap;

public class UtilDate8 {

    private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";
    private static final String DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final String DEFAULT_FULLDATE_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
    private static final HashMap<String, DateTimeFormatter> dateFormatHashMap = new HashMap<>();
    private static DateTimeFormatter DATEFORMATTER =
            new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy[ [HH][:mm][:ss][.SSS]]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
                    .toFormatter();

    private UtilDate8() {
    }

    /**
     * @param format
     * @return SimpleDateFormat
     */
    private static DateTimeFormatter getDateFormat(final String format) {
        final String s = format == null ? DEFAULT_DATE_FORMAT : format;
        DateTimeFormatter sdf = dateFormatHashMap.get(s);
        if (sdf == null) {
            try {
                dateFormatHashMap.put(s, sdf = DateTimeFormatter.ofPattern(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sdf;
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getDate(final String date) {
        if (date != null) {
            try {
                return Date.from(LocalDateTime.parse(date, DATEFORMATTER).toInstant(OffsetDateTime.now().getOffset()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param date
     * @param format
     * @return String
     */
    public static String getStringDate(final Date date, final String format) {
        if (date != null) {
            final DateTimeFormatter df = getDateFormat(format);
            if (df != null) {
                try {
                    return df.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    /**
     * @param date
     * @return String
     */
    public static String getStringDate(final Date date) {
        return getStringDate(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * @param date
     * @return String
     */
    public static String getStringDateTime(final Date date) {
        return getStringDate(date, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * @param date
     * @return String
     */
    public static String getStringFullDate(final Date date) {
        return getStringDate(date, DEFAULT_FULLDATE_FORMAT);
    }

    /**
     * @param date
     * @return Date
     */
    private static Date getStartDate(final String date) {
        return getDate(date + " 00:00:00.000");
    }

    /**
     * @param date
     * @return Date
     */
    private static Date getEndDate(final String date) {
        return getDate(date + " 23:59:59.999");
    }

    /**
     * @param date
     * @param stringDate
     * @return int
     */
    private static int compareStartDate(final Date date, final String stringDate) {
        final Date dateOriginal = date == null ? new Date() : date;
        final Date dateStart = getStartDate(stringDate);
        if (dateStart != null) {
            return dateOriginal.compareTo(dateStart);
        }

        return -1;
    }

    /**
     * @param date
     * @param stringDate
     * @return int
     */
    private static int compareEndDate(final Date date, final String stringDate) {
        final Date dateOriginal = date == null ? new Date() : date;
        final Date endDate = getEndDate(stringDate);
        if (endDate != null) {
            return dateOriginal.compareTo(endDate);
        }

        return 1;
    }

    /**
     * @param d
     * @param stringDate
     * @return int
     */
    public static int compareDateTime(final Date d, final String stringDate) {
        final Date dateOriginal = d == null ? new Date() : d;
        final Date date = getDate(stringDate);
        if (date != null) {
            return dateOriginal.compareTo(date);
        }

        return 1;
    }

    /**
     * @param stringDate
     * @return boolean
     */
    public static boolean isDateFrom(final String stringDate) {
        return compareStartDate(null, stringDate) >= 0;
    }

    /**
     * @param stringDate
     * @return boolean
     */
    public static boolean isDateUntil(final String stringDate) {
        return compareEndDate(null, stringDate) <= 0;
    }

    /**
     * @param date
     * @param stringDate
     * @return boolean
     */
    public static boolean isDateFrom(final Date date, final String stringDate) {
        return compareStartDate(date, stringDate) >= 0;
    }

    /**
     * @param date
     * @param stringDate
     * @return boolean
     */
    public static boolean isDateUntil(final Date date, final String stringDate) {
        return compareEndDate(date, stringDate) <= 0;
    }

    /**
     * @param dateBegin
     * @param dateEnd
     * @return boolean
     */
    public static boolean isDateRange(final String dateBegin, final String dateEnd) {
        return isDateRange(null, dateBegin, dateEnd);
    }

    /**
     * @param date
     * @param dateBegin
     * @param dateEnd
     * @return boolean
     */
    public static boolean isDateRange(final Date date, final String dateBegin, final String dateEnd) {
        return isDateFrom(date, dateBegin) && isDateUntil(date, dateEnd);
    }

}
