package example.weka.datacollector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import weka.classifiers.bayes.NaiveBayes;

public class ClassificationService extends Service {
    public ClassificationService() {
    }

    public class ClassificationBinder extends Binder {
        ClassificationService getService(){
            return ClassificationService.this;
        }

        public void Tag(){
            Toast.makeText(getApplicationContext(),"count: " + counter, Toast.LENGTH_SHORT).show();
            counter++;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        counter = 1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return _Binder;
    }

    private final IBinder _Binder = new ClassificationBinder();
    private int counter;
}
