package example.weka.datacollector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.content.ServiceConnection;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class ControlDataCollection extends ActionBarActivity{

    private boolean mRecording;

    private Button startButton;
    private boolean mIsBound;
    private PendingIntent pendingIntent;
    private static final String TAG = "ControlCollection";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_data_collection);
        Intent alarmIntent = new Intent(ControlDataCollection.this, DataCollector.class);
        pendingIntent = PendingIntent.getBroadcast(ControlDataCollection.this, 0, alarmIntent, 0);

        doBindService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startCollection(View view) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancelCollection(View view) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Start/Stop mRecording accelerometer readings
     */
    public void toggleRecord(View view){

        if(startButton == null){
            Log.d(TAG,"no button ref");
            return;
        }

        if(mRecording)
            startButton.setText("Start");
        else
            startButton.setText("Stop");

        mRecording = !mRecording;
        //mBoundService.setActive(mRecording);
    }


    private ClassificationService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((ClassificationService.ClassificationBinder)service).getService();

                    //((DataCollectorService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(ControlDataCollection.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(ControlDataCollection.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }

    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        //bindService(new Intent(ControlDataCollection.this,
        //        DataCollectorService.class), mConnection, Context.BIND_AUTO_CREATE);

        bindService(new Intent(ControlDataCollection.this,
                ClassificationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}