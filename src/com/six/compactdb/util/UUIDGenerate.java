package com.six.compactdb.util;

import java.net.InetAddress;

public class UUIDGenerate {
    private static final UUIDGenerate uuid = new UUIDGenerate();
    private static final int IP;
    private static short counter;
    private static final int JVM;

    static {
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception var2) {
            ipadd = 0;
        }

        IP = ipadd;
        counter = 0;
        JVM = (int)(System.currentTimeMillis() >>> 8);
    }

    private UUIDGenerate() {
    }

    private int getJVM() {
        return JVM;
    }

    private short getCount() {
        synchronized(UUIDGenerate.class) {
            if(counter < 0) {
                counter = 0;
            }

            return counter++;
        }
    }

    private int getIP() {
        return IP;
    }

    private short getHiTime() {
        return (short)((int)(System.currentTimeMillis() >>> 32));
    }

    private int getLoTime() {
        return (int)System.currentTimeMillis();
    }

    private static int toInt(byte[] bytes) {
        int result = 0;

        for(int i = 0; i < 4; ++i) {
            result = (result << 8) - -128 + bytes[i];
        }

        return result;
    }

    private String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    private String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    private String generate() {
        return (new StringBuffer(32)).append(this.format(this.getIP())).append(this.format(this.getJVM())).append(this.format(this.getHiTime())).append(this.format(this.getLoTime())).append(this.format(this.getCount())).toString();
    }

    public static String getUUID() {
        return uuid.generate();
    }
}
