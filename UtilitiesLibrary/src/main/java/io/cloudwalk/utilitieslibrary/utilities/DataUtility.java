package io.cloudwalk.utilitieslibrary.utilities;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.cloudwalk.loglibrary.Log;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.US;

public class DataUtility {
    private static final String
            TAG = DataUtility.class.getSimpleName();

    /**
     * Converts given {@link List} to {@link JSONArray}.
     *
     * @param list {@link List}
     * @return {@link JSONArray}
     */
    private static JSONArray _getJSONArrayFromList(List list) {
        // Log.d(TAG, "_getJSONArrayFromList");

        JSONArray jsonArray = new JSONArray();

        for (Object object : list) {
            if (object instanceof List) {
                jsonArray.put(_getJSONArrayFromList((List) object));
            } else if (object instanceof Bundle) {
                jsonArray.put(getJSONObjectFromBundle((Bundle) object));
            } else {
                jsonArray.put(object);
            }
        }

        return jsonArray;
    }

    /**
     * See {@link DataUtility#getJSONObjectFromBundle(Bundle, boolean)}.
     *
     * @param input {@link Bundle}
     * @return {@link JSONObject}
     */
    public static JSONObject getJSONObjectFromBundle(@NotNull Bundle input) {
        return getJSONObjectFromBundle(input, false);
    }

    /**
     * Converts given {@link Bundle} to {@link JSONObject}.
     *
     * @param input {@link Bundle}
     * @param sort indicates whether the collection of keys must be sorted lexicographically
     * @return {@link JSONObject}
     */
    public static JSONObject getJSONObjectFromBundle(@NotNull Bundle input, boolean sort) {
        // Log.d(TAG, "getJSONObjectFromBundle");

        List<String> keySet = new ArrayList<>(0);

        if (sort) {
            keySet.addAll(input.keySet());

            Collections.sort(keySet);
        }

        JSONObject response = new JSONObject();

        for (String key : (sort) ? keySet : input.keySet()) {
            try {
                Object item = input.get(key);

                if (item instanceof Bundle) {
                    response.put(key, getJSONObjectFromBundle((Bundle) item, sort));
                } else if (item instanceof List) {
                    response.put(key, _getJSONArrayFromList((List) item));
                } else {
                    response.put(key, item);
                }
            } catch (Exception exception) {
                Log.e(TAG, Log.getStackTraceString(exception));

                response = null;
            }
        }

        return response;
    }

    /**
     * Digests an {@code input} using the specified {@code algorithm}.<br>
     * If the algorithm isn't available or known, the return will be an empty {@link String}.
     *
     * @param algorithm e.g. "MD5", "SHA", "SHA-256", etc.
     * @param input {@link String}
     * @return {@link String}
     */
    public static String digest(@NotNull String algorithm, @NotNull String input) {
        // Log.d(TAG, "digest");

        StringBuilder response = new StringBuilder();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

            byte[] byteInput = input.getBytes(UTF_8);

            messageDigest.update(byteInput, 0, byteInput.length);

            byte[] byteDigest = messageDigest.digest();

            for (int i = 0; i < byteDigest.length; i++) {
                response.append(Integer.toString(( byteDigest[i] & 0xFF ) + 0x100, 16).substring(1));
            }
        } catch (Exception exception) {
            Log.e(TAG, Log.getStackTraceString(exception));
        }

