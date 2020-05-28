package me.Fl0w.twitchdnla;

import android.util.Log;
import me.Fl0w.twitchdnla.UpnpUtils.DeviceDisplay;
import me.Fl0w.twitchdnla.UpnpUtils.UpnpServiceRegistry;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.*;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;

public class Player {
    private AndroidUpnpService upnpService;
    private DeviceDisplay deviceDisplay;
    private Service AVTransportService;
    private Service renderingControlService;
    private ActionCallback AVTransportURIAction;


    interface VolumeHandler {
        void received(int volume);
    }

    interface PositionInfoHandler {
        void received(PositionInfo positionInfo);
    }

    interface MediaInfoHandler {
        void received(MediaInfo mediaInfo);
    }

    public Player() {
        super();
        this.upnpService = UpnpServiceRegistry.getUpnpService();
    }

    public Player setDevice(DeviceDisplay deviceDisplay) {
        Device device = deviceDisplay.getDevice();
        this.upnpService = UpnpServiceRegistry.getUpnpService();
        this.deviceDisplay = deviceDisplay;
        this.renderingControlService = device.findService(new UDAServiceType("RenderingControl"));
        this.AVTransportService = device.findService(new UDAServiceId("AVTransport"));
        return this;
    }

    public Player loadFile(String url) {
        Log.i("Player", "Loading link: " + url);
        AVTransportURIAction = new SetAVTransportURI(AVTransportService, url, "NO METADATA") {
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        upnpService.getControlPoint().execute(AVTransportURIAction);
        return this;
    }

    public Player play() {
        deviceDisplay.setState(Boolean.TRUE);
        Log.e("Player", "Play");
        AVTransportURIAction.getControlPoint().execute(new Play(AVTransportService) {
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;
    }

    public Player pause() {
        deviceDisplay.setState(Boolean.FALSE);
        Log.e("Player", "Pause");
        AVTransportURIAction.getControlPoint().execute(new Pause(AVTransportService) {
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;
    }

    public Player stop() {
        AVTransportURIAction.getControlPoint().execute(new Stop(AVTransportService) {
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;
    }

    public Player next() {
        AVTransportURIAction.getControlPoint().execute(new Next(AVTransportService) {
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;
    }

    public Player previous() {
        AVTransportURIAction.getControlPoint().execute(new Previous(AVTransportService) {
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;

    }

    public Player getMediaInfo(final MediaInfoHandler handler) {
        AVTransportURIAction.getControlPoint().execute(new GetMediaInfo(AVTransportService) {
            public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                handler.received(mediaInfo);
            }

            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;
    }

    public Player getPositionInfo(final PositionInfoHandler handler) {
        AVTransportURIAction.getControlPoint().execute(new GetPositionInfo(AVTransportService) {
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                handler.received(positionInfo);
            }

            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
        return this;
    }

    public Player seek(String seek) {
        AVTransportURIAction.getControlPoint().execute(new Seek(AVTransportService, seek) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        });
        return this;
    }

    public Player setMute(boolean muted) {
        upnpService.getControlPoint().execute(new SetMute(renderingControlService, muted) {
            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {

            }

            @Override
            public void success(ActionInvocation invocation) {

            }
        });
        return this;
    }

    public Player setVolume(int volume) {
        upnpService.getControlPoint().execute(new SetVolume(renderingControlService, volume) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        });
        return this;
    }

    public Player getVolume(VolumeHandler handler) {
        AndroidUpnpService upnpService = UpnpServiceRegistry.getUpnpService();
        upnpService.getControlPoint().execute(new GetVolume(renderingControlService) {
            @Override
            public void received(ActionInvocation actionInvocation, int currentVolume) {
                handler.received(currentVolume);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        });
        return this;
    }

    public boolean isReady() {
        return deviceDisplay != null && AVTransportURIAction != null;
    }

    public DeviceDisplay getDevice() {
        return deviceDisplay;
    }

    public boolean getState() {
        return deviceDisplay.getState();
    }

}
