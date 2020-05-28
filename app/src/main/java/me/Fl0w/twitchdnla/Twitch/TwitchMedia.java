package me.Fl0w.twitchdnla.Twitch;

public class TwitchMedia {
    private final String quality;
    private final String URL;
    public TwitchMedia(String quality,String URL){
        this.quality = quality;
        this.URL = URL;
    }

    @Override
    public String toString() {
        return this.getQuality() + ", " + this.getURL();
    }

    public String getQuality() {
        return quality;
    }

    public String getURL() {
        return URL;
    }
}

