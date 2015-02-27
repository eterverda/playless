package io.github.eterverda.playless.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public final class TimestampUtils {
    private static final AtomicReference<SimpleDateFormat> TIMESTAMP_FORMAT = new AtomicReference<>();

    private TimestampUtils() {
    }

    public static String zulu(long timestamp) {
        final SimpleDateFormat usedFormat = TIMESTAMP_FORMAT.getAndSet(null);
        final SimpleDateFormat format = usedFormat != null ? usedFormat : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String result = format.format(new Date(timestamp));

        TIMESTAMP_FORMAT.set(format);

        return result;
    }
}
