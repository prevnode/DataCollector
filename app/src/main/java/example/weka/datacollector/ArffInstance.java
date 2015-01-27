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

    //Network statics are guaranteed to be monotonic so deltas should never be negative
    public long Local_TX_Packet_Delta = -1;
    public long Local_TX_Byte_Delta = -1;
    public long Local_RX_Packet_Delta = -1;
    public long Local_RX_Byte_Delta = -1;
    public long Mobile_TX_Packet_Delta = -1;
    public long Mobile_TX_Byte_Delta = -1;
    public long Mobile_RX_Packet_Delta = -1;
    public long Mobile_RX_Byte_Delta = -1;


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

    //Battery
    public int Batt_Voltage = -1;
    public int Batt_Current = -1;
    public int Batt_Temp = -1;
    public float BattPercentLevel = -1;
    public String Class = new String();

    @Override
    public String toString(){
        StringBuilder line = new StringBuilder(30);
        line.append(Batt_Voltage);
        line.append(',');
        line.append(Batt_Current);
        line.append(',');
        line.append(Batt_Temp);
        line.append(',');
        line.append(BattPercentLevel);
        line.append(',');
        line.append(Local_TX_Packet_Delta);
        line.append(',');
        line.append(Local_TX_Byte_Delta);
        line.append(',');
        line.append(Local_RX_Packet_Delta);
        line.append(',');
        line.append(Local_RX_Byte_Delta);
        line.append(',');
        line.append(Mobile_TX_Packet_Delta);
        line.append(',');
        line.append(Mobile_TX_Byte_Delta);
        line.append(',');
        line.append(Mobile_RX_Packet_Delta);
        line.append(',');
        line.append(Mobile_RX_Byte_Delta);
        line.append(',');
        line.append(Class);
        line.append('\n');
        return line.toString();
    }
}
