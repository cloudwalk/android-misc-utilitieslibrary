package io.cloudwalk.utilitieslibrary.utilities;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.US;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.cloudwalk.loglibrary.Log;

public class ByteUtility {
    private static final String
            TAG = ByteUtility.class.getSimpleName();

    /**
     * See <a href="https://bit.ly/2RWydoS">https://bit.ly/2RWydoS</a> for details.
     *
     * @param input {@code byte} array
     * @param length self-describing
     * @return {@link String}
     */
    public static String getHexString(byte[] input, int length) {
        // Log.d(TAG, "getHexString");

        int offset = 0;

        return getHexString(input, offset, length);
    }

    /**
     * {@link ByteUtility#getHexString(byte[], int)} extended.<br>
     * Implementation is permissive: {@code length} and {@code offset} will be recalculated
     * internally to fall between {@code 0} and {@code input.length}.
     *
     * @param input {@code byte} array
     * @param offset self-describing
     * @param length self-describing
     * @return {@code int}
     */
    public static String getHexString(byte[] input, int offset, int length) {
        // Log.d(TAG, "getHexString");

        final byte[] reference = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

        length = Math.max(length, 0);
        length = Math.min(length, input.length);

        offset = Math.max(offset, 0);

        if (length > offset) {
            byte[] response = new byte[(length - offset) * 2];

            for (int i = offset, j = 0; i < length; i++, j++) {
                int value = input[i] & 0xFF;

                response[j * 2]     = reference[value >>> 0x04];
                response[j * 2 + 1] = reference[value &   0x0F];
            }

            return new String(response, UTF_8);
        } else {
            Log.e(TAG, String.format(US, "getHexString::input.length [%d] length [%d] offset [%d]", input.length, length, offset));
        }

        return "";
    }

    /**
     * Same as {@link ByteUtility#clear(byte[])} with {@code input} as
     * {@link ByteArrayOutputStream}.<br>
     * Previous calls to {@link ByteArrayOutputStream#reset()} may render this method useless.
     *
     * @param input {@link ByteArrayOutputStream}
     * @return {@code byte[]}
     */
    public static byte[] clear(@NotNull ByteArrayOutputStream input) {
        // Log.d(TAG, "clear");

        int length = input.size();

        input.reset();

        for (int i = 0; i < length; i++) { input.write((byte) 0x00); }

        return input.toByteArray();
    }

    /**
     * {@link ByteUtility#clear(ByteArrayOutputStream)} extended.<br>
     * Implementation is permissive: {@code size} may be lower than {@code 0} (in which case, no
     * clearing will be done).
     *
     * @param input {@code byte[]}
     * @param size {@code int}
     * @return {@code byte[]}
     */
    public static byte[] clear(@NotNull ByteArrayOutputStream input, int size) {
        // Log.d(TAG, "clear");

        input.reset();

        for (int i = 0; i < size; i++) { input.write((byte) 0x00); }

        return input.toByteArray();
    }

    /**
     * {@link Arrays#fill(byte[], int, int, byte)} with {@code val = (byte) 0x00}.<br>
     * Implementation is permissive: {@code input} may be {@code null}.
     *
     * @param input {@code byte[]}
     * @return {@code byte[]}
     */
    public static byte[] clear(byte[] input) {
        // Log.d(TAG, "clear");

        if (input != null) {
            return clear(input, 0, input.length);
        } else {
            return null;
        }
    }

    /**
     * {@link ByteUtility#clear(byte[])} extended.<br>
     * Implementation is permissive: {@code length} and {@code offset} will be recalculated
     * internally to fall between {@code 0} and {@code input.length}.
     *
     * @param input {@code byte[]}
     * @param offset self-describing
     * @param length self-describing
     * @return {@code byte[]}
     */
    public static byte[] clear(byte[] input, int offset, int length) {
        // Log.d(TAG, "clear");

        if (input != null) {
            length = Math.max(length, 0);
            length = Math.min(length, input.length);

            offset = Math.max(offset, 0);

            if (length > offset) {
                Arrays.fill(input, offset, length, (byte) 0x00);
            } else {
                Log.e(TAG, String.format(US, "clear::input.length [%d] offset [%d] length [%d]", input.length, offset, length));
            }
        }

        return input;
    }

