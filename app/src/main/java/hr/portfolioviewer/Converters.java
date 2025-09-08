package hr.portfolioviewer;

import androidx.room.TypeConverter;
import java.math.BigDecimal;

public class Converters {
    @TypeConverter
    public static BigDecimal fromString(String value) {
        return value == null || value.equals("undefined") ? BigDecimal.ZERO : new BigDecimal(value);
    }

    @TypeConverter
    public static String bigDecimalToString(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO.toPlainString() : bigDecimal.toPlainString();
    }
}