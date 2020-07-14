package io.vantiq.ext.tcp;

import io.netty.buffer.ByteBufUtil;
import org.junit.Test;

import java.math.BigInteger;

public class ByteTest {

    @Test
    public void testHex2Byte() {
        byte[] cmd = new BigInteger("4CE6", 16).toByteArray();
        System.out.println("Command:" + cmd);
    }

    @Test
    public void testHex2Byte2() {
        Integer.parseInt("4C", 16);
        System.out.println("Command:" + Integer.parseInt("4C", 16));
    }

    @Test
    public void testString2Hex() {
        String str = "GTLAND";
        StringBuffer sb = new StringBuffer();
        //Converting string to character array
        char ch[] = str.toCharArray();
        for(int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        System.out.println(result);
    }

    @Test
    public void testByte2Hex() {
        byte[] cmd = "GTLAND".getBytes();;
        String result = ByteBufUtil.hexDump(cmd);
        System.out.println("result:" + result);

        System.out.println("result inverse:" + new String(ByteBufUtil.decodeHexDump(result)));
    }

    @Test
    public void testInt2Hex() {
        Integer test = 12000;
        String result = ByteBufUtil.hexDump(test.toString().getBytes());
        System.out.println("result:" + result);

        System.out.println("result inverse:" + new String(ByteBufUtil.decodeHexDump(result)));
    }

    @Test
    public void testByteToString() {
        byte b1 = (byte)30;
        System.out.println("result:[" + new Character((char)b1) + "]");

        System.out.println("byte of 0:[" + (byte)0 + "]");

    }
}
