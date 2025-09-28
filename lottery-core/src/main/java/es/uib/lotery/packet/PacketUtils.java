package es.uib.lotery.packet;

import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;

@UtilityClass
public class PacketUtils {

    public static void putString(ByteBuffer buffer, String str) {
        byte[] bytes = str.getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    public static void putByte(ByteBuffer buffer, byte b) {
        buffer.put(b);
    }
    public static void putInt(ByteBuffer buffer, int value) {
        buffer.putInt(value);
    }

    public static void putLong(ByteBuffer buffer, long value) {
        buffer.putLong(value);
    }

    public static String readString(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        return new String(bytes);
    }

    public static byte readByte(ByteBuffer buffer) {
        return buffer.get();
    }
    public static int getInt(ByteBuffer buffer) {
        return buffer.getInt();
    }

    public static long getLong(ByteBuffer buffer) {
        return buffer.getLong();
    }
}
