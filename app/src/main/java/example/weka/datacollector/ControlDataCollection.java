package example.weka.datacollector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import java.io.FileWriter;
import java.io.IOException;

public class ControlDataCollection extends ActionBarActivity implements IInstanceReceiver{

    private boolean mRecording;

    private Button startButton;
    private boolean mIsBound;
    private boolean mFileReadyToWrite;
    private static final String TAG = "ControlReading";
    private FileWriter dataFile;




    @Override
    public void receiveData(Instance instance){

        if(!mFileReadyToWrite) {
            Log.d(TAG, "Tried to receive data but file not ready");
            return;
        }

        try {
            dataFile.append(instance.toString());
        }catch(IOException e){
            Log.e(TAG,"receiveData : " + e.toString());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_data_collection);
        startButton =(Button)findViewById(R.id.toggleButton);
        startButton.setText("Start");


        mFileReadyToWrite = setupFileWriter();
        doBindService();

    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getDocumentsDir(String dataDirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), dataDirName);
        if(file.exists()) {
            Log.d(TAG, "Directory exists");
            return file;
        }

        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    private void scanDataFile(File file) {
        MediaScannerConnection.scanFile(this,
                new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private boolean setupFileWriter(){
        if(!isExternalStorageWritable()){
            Log.e(TAG, "External Storage unavailable");
            return false;
        }

        File dir = getDocumentsDir("arff");
        File file = new File(dir,"data.arff");

        try{
            dataFile = new FileWriter(file);
        }catch(IOException e){
            Log.e(TAG, e.toString());
        }
        return true;
    }

    /**
     * Writes the arff header info to the file. Current version doesn't have
     * anything to write. DO NOT use until creating a source for the header text
     */
    private void writeHeader(){
        if(!isExternalStorageWritable()){
            Log.e(TAG, "External Storage unavailable");
            return;
        }

        try {
            FileWriter outputStream;

            File dir = getDocumentsDir("arff");
            File file = new File(dir,"data.arff");
            if( file.exists() ){
                Log.d(TAG, "accel exists");
            }
            else{
                file.createNewFile();
                Log.d(TAG, "accel created");
            }
            outputStream =  new FileWriter(file); //openFileOutput("accelARFF", Context.MODE_WORLD_READABLE);
            if(outputStream == null)
                throw new IOException("wtf");

            outputStream.close();
            scanDataFile(file);
            Toast.makeText(getBaseContext(),"file saved",
                    Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Log.e(TAG, e.toString() + " WriterHeader()" );
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_control_reading, menu);
        return true;
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
        mBoundService.setActive(mRecording);
    }

    private DataCollectorService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((DataCollectorService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(ControlDataCollection.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();

            mBoundService.RegisterReceiver(ControlDataCollection.this);
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
        bindService(new Intent(ControlDataCollection.this,
                DataCollectorService.class), mConnection, Context.BIND_AUTO_CREATE);
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
