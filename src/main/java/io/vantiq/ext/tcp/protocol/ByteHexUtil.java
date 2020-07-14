package io.vantiq.ext.tcp.protocol;

import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.security.InvalidParameterException;

public class ByteHexUtil {

    private static final char INT_FILL_PAD = '0';
    private static final char STRING_FILL_PAD = ' ';


    public static byte[] hexStringToByte(String hexString) {
        return new BigInteger(hexString, 16).toByteArray();
    }

    public static byte[] encodeInteger(int value, int length) {
        String valueStr = Integer.valueOf(value).toString();
        if (valueStr.length() > length) {
            throw new InvalidParameterException("Invalid length " + length + " for value:" + valueStr);
        } else {
            return StringUtils.leftPad(valueStr, length, INT_FILL_PAD).getBytes();
        }
    }

    public static Integer decodeInteger(byte[] value) {
        return Integer.valueOf(new String(value));
    }

    public static byte[] encodeString(String value, int length) {
        if (value.length() > length) {
            throw new InvalidParameterException("Invalid length " + length + " for string:" + value);
        }
        return StringUtils.leftPad(value, length, STRING_FILL_PAD).getBytes();
    }

    public static String decodeString(byte[] value) {
        return new String(value).trim();
    }
}
