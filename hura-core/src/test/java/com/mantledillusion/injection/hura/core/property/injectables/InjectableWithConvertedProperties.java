package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

import java.math.BigDecimal;
import java.math.BigInteger;

public class InjectableWithConvertedProperties {

    public static final String PKEY_BOOLEAN = "property.boolean";
    public static final String PKEY_CHARACTER = "property.character";
    public static final String PKEY_NUMBER = "property.number";

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
}
