package com.app7ub.movies.utils.stream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class StreamSB {
    public interface StreamSBCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }

    public static void getStreamSBString(Context context, String code, final StreamSB.StreamSBCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, "https://cloud.team-dooo.com/Dooo/stream/StreamSB/get_StreamSB_string.php?code="+ code, callback::onSuccess, error -> callback.onError(error));
        queue.add(sr);
    }
}
