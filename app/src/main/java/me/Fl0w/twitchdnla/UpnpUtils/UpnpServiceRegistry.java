package me.Fl0w.twitchdnla.UpnpUtils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;

public class UpnpServiceRegistry {
    private static org.fourthline.cling.android.AndroidUpnpService upnpService;

    public static BrowseRegistryListener registryListener = new BrowseRegistryListener();
    public static ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;


            // Get ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);
            upnpService.getControlPoint().search();


        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    public static AndroidUpnpService getUpnpService() {
        return upnpService;
    }
    public static BrowseRegistryListener getRegistryListener(){
        return registryListener;
    }
    public static ServiceConnection getServiceConnection(){
        return serviceConnection;
    }
}
