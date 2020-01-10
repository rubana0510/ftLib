package com.feedbacktower.ui.location.live;

import android.app.ActivityManager;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {
    private TrackerService trackerService;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();

        String action = intent.getStringExtra("action");
        if (action.equals("open_app")) {
            //performAction1(context);
            //Navigation.navig
        } else if (action.equals("action2")) {
            performAction2();

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void performAction1(Context context) {
        Intent intent = new Intent(context, TrackerService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void performAction2() {

    }

    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackerService.class.getName().equals(service.service.getClassName())) {
//                TrackerService trackerService = manager.getRunningServiceControlPanel()
                Toast.makeText(context, "Service is running!!", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (name.toString().endsWith("TrackerService")) {
                trackerService = ((TrackerService.LocationServiceBinder) service).getService();
                trackerService.stopTracking();
                Log.d("ServiceConnection", "onServiceConnected: Stopped");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name.getClassName().equals("TrackerService")) {
                trackerService = null;
            }
        }
    };

}