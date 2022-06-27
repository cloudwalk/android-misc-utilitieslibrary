package io.cloudwalk.utilitieslibrary.utilities;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;

import io.cloudwalk.loglibrary.Log;

public class StringUtility {
    private static final String
            TAG = StringUtility.class.getSimpleName();

    /**
     * Digests {@code input} with {@code algorithm}.<br>
     * If {@code }algorithm} isn't available or known, returns an empty {@link String}.
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
     * Masks given {@code input} between indexes {@code ll} and {@code rr}.<br>
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
     * {@link String} binary search.<br>
     * Bear in mind a binary search requires {@code haystack} properly sorted.
     *
     * @param haystack self-describing
     * @param needle self-describing
     * @return either the position of {@code needle} on the {@code haystack} or -1
     */
    public static int binarySearch(@NotNull String[] haystack, @NotNull String needle) {
        // Log.d(TAG, "binarySearch");

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
