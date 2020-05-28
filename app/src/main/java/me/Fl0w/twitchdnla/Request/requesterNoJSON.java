package me.Fl0w.twitchdnla.Request;

public interface requesterNoJSON {
    void onSuccess(String body);

    void onFailure(Throwable e, String response);
}
