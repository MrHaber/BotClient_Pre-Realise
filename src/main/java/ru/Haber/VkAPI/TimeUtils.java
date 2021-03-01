package ru.Haber.VkAPI;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getTime(long left) {
        if (left < 1L) {
            return "1 секунда";
        }
        final long m = TimeUnit.MILLISECONDS.toMinutes(left) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(left));
        final long h = TimeUnit.MILLISECONDS.toHours(left) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(left));
        long d = TimeUnit.MILLISECONDS.toDays(left);
        final long y = TimeUnit.MILLISECONDS.toDays(left) / 365L;
        d %= 365L;
        left = TimeUnit.MILLISECONDS.toSeconds(left) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(left));
        String time = "";
        if (y > 0L) {
            time = time + y + " " + formatTime(y, "год", "лет", "лет");
            if (d > 0L || h > 0L || m > 0L || left > 0L) {
                time += " ";
            }
        }
        if (d > 0L) {
            time = time + d + " " + formatTime(d, "день", "дня", "дней");
            if (h > 0L || m > 0L || left > 0L) {
                time += " ";
            }
        }
        if (h > 0L) {
            time = time + h + " " + formatTime(h, "час", "часа", "часов");
            if (m > 0L || left > 0L) {
                time += " ";
            }
        }
        if (m > 0L) {
            time = time + m + " " + formatTime(m, "минута", "минуты", "минут");
            if (left > 0L) {
                time += " ";
            }
        }
        if (left > 0L) {
            time = time + left + " " + formatTime(left, "секунда", "секунды", "секунд");
        }
        return time;
    }
    public static String getTimeUnit(long sec) {
        if (sec < 1L) {
            return "1 секунда";
        }
        long m = sec / 60L;
        sec %= 60L;
        long h = m / 60L;
        m %= 60L;
        long d = h / 24L;
        h %= 24L;
        final long y = d / 365L;
        d %= 365L;
        String time = "";
        if (y > 0L) {
            time = time + y + " " + formatTime(y, "год", "лет", "лет");
            if (d > 0L || h > 0L || m > 0L || sec > 0L) {
                time += " ";
            }
        }
        if (d > 0L) {
            time = time + d + " " + formatTime(d, "день", "дня", "дней");
            if (h > 0L || m > 0L || sec > 0L) {
                time += " ";
            }
        }
        if (h > 0L) {
            time = time + h + " " + formatTime(h, "час", "часа", "часов");
            if (m > 0L || sec > 0L) {
                time += " ";
            }
        }
        if (m > 0L) {
            time = time + m + " " + formatTime(m, "минута", "минуты", "минут");
            if (sec > 0L) {
                time += " ";
            }
        }
        if (sec > 0L) {
            time = time + sec + " " + formatTime(sec, "секунда", "секунды", "секунд");
        }
        return time;
    }
    private static String formatTime(final long num, final String single, final String lessfive, final String others) {
        if (num % 100L > 10L && num % 100L < 15L) {
            return others;
        }
        switch ((int)(num % 10L)) {
            case 1: {
                return single;
            }
            case 2:
            case 3:
            case 4: {
                return lessfive;
            }
            default: {
                return others;
            }
        }
    }
}
