package me.Fl0w.twitchdnla.Twitch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.Fl0w.twitchdnla.LoadingDialog;
import me.Fl0w.twitchdnla.R;
import me.Fl0w.twitchdnla.Request.request;
import me.Fl0w.twitchdnla.Request.requester;
import me.Fl0w.twitchdnla.Request.requesterNoJSON;


public class TwitchMediaSelectDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        FragmentActivity activity = getActivity();
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_twitch, null));
        builder.setPositiveButton("Valid", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                Dialog dialog = (Dialog) dialogInterface;
                Context context = dialog.getContext();
                DialogFragment loadingDialog = new LoadingDialog();
                loadingDialog.show(activity.getSupportFragmentManager(), "loadingDialog");

                final EditText entry = getDialog().findViewById(R.id.twitchChannelEntry);
                final String text = entry.getText().toString().toLowerCase();
                //fixed error 201 Gone with my client ID 6c7kkuf1dyl50bpomqxw73p4vv20ac
                request.get("https://api.twitch.tv/api/channels/" + text + "/access_token?client_id=kimne78kx3ncx6brgo4mv6wki5h1ko", new requester() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        String random = String.valueOf(Math.floor(Math.random() * 1E7));
                        final String url = "https://usher.ttvnw.net/api/channel/hls/" + text + ".m3u8?player=twitchweb&token=" + response.getString("token") + "&sig=" + response.getString("sig") + "&$allow_audio_only=true&allow_source=true&type=any&p=" + random;
                        request.get(url, new requesterNoJSON() {
                            @Override
                            public void onSuccess(String body) {

                                final Pattern pattern = Pattern.compile("^#EXT-X-MEDIA:.*NAME=\"(.+)\".*");
                                String[] bodysplit = body.split("\n");

                                ArrayList<TwitchMedia> medias = new ArrayList<>();
                                for (int i = 0; i < bodysplit.length; i++) {
                                    Matcher matcher = pattern.matcher(bodysplit[i]);
                                    String quality = "";
                                    if (matcher.find()) {
                                        quality = matcher.group(1);
                                        medias.add(new TwitchMedia(quality, bodysplit[i + 2]));
                                    }
                                }

                                loadingDialog.dismiss();
                                Bundle args = new Bundle();
                                args.putSerializable("medias", medias);
                                DialogFragment twitchQualitySelectDialog = new TwitchQualitySelectDialog();
                                twitchQualitySelectDialog.setArguments(args);
                                twitchQualitySelectDialog.show(activity.getSupportFragmentManager(), "TwitchDialogFragment");
                            }

                            @Override
                            public void onFailure(Throwable e, String response) {
                                final Pattern pattern = Pattern.compile("^status code: (.\\d+).*reason phrase:(.+).*");
                                loadingDialog.dismiss();
                                Matcher matcher = pattern.matcher(e.getMessage());
                                if (matcher.find()) {
                                    int statuscode = Integer.valueOf(matcher.group(1));
                                    if (statuscode == 404) {
                                        Toast.makeText(context, "This streamer is not on stream or unfindable", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        final Pattern pattern = Pattern.compile("^status code: (.\\d+).*reason phrase:(.+).*");
                        loadingDialog.dismiss();
                    Matcher matcher = pattern.matcher(e.getMessage());
                        if (matcher.find()) {
                        int statuscode = Integer.valueOf(matcher.group(1));
                        if (statuscode == 404) {
                            Toast.makeText(context, "This streamer is not on stream or unfindable", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TwitchMediaSelectDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
