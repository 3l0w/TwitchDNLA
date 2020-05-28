package me.Fl0w.twitchdnla.Youtube;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.YoutubeDLResponse;
import me.Fl0w.twitchdnla.LoadingDialog;
import me.Fl0w.twitchdnla.Request.request;
import me.Fl0w.twitchdnla.Request.requesterNoJSON;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeProcessing {
    public YoutubeProcessing(Context context, String url, FragmentManager fragmentManager, YoutubeProcessingCallback callback) {
        DialogFragment progressDialog = new LoadingDialog();
        progressDialog.show(fragmentManager, "TwitchDialogFragment");
        request.get("http://www.youtube.com/oembed?url=" + url, new requesterNoJSON() {
            @Override
            public void onSuccess(String body) {
                if (!body.equals("Not Found")) {
                    YoutubeDLRequest request = new YoutubeDLRequest(url);
                    request.setOption("-f", "best");
                    request.setOption("--get-url");
                    new Thread() {
                        @Override
                        public void run() {
                            YoutubeDLResponse response;
                            try {
                                response = YoutubeDL.getInstance().execute(request);
                                String stdOut = response.getOut();

                                String result = null;
                                if ((stdOut != null) && (stdOut.length() > 0)) {
                                    result = stdOut.substring(0, stdOut.length() - 1);
                                }
                                String thumbnail = "https://img.youtube.com/vi/"+getVideoID(url)+"/sddefault.jpg";
                                callback.onSuccess(result,thumbnail);
                                progressDialog.dismiss();


                            } catch (YoutubeDLException e) {
                                callback.onFailure(e);
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                    }.start();
                } else {
                    Toast.makeText(context, "Video cannot be found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable e, String response) {
                progressDialog.dismiss();
                callback.onFailure(e);
            }
        });
    }
    private String getVideoID(String url){
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
