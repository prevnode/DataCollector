package example.weka.datacollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class InstanceGenerator extends BroadcastReceiver {

    private final String TAG = "InstanceGenerator";
    private boolean mFileReadyToWrite;
    private File file;
    private IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private FileWriter fileWriter;
    private ArffInstance mArffInstance = new ArffInstance();

    public InstanceGenerator() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarm Received", Toast.LENGTH_SHORT).show();

        readBatt(context);
        //readCPU();

        mFileReadyToWrite = setupFileWriter();
        if(!mFileReadyToWrite) {
            Log.d(TAG, "File Not ready");
            return;
        }

        try {
            fileWriter.append(mArffInstance.toString());
            fileWriter.close();
        }catch(IOException e){
            Log.e(TAG,"receiveAlarm : " + e.toString());
        }

        scanDataFile(file,context);
        Log.d(TAG, "Wrote " + mArffInstance.BattPercentLevel + "...");
    }

    private void readMem(){
        if(mArffInstance == null){
            Log.e(TAG, "Unable to get instance");
            return;
        }


    }

    private void readCPU(){
        if(mArffInstance == null){
            Log.e(TAG, "Unable to get instance");
            return;
        }

        String cpuData = "No data from top";

        try {
            Process process = Runtime.getRuntime().exec("cat /proc/loadavg"); //"uptime top -n 1 -d 1"
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                log.append(line + "\n");
            }

            cpuData = log.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }


        /*
        try {

            ProcessBuilder pb = new ProcessBuilder("adb", "uptime");
            Process pc = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            StringBuilder log=new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                Log.d(TAG, "read line");
                log.append(line + "\n");
            }

            cpuData = log.toString();

        }catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        */

        mArffInstance.Class = cpuData;
    }

    private void readBatt(Context context){

        // Intent is sticky so using null as receiver works fine
        // return value contains the status
        Intent batteryStatus = context.getApplicationContext().registerReceiver(null, battFilter);


        if(mArffInstance == null){
            Log.e(TAG, "Unable to get instance");
            return;
        }

        mArffInstance.BattCurrent = BatteryManager.BATTERY_PROPERTY_CURRENT_NOW;
        mArffInstance.BattPercentLevel =
                batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) /
                        batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        mArffInstance.BattVoltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        mArffInstance.BattTemp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private File getDocumentsDir(String dataDirName) {
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


    private boolean setupFileWriter(){
        if(!isExternalStorageWritable()){
            Log.e(TAG, "External Storage unavailable");
            return false;
        }

        File dir = getDocumentsDir("arff");
        file = new File(dir,"data.arff");

        try{
            fileWriter = new FileWriter(file,true);
        }catch(IOException e){
            Log.e(TAG, e.toString());
        }
        return true;
    }

    private void scanDataFile(File file, Context context) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

}
