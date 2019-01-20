package pw.forcide.hub.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils
{
    public static String formatLetters(final int i) {
        final int r = i * 1000;
        final int sec = r / 1000 % 60;
        final int min = r / 60000 % 60;
        final int hour = r / 3600000 % 24;
        final int days = r / 86400000 % 365;
        return String.valueOf((days > 0) ? new StringBuilder(String.valueOf(days)).append("d").toString() : "") + ((hour > 0) ? (String.valueOf(hour) + "h") : "") + ((min < 10) ? ("0" + min + "m") : (String.valueOf(min) + "m")) + ((sec < 10) ? ("0" + sec + "s") : (String.valueOf(sec) + "s"));
    }
    
    public static String format(final int i) {
        final int r = i * 1000;
        final int sec = r / 1000 % 60;
        final int min = r / 60000 % 60;
        final int h = r / 3600000 % 24;
        return String.valueOf((h > 0) ? new StringBuilder(String.valueOf(h)).append(":").toString() : "") + ((min < 10) ? ("0" + min) : min) + ":" + ((sec < 10) ? ("0" + sec) : sec);
    }
    
    public static long parse(final String s) {
        long result = 0L;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (Character.isDigit(c)) {
                b.append(c);
            }
            else {
                final String str;
                if (Character.isLetter(c) && !(str = b.toString()).isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    b = new StringBuilder();
                }
            }
        }
        return result;
    }
    
    private static long convert(final int value, final char unit) {
        switch (unit) {
            case 'y': {
                return value * TimeUnit.DAYS.toMillis(365L);
            }
            case 'M': {
                return value * TimeUnit.DAYS.toMillis(30L);
            }
            case 'd': {
                return value * TimeUnit.DAYS.toMillis(1L);
            }
            case 'h': {
                return value * TimeUnit.HOURS.toMillis(1L);
            }
            case 'm': {
                return value * TimeUnit.MINUTES.toMillis(1L);
            }
            case 's': {
                return value * TimeUnit.SECONDS.toMillis(1L);
            }
            default: {
                return -1L;
            }
        }
    }
}
