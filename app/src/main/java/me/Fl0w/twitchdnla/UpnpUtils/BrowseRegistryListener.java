package me.Fl0w.twitchdnla.UpnpUtils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class BrowseRegistryListener extends DefaultRegistryListener {
    public ArrayList<DeviceDisplay> deviceDisplays = new ArrayList<>();

    /* Discovery performance optimization for very slow Android devices! */
    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        new Runnable() {
            public void run() {
                Log.i("UPNP error", "Discovery failed of '" + device.getDisplayString() + "': "
                        + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"));
            }
        };
        deviceRemoved(device);
    }
    /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(device);
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        deviceAdded(device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        deviceRemoved(device);
    }

    public void deviceAdded(final Device device) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (device.getType().getDisplayString().equals("MediaRenderer")) {
                    DeviceDisplay d = new DeviceDisplay(device);
                    int position = deviceDisplays.indexOf(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        deviceDisplays.remove(d);
                        deviceDisplays.add(position, d);
                    } else {
                        deviceDisplays.add(d);
                    }
                    for (DeviceListener deviceListener : listeners)
                        deviceListener.addDevice(d);
                }

            }
        });
    }

    public void deviceRemoved(Device device) {
        DeviceDisplay d = new DeviceDisplay(device);
        int position = deviceDisplays.indexOf(d);

        deviceDisplays.remove(d);
        // Notify everybody that may be interested.
        for (DeviceListener deviceListener : listeners)
         deviceListener.removeDevice(d);
    }
    private List<DeviceListener> listeners = new ArrayList<DeviceListener>();

    public void addListener(DeviceListener toAdd) {
        listeners.add(toAdd);
    }
}

