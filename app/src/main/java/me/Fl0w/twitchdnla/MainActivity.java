package me.Fl0w.twitchdnla;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import me.Fl0w.twitchdnla.Twitch.TwitchMediaSelectDialog;
import me.Fl0w.twitchdnla.UpnpUtils.DeviceDisplay;
import me.Fl0w.twitchdnla.UpnpUtils.MediaListener;
import me.Fl0w.twitchdnla.UpnpUtils.UpnpServiceRegistry;
import me.Fl0w.twitchdnla.Youtube.YoutubeMediaSelectDialog;
import me.Fl0w.twitchdnla.Youtube.YoutubeProcessing;
import me.Fl0w.twitchdnla.Youtube.YoutubeProcessingCallback;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MediaListener {
    public static MaterialButton playPauseButton;
    private MaterialToolbar toolBar;
    private static FloatingActionButton selectDeviceButton;
    private ImageView imageView;
    private static String url;
    private static Context context;
    private DrawerLayout drawer;
    private MediaListener listener;
    private Slider slider;
    private Player player;

    public MainActivity() {
        super();
    }

    public static Context getContext() {
        return context;
    }

    @SuppressLint({"CutPasteId", "WrongConstant"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolBar = findViewById(R.id.toolbar);
        slider = findViewById(R.id.volumeSlider);
        drawer = findViewById(R.id.drawer_layout);
        playPauseButton = findViewById(R.id.buttonPlayPause);
        selectDeviceButton = findViewById(R.id.buttonDeviceSelect);
        MaterialButton seekBackwardButton = findViewById(R.id.buttonSeekBackward);
        MaterialButton seekForwardButton = findViewById(R.id.buttonSeekBackward);
        imageView = findViewById(R.id.image);

        listener = MainActivity.this;
        context = MainActivity.this;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try {
            YoutubeDL.getInstance().init(getApplication());
            Log.i("YoutubeDL", "Successfully started");
        } catch (YoutubeDLException e) {
            Log.e("YoutubeDL", "failed to initialize youtubeDL-android", e);
        }
        getApplicationContext().bindService(new Intent(this, AndroidUpnpServiceImpl.class), UpnpServiceRegistry.serviceConnection, 1);

        player = new Player();

        imageView.setColorFilter(0xffffff);

        playPauseButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (player.isReady()) {
                    if (player.getDevice().getState()) {
                        playPauseButton.setIcon(getDrawable(R.drawable.round_play_arrow_white_48));
                        player.pause();
                        return;
                    }

                    playPauseButton.setIcon(getDrawable(R.drawable.round_pause_white_48));
                    player.play();
                }
            }
        });
        playPauseButton.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (player.isReady()) {
                    player.stop();
                    playPauseButton.setIcon(getDrawable(R.drawable.round_play_arrow_white_48));
                }
                return true;
            }
        });

        seekBackwardButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (player.isReady()) {
                    player.getPositionInfo(new Player.PositionInfoHandler() {
                      @Override
                      public void received(PositionInfo positionInfo) {
                          player.seek(String.valueOf(positionInfo.getTrackElapsedSeconds()+10));
                      }
                  });
                }
            }
        });
        seekForwardButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (player.isReady()) {
                }
            }
        });
        selectDeviceButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, DeviceSelectActivity.class));
            }
        });

        slider.setOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value) {
                if (player.getDevice() != null) {
                    player.setVolume((int) value);
                }
            }
        });

        slider.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                showVolumeSlider();
                return true;
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               /* if (player.getDevice() != null) {
                    player.getVolume(new Player.VolumeHandler() {
                        @Override
                        public void received(int volume) {
                            slider.setValue(volume);
                            Log.i("slider", String.valueOf(volume));
                        }
                    });
                }*/
            }
        }, 0, 500);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loadTwitch: {
                DialogFragment dialog = new TwitchMediaSelectDialog();
                dialog.show(getSupportFragmentManager(), "TwitchDialogFragment");
                break;
            }
            case R.id.loadYoutube: {
                DialogFragment dialog = new YoutubeMediaSelectDialog();
                dialog.show(getSupportFragmentManager(), "YoutubeDialogFragment");
                break;
            }
            default: {
                break;
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        AndroidUpnpService androidUpnpService = UpnpServiceRegistry.getUpnpService();
        if (androidUpnpService != null) {
            androidUpnpService.getRegistry().removeListener(UpnpServiceRegistry.getRegistryListener());
        }
        getApplicationContext().unbindService(UpnpServiceRegistry.getServiceConnection());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        float value;
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                showVolumeSlider();
                value = Math.round(slider.getValue() - 10);
                slider.setValue(value < slider.getValueFrom() ? 0 : value);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                showVolumeSlider();
                value = Math.round(slider.getValue() + 10);
                slider.setValue(value > slider.getValueTo() ? 100 : value);
                break;
        }

        return true;
    }

    int press = 0;

    private void showVolumeSlider() {
        press++;
        int id = press;
        ViewPropertyAnimator animator = slider.animate()
                .setDuration(200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == press) {
                    slider.animate()
                            .alpha(0f)
                            .setDuration(400);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            press = 0;
                            slider.setVisibility(View.GONE);
                            slider.setAlpha(1);
                        }
                    }, 400);
                }
            }
        }, 1200);

        if (press == 1) {
            slider.setVisibility(View.VISIBLE);
            slider.setAlpha(0);
            animator.alpha(1);
        }
    }

    @Override
    public void setDevice(DeviceDisplay device) {

        Log.i("Device",device.toString());
        Log.i("Device",device.getDetailsMessage());
        Log.i("Device",device.getDevice().getDetails().getFriendlyName());
        Log.i("Device",device.getDevice().getDetails().getManufacturerDetails().getManufacturer());

        player.setDevice(device);

        if (url != null) {
            toolBar.setTitle(device.toString());

            player.loadFile(MainActivity.url);
            player.play();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playPauseButton.setIconResource(R.drawable.round_pause_white_48);
                    selectDeviceButton.setImageResource(R.drawable.round_cast_connected_white_48);
                }
            });
        } else {
            toolBar.setTitle(device.toString());
            selectDeviceButton.setImageResource(R.drawable.round_cast_connected_white_48);
        }

    }

    @Override
    public void setMediaUrl(String url, String thumbnail) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (thumbnail != null) {
                    imageView.setColorFilter(null);
                    new DownloadImageTask(imageView).execute(thumbnail);

                } else {
                    imageView.setImageDrawable(getDrawable(R.drawable.outline_music_note_24));
                    imageView.setColorFilter(0xffffff);
                }
            }
        });
        if (player.getDevice() != null) {
            player.loadFile(url);
            player.play();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    playPauseButton.setIconResource(R.drawable.round_pause_white_48);
                }
            });
            MainActivity.url = url;
        } else {
            MainActivity.url = url;
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            List<String> urls = extractUrls(sharedText);

            Pattern pattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
            Matcher matcher = pattern.matcher(sharedText);
            Log.i("Share", sharedText);
            if (matcher.matches()) {
                try {
                    URL url = new URL(sharedText);
                    Log.i("URL", sharedText);
                    if (url.getHost().contains("youtube") || url.getHost().contains("youtu")) {
                        new YoutubeProcessing(MainActivity.this, sharedText, getSupportFragmentManager(), new YoutubeProcessingCallback() {
                            @Override
                            public void onSuccess(String URL, String thumbnail) {
                                listener.setMediaUrl(URL, thumbnail);
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                final Pattern pattern = Pattern.compile("^status code: (.\\d+).*reason phrase:(.+).*");
                                Matcher matcher = pattern.matcher(e.getMessage());
                                if (matcher.find()) {
                                    int statusCode = Integer.parseInt(matcher.group(1));
                                    if (statusCode == 404) {
                                        Toast.makeText(MainActivity.this, "Cant find the video", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    } else if (url.getHost().contains("http://www.twitch.tv/")) {
                        Toast.makeText(MainActivity.this, url.toString(), Toast.LENGTH_LONG).show();
                        Log.i("twitch", sharedText);
                    } else {
                        listener.setMediaUrl(sharedText, null);
                    }
                } catch (MalformedURLException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.left_menu, menu);
        return true;
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Returns a list with all links contained in the input
     */
    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }
}
