package io.cloudwalk.utilitieslibrary.utilities;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.cloudwalk.loglibrary.Log;

public class BundleUtility {
    private static final String
            TAG = BundleUtility.class.getSimpleName();

    /**
     * Converts given {@link List} to {@link JSONArray}.
     *
     * @param list {@link List}
     * @return {@link JSONArray}
     */
    private static JSONArray _getJSONArray(List list) {
        // Log.d(TAG, "_getJSONArrayFromList");

        JSONArray array = new JSONArray();

        for (Object object : list) {
            if (object instanceof Bundle) {
                array.put(getJSONObject((Bundle) object));
            } else if (object instanceof List) {
                array.put(_getJSONArray((List) object));
            } else {
                array.put(object);
            }
        }

        return array;
    }

    /**
     * See {@link BundleUtility#getJSONObject(Bundle, boolean)}.
     *
     * @param input {@link Bundle}
     * @return {@link JSONObject}
     */
    public static JSONObject getJSONObject(@NotNull Bundle input) {
        // Log.d(TAG, "getJSONObject");

        return getJSONObject(input, false);
    }

    /**
     * Converts given {@link Bundle} to {@link JSONObject}.<br>
     * Bear in mind:<br>,
     * <ul>
     *   <li>Harold L. points out "The JSON spec. says you CAN escape forward slash", therefore {@code //} will be outputted as {@code \/\/}.</li>
     *   <li>{@link Bundle} and {@link JSONObject} aren't strictly equivalent, therefore are limitations to the conversion between them.</li>
     * </ul>
     *
     * @param input {@link Bundle}
     * @param sort indicates whether the collection of keys must be sorted lexicographically
     * @return {@link JSONObject}
     */
    public static JSONObject getJSONObject(@NotNull Bundle input, boolean sort) {
        // Log.d(TAG, "getJSONObject");

        List<String> keySet = new ArrayList<>(0);

        if (sort) {
            keySet.addAll(input.keySet());

            Collections.sort(keySet);
        }

        JSONObject response = new JSONObject();

        for (String key : (sort) ? keySet : input.keySet()) {
            try {
                Object item = input.get(key);

                assert response != null;

                if (item instanceof Bundle) {
                    response.put(key, getJSONObject((Bundle) item, sort));
                } else if (item instanceof List) {
                    response.put(key, _getJSONArray((List) item));
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
}
