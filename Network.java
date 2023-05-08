import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
/**
 * A class that represent each type of Network and store its property.
 * @author chitipat marsri
 * @version 1.1 - 27 Mar 2023
 */
public class Network {
    //Attributes
    //network name
    private String name;
    //network IP address
    private String ipAddress;
    //network metric
    private Integer metric;
    
    //private String packetLoss;
    //private ArrayList<Double> latency;
    
    public Network(String name, String ipAddress, Integer metric) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.metric = metric;
        //this.packetLoss = "";
        //this.latency = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getMetric() {
        return metric;
    }
    /*
    public String getPacketLoss() {
        return packetLoss;
    }
    
    public ArrayList<Double> getLatency() {
        return latency;
    }
    */
    public void setName(String name) {
        this.name = name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setMetric(Integer metric) {
        this.metric = metric;
    }
    /*
    public void setPacketLoss(String pac) {
        this.packetLoss = pac;
    }
    
    public void setLatency(ArrayList<Double> laten) {
        this.latency = laten;
    }
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Network other = (Network) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.ipAddress, other.ipAddress)) {
            return false;
        }
        if (!Objects.equals(this.metric, other.metric)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Network{" + "name=" + name + ", ipAddress=" + ipAddress + ", metric=" + metric + '}';
    }
    
    public static void main(String[] args) {
        Network n1 = new Network("wwan0", "1000000", 100);
        Network n2 = new Network("wwan0", "1000000", 100);
        System.out.println(n1);
        System.out.println(n1.equals(n2));
    }
}
