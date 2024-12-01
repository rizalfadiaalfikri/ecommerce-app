package id.orbion.ecommerce_app.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static Date convertLocalDateTimeToDate(LocalDateTime dateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

}
