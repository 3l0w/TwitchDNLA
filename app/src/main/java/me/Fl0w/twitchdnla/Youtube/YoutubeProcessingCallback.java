package me.Fl0w.twitchdnla.Youtube;

public interface YoutubeProcessingCallback {
    void onSuccess(String URL,String thumbnail);

    void onFailure(Throwable e);
}
