package example.weka.datacollector;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.net.TrafficStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import weka.core.Instance;
import weka.core.Instances;


/**
 * Upon receiving a system alarm this class reads all reads all relevant data from the phone.
 * This data can be written to disk as an arff or returned as a weka.core.instances object.
 */
public class DataCollector extends BroadcastReceiver {

    private boolean _writeToFile = true;
    private final long BYTES_IN_MEG = 1048576L;
    private final String TAG = "DataCollector";
    private boolean _fileReadyToWrite;
    private File file;
    private IntentFilter _battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private FileWriter _fileWriter;
    private ArffInstance _arffInstance = new ArffInstance();
    private static InstanceGenerator _instanceGenerator = new InstanceGenerator();
    private final Instances _dataSet = _instanceGenerator.getEmptyInstances();

    private Context appContext;

    //Set these variables first time app is loaded
    private static long lastTotalTxPacketSample = TrafficStats.getTotalTxPackets();
    private static long lastTotalTxByteSample = TrafficStats.getTotalTxBytes();
    private static long lastTotalRxPacketSample = TrafficStats.getTotalRxPackets();
    private static long lastTotalRxByteSample = TrafficStats.getTotalRxBytes();
    private static long lastTxMobilePacketSample = TrafficStats.getMobileTxPackets();
    private static long lastTxMobileByteSample = TrafficStats.getMobileTxBytes();
    private static long lastRxMobilePacketSample = TrafficStats.getMobileRxPackets();
    private static long lastRxMobileByteSample = TrafficStats.getMobileRxBytes();

    @Override
    public void onReceive(Context context, Intent intent) {

        appContext = context.getApplicationContext();
        //Toast.makeText(context, "Alarm Received", Toast.LENGTH_SHORT).show();

        readBatt(context);
        readCPU();
        readNetwork();
        readMem();
        _arffInstance.Class = "Normal";

        /*
        if(_writeToFile)
            writeToFile();
        else{
            _dataSet.add( createInstance() );
            double classValue = ControlDataCollection.Classify(_dataSet);
            _dataSet.instance(_dataSet.numInstances() -1).setClassValue(classValue);
            Toast.makeText(context, "Classified as: " + classValue, Toast.LENGTH_SHORT).show();
        }
        */
        IBinder binder = peekService(appContext, new Intent(appContext, ClassificationService.class));
        ClassificationService.ClassificationBinder classificationBinder = (ClassificationService.ClassificationBinder)binder;

        if(classificationBinder != null){
            //classificationBinder.Tag();
            classificationBinder.Classify(_dataSet);

        }


    }

    private Instance createInstance(){
        return new Instance(1, _arffInstance.toValues());
    }


    private boolean writeToFile(){
        _fileReadyToWrite = setupFileWriter();

        if(!_fileReadyToWrite) {
            Log.e(TAG, "File Not ready");
            return false;
        }

        try {
            _fileWriter.append(_arffInstance.toString());
            _fileWriter.close();
        }catch(IOException e){
            Log.e(TAG,"receiveAlarm : " + e.toString());
            return false;
        }


        scanDataFile(file,appContext);
        Log.d(TAG, "Wrote " + _arffInstance.Batt_Percent_Level + "...");
        return  true;

    }

    private void readMem(){
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager)appContext.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        _arffInstance.Memory_Available = mi.availMem / BYTES_IN_MEG;
        _arffInstance.Memory_Percentage = (float)mi.availMem / (float)mi.totalMem;
    }

    private void readCPU(){

        String cpuData = "No data from top";

        try {
            Process process = Runtime.getRuntime().exec("cat /proc/loadavg");
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
        String[] tokens = cpuData.split(" ");
        _arffInstance.Load_Avg_1_min = Float.parseFloat(tokens[0]);
        _arffInstance.Load_Avg_5_min = Float.parseFloat(tokens[1]);
        _arffInstance.Load_Avg_15_min = Float.parseFloat(tokens[2]);
        tokens = tokens[3].split("/");
        _arffInstance.Running_Entities = Integer.parseInt(tokens[0]);
        _arffInstance.Total_Entities = Integer.parseInt(tokens[1]);
    }

    private void readNetwork(){

        long txTotalPackets = TrafficStats.getTotalTxPackets();
        long txTotalBytes = TrafficStats.getTotalTxBytes();
        long rxTotalPackets = TrafficStats.getTotalRxPackets();
        long rxTotalBytes = TrafficStats.getTotalRxBytes();
        long txMobilePackets = TrafficStats.getMobileTxPackets();
        long txMobleBytes = TrafficStats.getMobileTxBytes();
        long rxMobilePackets = TrafficStats.getMobileRxPackets();
        long rxMobileBytes = TrafficStats.getMobileRxBytes();


        //Delta is difference between current and last counts
        _arffInstance.Local_TX_Packet_Delta = (txTotalPackets - txMobilePackets) - lastTotalTxPacketSample;
        _arffInstance.Local_TX_Byte_Delta = (txTotalBytes - txMobleBytes) - lastTotalTxByteSample;
        _arffInstance.Local_RX_Packet_Delta = (rxTotalPackets - rxMobilePackets) - lastTotalRxPacketSample;
        _arffInstance.Local_RX_Byte_Delta = (rxTotalBytes - rxMobileBytes) - lastTotalRxByteSample;
        _arffInstance.Mobile_TX_Packet_Delta = txMobilePackets - lastTxMobilePacketSample;
        _arffInstance.Mobile_TX_Byte_Delta = txMobleBytes - lastTxMobileByteSample;
        _arffInstance.Mobile_RX_Packet_Delta = rxMobilePackets - lastRxMobilePacketSample;
        _arffInstance.Mobile_RX_Byte_Delta = rxMobileBytes - lastRxMobileByteSample;

        //Update last samples
        lastTotalTxPacketSample     = txTotalPackets;
        lastTotalRxByteSample       = txTotalBytes;
        lastTotalRxPacketSample     = rxTotalPackets;
        lastTotalRxByteSample       = rxTotalBytes;
        lastTxMobilePacketSample    = txMobilePackets;
        lastTxMobileByteSample      = txMobleBytes;
        lastRxMobilePacketSample    = rxMobilePackets;
        lastRxMobileByteSample      = rxMobileBytes;

    }

    private void readBatt(Context context){

        // Intent is sticky so using null as receiver works fine
        // return value contains the status
        Intent batteryStatus = appContext.registerReceiver(null, _battFilter);

        int lvl = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        _arffInstance.Batt_Percent_Level = (float)lvl / (float)scale;


        _arffInstance.Batt_Voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        _arffInstance.Batt_Temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

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
            //Log.d(TAG, "Directory exists");
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
            _fileWriter = new FileWriter(file,true);
        }catch(IOException e){
            Log.e(TAG, e.toString());
        }
        return true;
    }

    private void scanDataFile(File file, Context context) {
        MediaScannerConnection.scanFile(appContext, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

}
