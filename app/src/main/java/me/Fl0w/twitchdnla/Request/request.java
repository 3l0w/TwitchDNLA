package me.Fl0w.twitchdnla.Request;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class request {
    public static void get(String url,int timeout, final requesterNoJSON listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(timeout);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                listener.onSuccess(new String(responseBody));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (error.toString().equals("java.net.SocketTimeoutException")) {
                    listener.onFailure(error, "Timeout");
                } else if (responseBody == null) {
                    listener.onFailure(error, null);
                } else {
                    listener.onFailure(error, responseBody.toString());
                }
            }
        });
    }

    public static void get(String url, final requesterNoJSON listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                listener.onSuccess(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (error.toString().equals("java.net.SocketTimeoutException")) {
                    listener.onFailure(error, "Timeout");
                } else if (responseBody == null) {
                    listener.onFailure(error, null);
                } else {
                    listener.onFailure(error, responseBody.toString());
                }
            }
        });
    }

    public static void get(String url, final requester listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    listener.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response){
                try {
                    listener.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                listener.onFailure(e);
            }
        });
    }

    public static void post(String url, final requester listener) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000);
        client.post(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    listener.onSuccess(response);
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                listener.onFailure(e);
            }
        });
    }

    public static void put(String url, final requester listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000);
        client.put(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    listener.onSuccess(response);
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                listener.onFailure(e);
            }
        });
    }
}

