package es.uib.lotery.packet;

import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;

/**
 * Utility class for encoding and decoding primitive data types and strings
 * into and from {@link ByteBuffer} objects.
 * <p>
 * Provides methods for reading and writing {@code byte}, {@code int}, {@code long},
 * {@code boolean}, and {@link String} values in a consistent serialization format.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * PacketUtils.putInt(buffer, 42);
 * buffer.flip();
 * int value = PacketUtils.getInt(buffer);
 * }</pre>
 */
@UtilityClass
public class PacketUtils {

    /**
     * Writes a {@link String} to the buffer.
     * The string is serialized as an integer length followed by the UTF-8 bytes.
     *
     * @param buffer the buffer to write into
     * @param str the string to serialize
     */
    public static void putString(ByteBuffer buffer, String str) {
        byte[] bytes = str.getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    /**
     * Writes a {@code boolean} value as a single byte (1 for true, 0 for false).
     *
     * @param buffer the buffer to write into
     * @param b the boolean value
     */
    public static void putBoolean(ByteBuffer buffer, boolean b) {
        buffer.put((byte) (b ? 1 : 0));
    }

    /** Writes a single byte into the buffer. */
    public static void putByte(ByteBuffer buffer, byte b) {
        buffer.put(b);
    }

    /** Writes an integer (4 bytes) into the buffer. */
    public static void putInt(ByteBuffer buffer, int value) {
        buffer.putInt(value);
    }

    /** Writes a long (8 bytes) into the buffer. */
    public static void putLong(ByteBuffer buffer, long value) {
        buffer.putLong(value);
    }

    /**
     * Reads a {@link String} from the buffer.
     * Expects the string to be serialized as an integer length followed by UTF-8 bytes.
     *
     * @param buffer the buffer to read from
     * @return the decoded string
     */
    public static String getString(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        return new String(bytes);
    }

    /** Reads a boolean from the buffer (1 byte, true if 1, false if 0). */
    public static boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() == 1;
    }

    /** Reads a single byte from the buffer. */
    public static byte getByte(ByteBuffer buffer) {
        return buffer.get();
    }

    /** Reads an integer (4 bytes) from the buffer. */
    public static int getInt(ByteBuffer buffer) {
        return buffer.getInt();
    }

    /** Reads a long (8 bytes) from the buffer. */
    public static long getLong(ByteBuffer buffer) {
        return buffer.getLong();
    }
}
