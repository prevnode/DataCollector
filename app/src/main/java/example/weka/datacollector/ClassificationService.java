package example.weka.datacollector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

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

        public void Classify(Instances dataSet){

            if(_naiveBayes == null){
                Log.e(TAG, "Classifier null");
                return;
            }

            Instances filteredInstances = FilterDataSet(dataSet);
            if(filteredInstances == null){
                Log.e(TAG, "classify can't use null filtered dataset");
                return;
            }


            try {
                int numberOfInstances = filteredInstances.numInstances() -1;
                Instance toClassify = filteredInstances.instance(numberOfInstances);

                double result = _naiveBayes.classifyInstance(toClassify);

                Toast.makeText(getApplicationContext(),"Result: " + result, Toast.LENGTH_SHORT).show();

                //instances.instance(0).setClassValue(clsLabel);

            }catch(Exception e){
                Log.e(TAG, "Classify: " + e.toString());
                return;
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        counter = 1;

        if( LoadClassifierModel() )
            PrepareFilter();
        else
            Log.e(TAG, "Load failed");

        /*
        if(PrepareFileReader() )
            TrainClassifier();
        else
            Log.e(TAG, "Unable to read training set");
        */


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return _Binder;
    }

    private final IBinder _Binder = new ClassificationBinder();
    private int counter;
    private NaiveBayes _naiveBayes = new NaiveBayes();
    private final String TAG = "ClassificationService";
    private FileReader _fileReader;
    private Instances _trainInstances;
    private Discretize _discretize;

    private void TrainClassifier(){

        try {
            ArffLoader loader = new ArffLoader();

            File trainingFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "/arff/Training2.arff" );

            loader.setFile(trainingFile);

            Instances trainInstances = loader.getDataSet();

            trainInstances.setClassIndex(trainInstances.numAttributes() - 1);

            _naiveBayes.buildClassifier(trainInstances);

        }catch(Exception e){
            Log.e(TAG, "train classifier: " + e.toString());
            return;

        }

    }

    private boolean LoadClassifierModel(){

        File model = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "arff/NBTrainedONSet2.model");

        InputStream inputStream;
        Object o[];

        try {
            inputStream = new FileInputStream(model);
        }catch (FileNotFoundException f){
            Log.e(TAG, f.toString());
            return  false;
        }

        try {
            o = SerializationHelper.readAll(inputStream);
        }catch (Exception e){
            Log.e(TAG, e.toString());
            return false;
        }

        _naiveBayes = (NaiveBayes)o[0];

        if(_naiveBayes == null){
            Log.e(TAG, "Failed to load model classifier");
            return false;
        }

        _trainInstances = (Instances)o[1];

        if(_trainInstances == null){
            Log.e(TAG, "Failed to load model instances");
            return false;
        }

        return true;
    }

    private void PrepareFilter(){
        _discretize = new Discretize();
        try {
            _discretize.setInputFormat(_trainInstances);
        }catch (Exception e){
            Log.e(TAG, "prepare filter: " + e.toString());
        }

    }

    private boolean PrepareFileReader(){
        File trainingSet = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/arff/Training.arff" );

        try {
            _fileReader = new FileReader(trainingSet);
        }catch(Exception e){
            Log.e(TAG, "Prepare reader: " + e.toString());
            return false;
        }

        return true;
    }

    /**
     * Applies the discretize filter to the instances
     * @param toFilter
     * @return Instances with the filter applied or null if error occurred
     */
    private Instances FilterDataSet(Instances toFilter){
        Instances filtered = null;

        try{
            filtered = Filter.useFilter(toFilter, _discretize);
        }catch (Exception e){
            Log.e(TAG, "filterData: " + e.toString() );
        }

        return filtered;
    }
}
