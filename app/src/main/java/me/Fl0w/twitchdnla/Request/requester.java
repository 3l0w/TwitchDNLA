package me.Fl0w.twitchdnla.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface requester {
    void onSuccess(JSONObject response) throws JSONException;

    void onSuccess(JSONArray response) throws JSONException;

    void onFailure(Throwable e);
}
