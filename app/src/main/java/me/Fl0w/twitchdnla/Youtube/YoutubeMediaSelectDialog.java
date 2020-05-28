package me.Fl0w.twitchdnla.Youtube;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.Fl0w.twitchdnla.R;
import me.Fl0w.twitchdnla.UpnpUtils.MediaListener;

public class YoutubeMediaSelectDialog extends DialogFragment {
    private MediaListener listener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (MediaListener) context;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_youtube, null))
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Dialog dialog = (Dialog) dialogInterface;
                        Context context = dialog.getContext();
                        EditText entry = getDialog().findViewById(R.id.youtubeChannelEntry);
                        String url = entry.getText().toString();
                        new YoutubeProcessing(context, url,activity.getSupportFragmentManager() ,new YoutubeProcessingCallback() {
                            @Override
                            public void onSuccess(String URL,String thumbnail) {
                                listener.setMediaUrl(URL,thumbnail);
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                final Pattern pattern = Pattern.compile("^status code: (.\\d+).*reason phrase:(.+).*");
                                Matcher matcher = pattern.matcher(e.getMessage());
                                if (matcher.find()) {
                                    int statuscode = Integer.parseInt(matcher.group(1));
                                    if (statuscode == 404) {
                                        Toast.makeText(context, "Cant find the video", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        });

                    }
                });
        return builder.create();
    }
}
