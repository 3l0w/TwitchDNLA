package me.Fl0w.twitchdnla;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.Fl0w.twitchdnla.UpnpUtils.DeviceDisplay;
import me.Fl0w.twitchdnla.UpnpUtils.DeviceListener;
import me.Fl0w.twitchdnla.UpnpUtils.MediaListener;

import static java.security.AccessController.getContext;
import static me.Fl0w.twitchdnla.ListUtils.setOnCreateOptionsMenu;
import static me.Fl0w.twitchdnla.ListUtils.setOnOptionsItemSelected;
import static me.Fl0w.twitchdnla.UpnpUtils.UpnpServiceRegistry.registryListener;

public class DeviceSelectActivity extends ListActivity implements DeviceListener {
    public static ArrayAdapter<DeviceDisplay> listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        setListAdapter(listAdapter);

        for (DeviceDisplay deviceDisplay : registryListener.deviceDisplays) {
            listAdapter.add(deviceDisplay);
        }

        registryListener.addListener(this);
    }


    @Override
    public void addDevice(DeviceDisplay deviceDisplay) {

        int position = listAdapter.getPosition(deviceDisplay);
        if (position >= 0) {
            // Device already in the list, re-set new value at same position
            listAdapter.remove(deviceDisplay);
            listAdapter.insert(deviceDisplay, position);
        } else {
            listAdapter.add(deviceDisplay);
        }

    }

    @Override
    public void removeDevice(DeviceDisplay deviceDisplay) {
        int position = listAdapter.getPosition(deviceDisplay);

        if (position >= 0) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    listAdapter.remove(deviceDisplay);
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setOnCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setOnOptionsItemSelected(item, getApplicationContext());
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        DeviceDisplay deviceDisplay = (DeviceDisplay) l.getItemAtPosition(position);

        ((MediaListener) MainActivity.getContext()).setDevice(deviceDisplay);
        finish();
    }

    public static ArrayAdapter<DeviceDisplay> getlistAdapter() {
        return listAdapter;
    }
}

