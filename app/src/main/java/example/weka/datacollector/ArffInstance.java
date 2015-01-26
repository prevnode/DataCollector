package example.weka.datacollector;

/**
 * Created by lenovo on 1/21/2015.
 */
public class ArffInstance {


    //CPU_Usage
    int Load_Avg_1_min;
    int Load_Avg_5_min;
    int Load_Avg_15_min;
    int Runnable_Entities;
    int Total_Entities;

    //Network
    public long TxLocalPacketDelta;
    public long RxLocalPacketDelta;
    public long TxMobilePacketDelta;
    public long RxMobilePacketDelta;


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
    public float BattPercentLevel = -1;
    public String Class = new String();

    @Override
    public String toString(){
        StringBuilder line = new StringBuilder(30);
        line.append(BattVoltage);
        line.append(',');
        line.append(BattCurrent);
        line.append(',');
        line.append(BattTemp);
        line.append(',');
        line.append(BattPercentLevel);
        line.append(',');
        line.append(Class);
        line.append(TxLocalPacketDelta);
        line.append(',');
        line.append(RxLocalPacketDelta);
        line.append('\n');
        return line.toString();
    }
}
