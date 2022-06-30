package io.cloudwalk.utilitieslibrary.utilities;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @deprecated As of release 1.1.0, replaced by {@link BundleUtility}, {@link ByteUtility} and
 * {@link StringUtility}.
 */
@Deprecated
public class DataUtility {
    /**
     * @deprecated As of release 1.1.0, replaced by {@link BundleUtility#getJSONObject(Bundle)}.
     */
    @Deprecated
    public static JSONObject getJSONObjectFromBundle(@NotNull Bundle input) {
        return BundleUtility.getJSONObject(input);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link BundleUtility#getJSONObject(Bundle, boolean)}.
     */
    @Deprecated
    public static JSONObject getJSONObjectFromBundle(@NotNull Bundle input, boolean sort) {
        return BundleUtility.getJSONObject(input, sort);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link StringUtility#digest(String, String)}.
     */
    @Deprecated
    public static String digest(@NotNull String algorithm, @NotNull String input) {
        return StringUtility.digest(algorithm, input);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#getHexString(byte[], int)}.
     */
    @Deprecated
    public static String getHexStringFromByteArray(byte[] input) {
        return ByteUtility.getHexString(input, input.length);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#getHexString(byte[], int, int)}.
     */
    @Deprecated
    public static String getHexStringFromByteArray(byte[] input, int length, int offset) {
        return ByteUtility.getHexString(input, offset, length);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link StringUtility#mask(String, int, int)}.
     */
    @Deprecated
    public static String mask(@NotNull String input, int ll, int rr) {
        return StringUtility.mask(input, ll, rr);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#crc16(byte[], int, int)}.
     */
    @Deprecated
    public static byte[] CRC16_XMODEM(byte[] input) {
        byte[] response = null;

        try {
            response = ByteUtility.crc16(input, 0, input.length);
        } catch (Exception ignored) { }

        return response;
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#concat(byte[]...)}.
     */
    @Deprecated
    public static byte[] concatByteArray(byte[]... input) {
        return ByteUtility.concat(input);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#fromHexString(String)}.
     */
    @Deprecated
    public static byte[] getByteArrayFromHexString(String input) {
        return ByteUtility.fromHexString(input);
    }

    /**
     * @deprecated As of release 1.1.0, prefer {@link String#getBytes(Charset)}.
     */
    @Deprecated
    public static byte[] getByteArrayFromString(Charset charset, String input) {
        return input.getBytes(charset);
    }

    /**
     * @deprecated As of release 1.1.0, prefer {@link String#getBytes(Charset)} with
     * {@link StandardCharsets#UTF_8}.
     */
    @Deprecated
    public static byte[] getByteArrayFromString(String input) {
        return input.getBytes(UTF_8);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#trim(byte[])}.
     */
    @Deprecated
    public static byte[] trimByteArray(byte[] input) {
        return ByteUtility.trim(input);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#getInt(byte[], int)}.
     */
    @Deprecated
    public static int getIntFromByteArray(byte[] input, int length) {
        return ByteUtility.getInt(input, length);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link ByteUtility#getInt(byte[], int, int)}.
     */
    @Deprecated
    public static int getIntFromByteArray(byte[] input, int length, int offset) {
        return ByteUtility.getInt(input, offset, length);
    }

    /**
     * @deprecated As of release 1.1.0, replaced by {@link StringUtility#binarySearch(String[], String)}.
     */
    @Deprecated
    public static int stringBinarySearch(@NotNull String[] haystack, @NotNull String needle) {
        return StringUtility.binarySearch(haystack, needle);
    }
}
