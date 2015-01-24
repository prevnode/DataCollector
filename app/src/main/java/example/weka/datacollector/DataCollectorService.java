package example.weka.datacollector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.util.Log;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.app.PendingIntent;

public class DataCollectorService extends Service implements IInstanceCreator {
    public DataCollectorService() {
    }
    private static final String TAG = "BatterySampleService";
    private NotificationManager mNM;
    private MyReceiver mReceiver = new MyReceiver();
    private IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private IInstanceReceiver instanceReceiver;
    private ArffInstance mArffInstance;
    //private AlarmManager alarmManager;
    //private PendingIntent pendingIntent;



    private boolean mActive;

    @Override
    public void RegisterReceiver(IInstanceReceiver ir){
        instanceReceiver = ir;
    }

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;


    private void registerBattReceiver(){
        // Register for the battery changed event
        this.registerReceiver(mReceiver,battFilter);
    }
    public void readBatt(){

        // Intent is sticky so using null as receiver works fine
        // return value contains the status
        Intent batteryStatus = this.registerReceiver(null, battFilter);

        mArffInstance = new ArffInstance();
        mArffInstance.BattCurrent = BatteryManager.BATTERY_PROPERTY_CURRENT_NOW;
        mArffInstance.BattLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        mArffInstance.BattVoltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        mArffInstance.BattTemp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

        if(instanceReceiver != null)
            instanceReceiver.receiveData(mArffInstance);

    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        DataCollectorService getService() {
            return DataCollectorService.this;
        }
    }

    public void setActive(boolean active){
        mActive = active;
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        //battReceiver = new BatteryReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        /*
        alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intermediateIntent = new Intent(this, DataCollectorService.class);
        pendingIntent = PendingIntent.getService(this,0,intermediateIntent,0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                System.currentTimeMillis() + 10000, 10000, pendingIntent);
        */

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ControlDataCollection.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    class MyReceiver extends BroadcastReceiver{
        int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;
        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            Log.e("BatteryManager", "level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage);
        }

    };
}
