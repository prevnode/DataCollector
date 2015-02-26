package example.weka.datacollector;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.FastVector;

import java.util.ArrayList;


/**
 * Created by Research on 2/2/2015.
 */
public final class InstanceGenerator {

    private final Instances _emptyInstances;

    public InstanceGenerator(){
        _attributes = new FastVector();
        _attributes.addElement(new Attribute("Batt_Voltage", 0));
        //_attributes.addElement(new Attribute("Batt_Current", 1));
        _attributes.addElement(new Attribute("Batt_Temp", 1));
        _attributes.addElement(new Attribute("BattPercentLevel", 2));

        _attributes.addElement(new Attribute("Local_TX_Packet_Delta", 3));
        _attributes.addElement(new Attribute("Local_TX_Byte_Delta", 4));
        _attributes.addElement(new Attribute("Local_RX_Packet_Delta", 5));
        _attributes.addElement(new Attribute("Local_RX_Byte_Delta", 6));
        _attributes.addElement(new Attribute("Mobile_TX_Packet_Delta", 7));
        _attributes.addElement(new Attribute("Mobile_TX_Byte_Delta", 8));
        _attributes.addElement(new Attribute("Mobile_RX_Packet_Delta", 9));
        _attributes.addElement(new Attribute("Mobile_RX_Byte_Delta", 10));

        //Memory Usage
        _attributes.addElement(new Attribute("Memory_Available", 11));
        _attributes.addElement(new Attribute("Memory_Percentage", 12));


        //CPU_Usage
        _attributes.addElement(new Attribute("Load_Avg_1_min", 13));
        _attributes.addElement(new Attribute("Load_Avg_5_min", 14));
        _attributes.addElement(new Attribute("Load_Avg_15_min", 15));
        _attributes.addElement(new Attribute("Running_Entities", 16));
        _attributes.addElement(new Attribute("Total_Entities", 17));

        //Nominal attribute
        FastVector labels = new FastVector();
        labels.addElement("Normal");
        labels.addElement("Attack");
        _attributes.addElement(new Attribute("Class", labels));


        _emptyInstances = new Instances("AndroidAttack", _attributes, 1);

        //Be careful that if adding more attributes class remains last
        _emptyInstances.setClassIndex(_emptyInstances.numAttributes() -1);
    }


    private FastVector _attributes;

    public Instances getEmptyInstances(){

        return _emptyInstances;
    }

}
