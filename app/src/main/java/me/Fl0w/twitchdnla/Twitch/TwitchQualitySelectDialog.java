package me.Fl0w.twitchdnla.Twitch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import me.Fl0w.twitchdnla.R;
import me.Fl0w.twitchdnla.UpnpUtils.MediaListener;

import java.io.InputStream;
import java.util.ArrayList;

public class TwitchQualitySelectDialog extends DialogFragment {
    private MediaListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (MediaListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        ArrayList<TwitchMedia> medias = (ArrayList<TwitchMedia>) getArguments().getSerializable("medias");
        final TwitchMedia[] activeMedia = {medias.get(0)};
        ArrayList<String> qualities = new ArrayList<>();
        for (int i = 0; i < medias.size() - 1; i++) {
            qualities.add(medias.get(i).getQuality());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose quality")
                .setSingleChoiceItems(qualities.toArray(new CharSequence[qualities.size()]), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activeMedia[0] = medias.get(which);
                    }
                })
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.setMediaUrl(activeMedia[0].getURL(), null);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}