    /**
     * XMODEM CRC16, often falsely identified as CCITT CRC16.
     * See <a href="https://reveng.sourceforge.io/crc-catalogue/16.htm">https://reveng.sourceforge.io/crc-catalogue/16.htm</a> and
     * <a href="https://bit.ly/3xY5mAv">https://bit.ly/3xY5mAv</a> for further details.
     *
     * @param input {@code byte[]}
     * @param offset self-describing
     * @param length self-describing
     * @return {@code byte[]}
     * @throws Exception {@link IllegalArgumentException} if arguments are invalid
     */
    public static byte[] crc16(byte[] input, int offset, int length)
            throws Exception { // TODO: foresee additional CRC types - e.g. crc16(<enum_type>, byte[], ...)
        // Log.d(TAG, "crc16");

        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }

        if (length > input.length) {
            throw new IllegalArgumentException("length > input.length");
        }

        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }

        if (offset >= length) {
            throw new IllegalArgumentException("offset > length");
        }

        int wCRCin = 0x0000;
        int wCPoly = 0x1021;

        for (byte b : input) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);

                wCRCin <<= 1;

                if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
            }
        }

        wCRCin &= 0xffff;
        wCRCin ^= 0x0000;

        String response = String.format(US, "%4.4s", Integer.toHexString(wCRCin)).replace(' ', '0');

        return ByteUtility.fromHexString(response);
    }

    /**
     * Concatenates given {@code byte[]} arguments into single {@code byte[]}.
     *
     * @param input one or more {@code byte[]}
     * @return {@code byte[]}
     */
    public static byte[] concat(byte[]... input) {
        // Log.d(TAG, "concat");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            for (byte[] array : input) {
                stream.write(array);
            }
        } catch (Exception exception) {
            Log.e(TAG, Log.getStackTraceString(exception));
        }

        return stream.toByteArray();
    }

    /**
     * See <a href="https://bit.ly/3rs0Bgj">https://bit.ly/3rs0Bgj</a> for details.
     *
     * @param input {@code byte[]}
     * @return {@code byte[]}
     */
    public static byte[] trim(byte[] input) {
        // Log.d(TAG, "trim");

        int i = input.length - 1;

        while (i >= 0 && input[i] == 0) { --i; }

        return Arrays.copyOf(input, i + 1);
    }

    /**
     * Converts given hexadecimal {@link String} to {@code byte} array.
     * See <a href="https://bit.ly/3Bvd7jU">https://bit.ly/3Bvd7jU</a> for details.
     *
     * @param input {@link String}
     * @return {@code byte}
     */
    public static byte[] fromHexString(String input) {
        // Log.d(TAG, "fromHexString");

        int len = input.length();

        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character.digit(input.charAt(i + 1), 16));
        }

        return data;
    }

    /**
     * Converts given {@code byte[]} to {@code int}.<br>
     * Bear in mind the possibility of overflow according to given {@code length}.<br>
     * Same as {@link ByteUtility#getInt(byte[], int, int)} with {@code offset = 0}.
     *
     * @param input {@code byte[]}
     * @param length self-describing
     * @return {@code int}
     */
    public static int getInt(byte[] input, int length) {
        // Log.d(TAG, "getInt");

        int offset = 0;

        return getInt(input, length, offset);
    }

    /**
     * {@link ByteUtility#getInt(byte[], int)} extended.<br>
     * Implementation is permissive: {@code length} and {@code offset} will be recalculated
     * internally to fall between {@code 0} and {@code input.length}.
     *
     * @param input {@code byte} array
     * @param offset self-describing
     * @param length self-describing
     * @return {@code int}
     */
    public static int getInt(byte[] input, int offset, int length) {
        // Log.d(TAG, "getInt");

        int response = 0;

        length = Math.max(length, 0);
        length = Math.min(length, input.length);

        offset = Math.max(offset, 0);

        for (int i = offset; i < length; i++) {
            response = (response << 8) + (input[i] & 0xFF);
        }

        return response;
    }
}
