package hr.portfolioviewer;

import androidx.room.TypeConverter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static BigDecimal fromString(String value) {
        return value == null || value.equals("undefined") ? BigDecimal.ZERO : new BigDecimal(value);
    }

    @TypeConverter
    public static String bigDecimalToString(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO.toPlainString() : bigDecimal.toPlainString();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromInvestmentList(List<Investment> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Investment> toInvestmentList(String value) {
        Type listType = new TypeToken<List<Investment>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromInvestment(Investment investment) {
        return gson.toJson(investment);
    }

    @TypeConverter
    public static Investment toInvestment(String json) {
        return new Gson().fromJson(json, Investment.class);
    }

    @TypeConverter
    public static Long fromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        return dateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(Long millis) {
        if (millis == null) return null;

        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}