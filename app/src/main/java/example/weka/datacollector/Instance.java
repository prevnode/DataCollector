package example.weka.datacollector;

/**
 * Created by lenovo on 1/21/2015.
 */
public class Instance {

     /*
    CPU_Usage
    Load_Avg_1_min
    Load_Avg_5_min
    Load_Avg_15_min
    Runnable_Entities
    Total_Entities
    */

    /*
    Network:
	Local_TX_Packets
	Local_TX_Bytes
	Local_RX_Packets
	Local_RX_Bytes
	Wifi_TX_Packets
	Wifi_TX_Bytes
	Wifi_RX_Packets
	Wifi_RX_Bytes
    */

    public int BattVoltage = -1;
    public int BattCurrent = -1;
    public int BattTemp = -1;
    public int BattLevelChange = -1;
    public int BattLevel = -1;
    public String Class = "Normal";

    @Override
    public String toString(){
        StringBuilder line = new StringBuilder(30);
        line.append(BattVoltage);
        line.append(',');
        line.append(BattCurrent);
        line.append(',');
        line.append(BattTemp);
        line.append(',');
        line.append(BattLevelChange);
        line.append(BattLevel);
        line.append(Class);
        line.append('\n');
        return line.toString();
    }
}