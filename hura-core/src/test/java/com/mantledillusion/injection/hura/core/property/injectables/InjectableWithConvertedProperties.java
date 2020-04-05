package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;

public class InjectableWithConvertedProperties {

    public static final String PKEY_BOOLEAN = "property.boolean";
    public static final String PKEY_CHARACTER = "property.character";
    public static final String PKEY_NUMBER = "property.number";
    public static final String PKEY_LOCALDATE = "property.localDate";
    public static final String PKEY_LOCALTIME = "property.localTime";
    public static final String PKEY_LOCALDATETIME = "property.localDateTime";
    public static final String PKEY_OFFSETTIME = "property.offsetTime";
    public static final String PKEY_OFFSETDATETIME = "property.offsetDateTime";
    public static final String PKEY_ZONEDDATETIME = "property.zonedDateTime";
    public static final String PKEY_INSTANT = "property.instance";
    public static final String PKEY_PERIOD = "property.period";
    public static final String PKEY_DURATION = "property.duration";

    @Resolve("${"+PKEY_BOOLEAN+"}")
    public boolean booleanProperty;
    @Resolve("${"+PKEY_BOOLEAN+"}")
    public Boolean BooleanProperty;

    @Resolve("${"+PKEY_CHARACTER+"}")
    public char charProperty;
    @Resolve("${"+PKEY_CHARACTER+"}")
    public Character CharacterProperty;

    @Resolve("${"+PKEY_NUMBER+"}")
    public byte byteNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public Byte ByteNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public short shortNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public Short ShortNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public int intNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public Integer IntegerNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public long longNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public Long LongNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public float floatNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public Float FloatNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public double doubleNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public Double DoubleNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public BigInteger BigIntegerNumber;
    @Resolve("${"+PKEY_NUMBER+"}")
    public BigDecimal BigDecimalNumber;

    @Resolve("${"+PKEY_LOCALDATE+"}")
    public LocalDate localDate;
    @Resolve("${"+PKEY_LOCALTIME+"}")
    public LocalTime localTime;
    @Resolve("${"+PKEY_LOCALDATETIME+"}")
    public LocalDateTime localDateTime;
    @Resolve("${"+PKEY_OFFSETTIME+"}")
    public OffsetTime offsetTime;
    @Resolve("${"+PKEY_OFFSETDATETIME+"}")
    public OffsetDateTime offsetDateTime;
    @Resolve("${"+PKEY_ZONEDDATETIME+"}")
    public ZonedDateTime zonedDateTime;
    @Resolve("${"+ PKEY_INSTANT +"}")
    public Instant instant;
    @Resolve("${"+ PKEY_PERIOD+"}")
    public Period period;
    @Resolve("${"+ PKEY_DURATION +"}")
    public Duration duration;
}
