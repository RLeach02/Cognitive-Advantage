import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/**
 * Class: Represents each type of Network and stores it's properties and metrics 
 * @author: Chitipat Marsri 
 * @Javadoc Comments:  Gobi Jegarjasingham & Chitipat Marsri
 * @create: 20 Mar 2023
 * @LastUpdate: 10 May 2023
 */
public class Network implements Comparable<Network> {
    //Attributes
    //Attribute name: Network Name
    private String name;
    //Attribute ipAddress: Network IP Address
    private String ipAddress;
    //Attribute metric: Network Metric 
    private Integer metric;  
    //Attribute packetLoss: Packet Loss Metric 
    private Double packetLoss;
    //Attribute latency: Latency Metric 
    private ArrayList<Double> latency;
    //Attribute connectionName: name of the connection
    private String connectionName;
    //Attribute normalizedLatency: normalized latency
    private double normalizedLatency;
    //Attribute normalizedPacketLoss: normalized packet loss
    private double normalizedPacketLoss;
    //Attribute weightedSum: wieghted sum
    private double weightedSum;   
    /**
     * Method: Constructor Method 
     * Description: Instantiates the Network's name, IP Address and Metrics 
     * @param nName
     * @param ipAddress
     * @param metric 
     */
    public Network(String nName, String ipAddress, Integer metric) {
        this.name = nName;
        this.ipAddress = ipAddress;
        this.metric = metric;
        this.packetLoss = 1.00;
        this.latency = new ArrayList<>();
    }
    /**
     * Method: getName 
     * Description: Returns the Network Name
     * @return Network Name
     */
    public String getName() {
        return name;
    }
    /**
     * Method: getIpAdress
     * Description: Returns the IP Address
     * @return IP Address
     */
    public String getIpAddress() {
        return ipAddress;
    }
    /**
     * Method: getMetric
     * Description: Returns the metric 
     * @return metric 
     */
    public Integer getMetric() {
        return metric;
    } 
    /**
     * Method: getPacketLoss() 
     * Description: Returns the Packet Loss metric 
     * @return Packet Loss metric 
     */
    public Double getPacketLoss() {
        return packetLoss;
    }
    /**
     * Method: getConnectionName() 
     * Description: Returns the name of connection
     * @return name of connection
     */
    public String getConnectionName() {
        return connectionName;
    }
    /**
     * Method: getLatency 
     * Description: Returns the Latency metric 
     * @return Latency metric 
     */
    public ArrayList<Double> getLatency() {
        return latency;
    }
    /**
     * Method: getNormalizedLatency
     * Description: get normalized latency
     * @return normalized latency
     */
    public double getNormalizedLatency() {
        return normalizedLatency;
    }
    /**
     * Method: getNormalizedPacketLoss
     * Description: get normalized packet loss
     * @return normalized packet loss
     */
    public double getNormalizedPacketLoss() {
        return normalizedPacketLoss;
    }
    /**
     * Method: getWeightedSum()
     * Description: get weighted sum
     * @return weighted sum
     */
    public double getWeightedSum() {
        return weightedSum;
    }
    /**
     * Method: setName 
     * Description: Sets the name of the network 
     * @param name name of the network 
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Method: setIpAddress 
     * Description: Sets the IP Address 
     * @param ipAddress IP Address 
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    /**
     * Method: setMetric
     * Description Sets the Metric 
     * @param metric Metric
     */
    public void setMetric(Integer metric) {
        this.metric = metric;
    }
    /**
     * Method: setPacketLoss 
     * Description: Sets the Packet loss metric 
     * @param pac Packet loss metric
     */
    public void setPacketLoss(Double pac) {
        this.packetLoss = pac;
    }
    /**
     * Method: setConnectionName 
     * Description: Sets the name of the connection 
     * @param connectionName name of the connection 
     */
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    /**
     * Method: setLatency 
     * Description: Sets the Latency metrics 
     * @param laten Latency metrics 
     */
    public void setLatency(ArrayList<Double> laten) {
        this.latency = laten;
    }
    /**
     * Method: setNormalizedLatency
     * Description: set normalized latency
     * @param normalizedLatency normalized latency
     */
    public void setNormalizedLatency(double normalizedLatency) {
        this.normalizedLatency = normalizedLatency;
    }
    /**
     * Method: setNormalizedPacketLoss
     * Description: set normalized packet loss
     * @param normalizedPacketLoss normalized packet loss
     */
    public void setNormalizedPacketLoss(double normalizedPacketLoss) {
        this.normalizedPacketLoss = normalizedPacketLoss;
    }
    /**
     * Method: setWeightedSum()
     * Description: set weighted sum
     * @param weightedSum weighted sum
     */
    public void setWeightedSum(double weightedSum) {
        this.weightedSum = weightedSum;
    }
    /**
     * Method: equals 
     * Description: Checks whether two network objects are equal
     * @param obj object
     * @return true if equal, false otherwise
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
        if (!Objects.equals(this.connectionName, other.connectionName)) {
            return false;
        }
        return true;
    }
    /**
     * Method: compareTo 
     * Description: Compares two latency metrics together
     * @param other other network
     * @return int that indicate which network have higher latency
     */
    @Override
    public int compareTo(Network other) {
        return Double.compare(this.latency.get(1), other.latency.get(1));
    }
    /**
     * Method: toString
     * Description: print information about network
     * @return Returns network <name> + <ipAddress> + <metric> 
     */
    @Override
    public String toString() {
        return "Network{" + "name=" + name + ", ipAddress=" + ipAddress + ", metric=" + metric +"}";
    }
    /**
     * Method: main 
     * Description: Tests Constructor, toString and equal methods 
     * @param args 
     */
    public static void main(String[] args) {
        //test contructor, toString() and equal() method
        Network n1 = new Network("wwan1", "1000000", 100);
        Network n2 = new Network("wwan2", "2000000", 110);
        Network n3 = new Network("wwan3", "3000000", 120);
        Network n4 = new Network("wwan4", "4000000", 130);
        Network n5 = new Network("wwan5", "5000000", 140);
        System.out.println(n1);
        System.out.println(n1.equals(n2));
        
        //test setLatency() and sort algorithm
        ArrayList<Double> latencyList1 = new ArrayList<>(List.of(0.0, 40.3, 0.0, 0.0));
        ArrayList<Double> latencyList2 = new ArrayList<>(List.of(0.0, 38.3, 0.0, 0.0));
        ArrayList<Double> latencyList3 = new ArrayList<>(List.of(0.0, 35.5, 0.0, 0.0));
        ArrayList<Double> latencyList4 = new ArrayList<>(List.of(0.0, 18.3, 0.0, 0.0));
        ArrayList<Double> latencyList5 = new ArrayList<>(List.of(0.0, 155.5, 0.0, 0.0));
        n1.setLatency(latencyList1);
        n2.setLatency(latencyList2);
        n3.setLatency(latencyList3);
        n4.setLatency(latencyList4);
        n5.setLatency(latencyList5);
        n1.setPacketLoss(1.0);
        n2.setPacketLoss(0.5);
        n3.setPacketLoss(1.9);
        n4.setPacketLoss(2.5);
        n5.setPacketLoss(0.0);
        List<Network> networks = new ArrayList<>();
        networks.add(n1);
        networks.add(n2);
        networks.add(n3);
        networks.add(n4);
        networks.add(n5);
        Collections.sort(networks);
        for (Network network : networks) {
            System.out.println(network.getName() + ": " + network.getLatency().get(1) + " ms" + "packet loss: " + network.getPacketLoss() + " %");
        }
    }
}
