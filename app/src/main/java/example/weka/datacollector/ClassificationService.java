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
import java.io.InputStream;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class ClassificationService extends Service {

    private Instance _testInstance;
    private Instances _testSet;

    public ClassificationService() {
    }

    public class ClassificationBinder extends Binder {
        ClassificationService getService(){
            return ClassificationService.this;
        }

        private void Classify(){

            if(_testSet.numInstances() > 0)
                _testSet.delete();

            try {
                _testSet.add(_testInstance);
            }catch(Throwable t){
                Log.e(TAG, "add: " + t.toString());
            }

            try{
                _testInstance.setDataset(_testSet);
            }catch(java.lang.ArrayIndexOutOfBoundsException e){
                Log.e(TAG, "set dataset: " + e.toString() );
            }

            try {
                _testInstance.setClassMissing();
            }catch(Exception e){
                Log.e(TAG, "set Class Missing: " + e.toString());
            }

            if(_classifier == null){
                Log.e(TAG, "Classifier null");
                return;
            }

            double result = -1;

            try {
                int lastInstance = _testSet.numInstances() -1;

                Instance toClassify = _testSet.instance(lastInstance);

                if(_trainInstances.equalHeaders(_testSet)) {

                    result = _classifier.classifyInstance(toClassify);
                }
                else{
                    Log.e(TAG, "incompatible headers");
                    return;
                }

                Toast.makeText(getApplicationContext(),"Result: " +
                       ( (result == 0) ? "Normal" : "Infected"), Toast.LENGTH_SHORT).show();

                //instances.instance(0).setClassValue(clsLabel);

            }catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "Classify: " + e.toString());
                return;
            }

        }

        public void sendData(double[] data){

            ClassificationService.this._testInstance = new Instance(19);
            for(int i = 0; i < 19; ++i){
                _testInstance.setValue(i,data[i]);
            }

            Classify();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        int counter = 1;


        if( LoadClassifierModel() )
            PrepareFilter();
        else
            Log.e(TAG, "Load failed");


        //Test set should have same header info as train set but no instances
        _testSet = new Instances(_trainInstances);
        _testSet.delete();
        _testSet.setRelationName("phone-weka.filters.supervised.attribute.Discretize-Rfirst-last");



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
    private NaiveBayes _naiveBayes = new NaiveBayes();
    private Classifier _classifier;
    //private FilteredClassifier _filteredClassifier = null;
    private final String TAG = "ClassificationService";
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
            //_filteredClassifier.buildClassifier(trainInstances);

        }catch(Exception e){
            Log.e(TAG, "train classifier: " + e.toString());
            return;

        }

    }

    private boolean LoadClassifierModel(){

        File model = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "arff/SMOTrainedSet2.model");

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

        _classifier = (Classifier)o[0];
        //_filteredClassifier = (FilteredClassifier)o[0];

        if(_classifier == null){
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
        String[] options = new String[2];

        options[0] = "-R";
        options[1] = "first-last";

        try {
            _discretize.setOptions(options);
            _discretize.setInputFormat(_trainInstances);
        }catch (Exception e){
            Log.e(TAG, "prepare filter: " + e.toString());
        }

    }


    /**
     * Applies the discretize filter to the instances
     * @param toFilter
     * @return Instances with the filter applied or null if error occurred
     */
    private Instances FilterDataSet(Instances toFilter){
        Instances filtered = null;

        boolean formatted = _discretize.isOutputFormatDefined();

        try{
            filtered = Filter.useFilter(toFilter, _discretize);
        }catch (Exception e){
            Log.e(TAG, "filterData: " + e.toString() );
        }

        return filtered;
    }


    private boolean PrepareFileReader(){
        File trainingSet = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/arff/Training2.arff" );

        try {
            FileReader fileReader = new FileReader(trainingSet);
        }catch(Exception e){
            Log.e(TAG, "Prepare reader: " + e.toString());
            return false;
        }

        return true;
    }
}
