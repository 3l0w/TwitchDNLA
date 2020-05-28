package me.Fl0w.twitchdnla.Youtube;

public class YoutubeMedia {
    private final String quality;
    private final int id;
    private String URL;

    public YoutubeMedia(String quality, int id,String URL) {
        this.quality = quality;
        this.id = id;
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "quality: "+ this.quality + ", URL: " + this.URL + ", id: "+this.id;
    }

    public String getQuality() {
        return quality;
    }

    public String getURL() {
        return URL;
    }

    public int getId() {
        return id;
    }
}