        return response.toString();
    }

    /**
     * See <a href="https://bit.ly/2RWydoS">https://bit.ly/2RWydoS</a> for details.
     *
     * @param input {@code byte} array
     * @return {@link String}
     */
    public static String getHexStringFromByteArray(@NotNull byte[] input) {
        // Log.d(TAG, "getHexStringFromByteArray");

        return getHexStringFromByteArray(input, input.length, 0);
    }

    /**
     * Extended version of {@link DataUtility#getHexStringFromByteArray(byte[])} for partial
     * conversion.
     *
     * @param input {@code byte} array
     * @param length self-describing
     * @param offset self-describing
     * @return {@link String}
     */
    private static String getHexStringFromByteArray(@NotNull byte[] input, int length, int offset) {
        // Log.d(TAG, "getHexStringFromByteArray");

        final byte[] reference = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

        length = Math.max(length, 0);
        length = Math.min(length, input.length);

        offset = Math.max(offset, 0);

        if (length > offset) {
            byte[] response = new byte[length * 2];
            int    boundary = Math.min(length, input.length);

            for (int i = offset, j = 0; i < boundary; i++, j++) {
                int value = input[i] & 0xFF;

                response[j * 2]     = reference[value >>> 0x04];
                response[j * 2 + 1] = reference[value &   0x0F];
            }

            return new String(response, UTF_8);
        } else {
            Log.e(TAG, String.format(US, "_getHexStringFromByteArray::input.length [%d] length [%d] offset [%d]", input.length, length, offset));
        }

        return "";
    }

    /**
     * Masks data from given {@code input} between indexes {@code ll} and {@code rr}.<br>
     * Ignores whitespaces.
     *
     * @param input {@link String}
     * @param rr {@code int}
     * @param ll {@code int}
     * @return {@link String}
     */
    public static String mask(@NotNull String input, int ll, int rr) {
        // Log.d(TAG, "mask");

        String response = new String(input);

        if (ll < 0 || rr < 0) {
            return response;
        }

        if ((response.length() - ll - rr) <= 0) {
            return response;
        }

        for (int i = ll; i < response.length() - rr; i++) {
            char candidate = response.charAt(i);

            if (candidate != ' ') {
                response = response.substring(0, i) + '*' + response.substring(i + 1);
            }
        }

        return response;
    }

    /**
     * See <a href="https://bit.ly/3xY5mAv">https://bit.ly/3xY5mAv</a> for details.
     *
     * @param input {@code byte[]}
     * @return {@code byte[]}
     */
    public static byte[] CRC16_XMODEM(byte[] input) {
        // Log.d(TAG, "CRC16_XMODEM");

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

        String trace = String.format(US, "%4.4s", Integer.toHexString(wCRCin)).replace(' ', '0');

        return DataUtility.getByteArrayFromHexString(trace);
    }

    /**
     * Concatenates variable number of given {@code byte[]} arguments into single new
     * {@code byte[]}.
     *
     * @param input one or more {@code byte[]}
     * @return {@code byte[]}
     */
    public static byte[] concatByteArray(byte[]... input) {
        // Log.d(TAG, "concatByteArray");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            for (byte[] array : input) {
                stream.write(array);
            }
        } catch (Exception exception) {
            Log.e(TAG, Log.getStackTraceString(exception));

            return null;
        }

        return stream.toByteArray();
    }

    /**
     * Converts given hexadecimal {@link String} to {@code byte} array.
     * See <a href="https://bit.ly/3Bvd7jU">https://bit.ly/3Bvd7jU</a> for details.
     *
     * @param input {@link String}
     * @return {@code byte}
     */
    public static byte[] getByteArrayFromHexString(String input) {
        // Log.d(TAG, "getByteArrayFromHexString");

        int len = input.length();

        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character.digit(input.charAt(i + 1), 16));
        }

        return data;
    }

    /**
     * See {@link String#getBytes(Charset)}.
     *
     * @param charset {@link StandardCharsets}
     * @param input {@link String}
     * @return {@code byte[]}
     */
    public static byte[] getByteArrayFromString(Charset charset, String input) {
        // Log.d(TAG, "getByteArrayFromString");

        return input.getBytes(charset);
    }

    /**
     * See {@link String#getBytes(Charset)}, taking into consideration
     * {@link StandardCharsets#UTF_8}.
     *
     * @param input {@link String}
     * @return {@code byte[]}
     */
    public static byte[] getByteArrayFromString(String input) {
        // Log.d(TAG, "getByteArrayFromString");

        return input.getBytes(UTF_8);
    }

    /**
     * See <a href="https://bit.ly/3rs0Bgj">https://bit.ly/3rs0Bgj</a> for details.
     *
     * @param input {@code byte[]}
     * @return {@code byte[]}
     */
    public static byte[] trimByteArray(byte[] input) {
        // Log.d(TAG, "trimByteArray");

        int i = input.length - 1;

        while (i >= 0 && input[i] == 0) { --i; }

        return Arrays.copyOf(input, i + 1);
    }

    /**
     * Converts given {@code byte[]} to {@code int}.<br>
     * One must consider the possibility of overflow according to given {@code length}.
     *
     * @param input {@code byte[]}
     * @param length {@code int}
     * @return {@code int}
     */
    public static int getIntFromByteArray(byte[] input, int length) {
        // Log.d(TAG, "getIntFromByteArray");

        return getIntFromByteArray(input, length, 0);
    }

    /**
     * Extended version of {@link DataUtility#getIntFromByteArray(byte[], int)} for partial
     * conversion.
     *
     * @param input {@code byte} array
     * @param length self-describing
     * @param offset self-describing
     * @return {@code int}
     */
    public static int getIntFromByteArray(byte[] input, int length, int offset) {
        // Log.d(TAG, "getIntFromByteArray");

        int response = -1;

        length = Math.max(length, 0);
        length = Math.min(length, input.length);

        offset = Math.max(offset, 0);

        for (int i = offset; i < length; i++) {
            response = (response << 8) + (input[i] & 0xFF);
        }

        return response;
    }

    /**
     * {@link String} binary search.<br>
     * Bear in mind binary searches require a {@code haystack} properly sorted.
     *
     * @param haystack self-describing
     * @param needle self-describing
     * @return either the position of {@code needle} on the {@code haystack} or -1
     */
    public static int stringBinarySearch(@NotNull String[] haystack, @NotNull String needle) {
        // Log.d(TAG, "stringBinarySearch");

        int left = 0, right = haystack.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            int res = needle.compareTo(haystack[middle]);

            if (res == 0) {
                return middle;
            }
            if (res > 0) {
                left = middle + 1;
            }
            else {
                right = middle - 1;
            }
        }

        return -1;
    }
}
