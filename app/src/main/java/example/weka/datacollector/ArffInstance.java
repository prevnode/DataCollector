package example.weka.datacollector;

/**
 * Created by lenovo on 1/21/2015.
 */
public class ArffInstance {

    //Battery
    public int Batt_Voltage = -1;
    public int Batt_Current = -1;
    public int Batt_Temp = -1;
    public float Batt_Percent_Level = -1;


    //Network statics are guaranteed to be monotonic so deltas should never be negative
    public long Local_TX_Packet_Delta = -2;
    public long Local_TX_Byte_Delta = -2;
    public long Local_RX_Packet_Delta = -2;
    public long Local_RX_Byte_Delta = -2;
    public long Mobile_TX_Packet_Delta = -2;
    public long Mobile_TX_Byte_Delta = -2;
    public long Mobile_RX_Packet_Delta = -2;
    public long Mobile_RX_Byte_Delta = -2;

    //Memory Usage
    public long Memory_Available = -3;  // in Megabytes
    public float Memory_Percentage = -3;


    //CPU_Usage
    float Load_Avg_1_min = -4;
    float Load_Avg_5_min = -4;
    float Load_Avg_15_min = -4;
    int Running_Entities = -4;
    int Total_Entities = -4;

    public String Class;

    @Override
    public String toString(){
        StringBuilder line = new StringBuilder(30);
        line.append(Batt_Voltage);
        line.append(',');
        line.append(Batt_Current);
        line.append(',');
        line.append(Batt_Temp);
        line.append(',');
        line.append(Batt_Percent_Level);
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
        line.append(Memory_Available);
        line.append(',');
        line.append(Memory_Percentage);
        line.append(',');
        line.append(Load_Avg_1_min);
        line.append(',');
        line.append(Load_Avg_5_min);
        line.append(',');
        line.append(Load_Avg_15_min);
        line.append(',');
        line.append(Running_Entities);
        line.append(',');
        line.append(Total_Entities);
        line.append(',');
        line.append(Class);
        line.append('\n');
        return line.toString();
    }
}
