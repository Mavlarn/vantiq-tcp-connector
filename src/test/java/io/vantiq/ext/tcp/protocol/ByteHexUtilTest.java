package io.vantiq.ext.tcp.protocol;

import org.junit.Assert;
import org.junit.Test;

import java.security.InvalidParameterException;

public class ByteHexUtilTest {

    @Test
    public void testEncodeDecodeString() {
        String value1 = "v1";
        byte[] v1Bytes = ByteHexUtil.encodeString(value1, 4);
        Assert.assertTrue(new String(v1Bytes).equals("  v1"));
        String value1Inverse = ByteHexUtil.decodeString(v1Bytes);
        Assert.assertTrue(value1.equals(value1Inverse));

        value1 = "aav1";
        v1Bytes = ByteHexUtil.encodeString(value1, 4);
        value1Inverse = ByteHexUtil.decodeString(v1Bytes);
        Assert.assertTrue(value1.equals(value1Inverse));

        try {
            value1 = "bbaav1";
            v1Bytes = ByteHexUtil.encodeString(value1, 4);
            value1Inverse = ByteHexUtil.decodeString(v1Bytes);
            Assert.assertTrue(false);
        } catch (InvalidParameterException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testEncodeDecodeInt() {
        int value1 = 20;
        byte[] v1Bytes = ByteHexUtil.encodeInteger(value1, 4);
        Assert.assertTrue(new String(v1Bytes).equals("0020"));
        int value1Inverse = ByteHexUtil.decodeInteger(v1Bytes);
        Assert.assertTrue(value1 == value1Inverse);

        value1 = 1200;
        v1Bytes = ByteHexUtil.encodeInteger(value1, 4);
        value1Inverse = ByteHexUtil.decodeInteger(v1Bytes);
        Assert.assertTrue(value1 == value1Inverse);

        try {
            value1 = 12000;
            v1Bytes = ByteHexUtil.encodeInteger(value1, 4);
            value1Inverse = ByteHexUtil.decodeInteger(v1Bytes);
            Assert.assertTrue(false);
        } catch (InvalidParameterException e) {
            Assert.assertTrue(true);
        }
    }
}
