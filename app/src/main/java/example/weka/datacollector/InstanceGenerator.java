package example.weka.datacollector;

import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;


/**
 * Created by Research on 2/2/2015.
 */
public final class InstanceGenerator {

    public InstanceGenerator(){
        _attributes = new ArrayList<>();
        _attributes.add(new Attribute("Batt_Voltage", 0));
        _attributes.add(new Attribute("Batt_Current", 1));
        _attributes.add(new Attribute("Batt_Temp", 2));
        _attributes.add(new Attribute("BattPercentLevel", 3));

        _attributes.add(new Attribute("Local_TX_Packet_Delta", 4));
        _attributes.add(new Attribute("Local_TX_Byte_Delta", 5));
        _attributes.add(new Attribute("Local_RX_Packet_Delta", 6));
        _attributes.add(new Attribute("Local_RX_Byte_Delta", 7));
        _attributes.add(new Attribute("Mobile_TX_Packet_Delta", 8));
        _attributes.add(new Attribute("Mobile_TX_Byte_Delta", 9));
        _attributes.add(new Attribute("Mobile_RX_Packet_Delta", 10));
        _attributes.add(new Attribute("Mobile_RX_Byte_Delta", 11));

        //Memory Usage
        _attributes.add(new Attribute("Memory_Available", 12));
        _attributes.add(new Attribute("Memory_Percentage", 13));

        //CPU_Usage
        _attributes.add(new Attribute("Load_Avg_1_min", 14));
        _attributes.add(new Attribute("Load_Avg_5_min", 15));
        _attributes.add(new Attribute("Load_Avg_15_min", 16));
        _attributes.add(new Attribute("Running_Entities", 17));
        _attributes.add(new Attribute("Total_Entities", 18));

        //Nominal attribute
        ArrayList<String> classification = new ArrayList<>(2);
        classification.add("Normal");
        classification.add("Attack");
        _attributes.add(new Attribute("Class", classification));

    }


    private ArrayList<Attribute> _attributes;




    public Instances getEmptyInstances(){

        return new Instances("AndroidAttack", _attributes, 1);
    }

}
