import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
/**
 * C1 
 * Class 1: Represents each type of Network and stores it's properties and metrics 
 * @author Citipat Mrsri 
 * Javadoc comments by Gobi Jegarjasingham 
 * @version 2.0 
 * @date 27 Mar 2023
 *
 */
 
public class Network implements Comparable<Network> 

    /**
    * A1
    * Attribute 1: Network Name
    */
    
    private String name;
    
    /** 
    * A2 
    * Attribute 2: Network IP Address
    */
  
    private String ipAddress;
    
    /** 
    * A3
    * Attribute 3: Network Metric 
    */
   
    private Integer metric;
    
    /**
    * A4
    * Attribute 4: Packet Loss Metric 
    */ 
    
    private Double packetLoss;
    
    /**
    * A5
    * Attribute 5: Latency Metric 
    */ 
    
    private ArrayList<Double> latency;
    
    /**
    * M1 
    * Method 1: Constructor Method 
    * Description: Instantiates the Network's name, IP Address and Metrics 
    */ 
    
    public Network(String name, String ipAddress, Integer metric) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.metric = metric;
        this.packetLoss = 1.00;
        this.latency = new ArrayList<>();
    }
    
    /**
    * M2
    * Method 2: getName 
    * Description: Returns the Network Name
    */ 

    public String getName() {
        return name;
    }

    /**
    * M3 
    * Method 3: getIpAdress
    * Description: Returns the IP Address
    */
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
    * M4 
    * Method 4: getMetric
    * Description: Returns the metric 
    */ 
    
    public Integer getMetric() {
        return metric;
    } 
    
    /** 
    * M5 
    * Method 5: getPacketLoss() 
    * Description: Returns the Packet Loss metric 
    */
    
    public Double getPacketLoss() {
        return packetLoss;
    }
    
    /** 
    * M6
    * Method 6: getLatency 
    * Description: Returns the Latency metric 
    */ 
    
    public ArrayList<Double> getLatency() {
        return latency;
    }
    
    /**
    * M7
    * Method 7: setName 
    * Description: Sets the name of the network 
    */ 
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
    * M8
    * Method 8: setIpAddress 
    * Description: Sets the IP Address 
    */ 
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    /** 
    * M9 
    * Method 9: setMetric
    * Description Sets the Metric 
    */ 
    
    public void setMetric(Integer metric) {
        this.metric = metric;
    }
    
    /** 
    * M10 
    * Method 10: setPacketLoss 
    * Description: Sets the Packet loss metric 
    */ 
    
    public void setPacketLoss(Double pac) {
        this.packetLoss = pac;
    }
    
    /** 
    * M11
    * Method 11: setLatency 
    * Description: Sets the Latency metrics 
    */ 
    
    public void setLatency(ArrayList<Double> laten) {
        this.latency = laten;
    }
    
    /**
    * M12
    * Method 12: equals 
    * Description: Checks whether two network objects are equal
    * @param obj 
    * @param name
    * @param ipAddress
    * @param metric 
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
    
    /** 
    * M13
    * Method 13: compareTo 
    * Description: Compares two latency metrics together
    * @param: other 
    */ 
    
    public int compareTo(Network other) {
        return Double.compare(this.latency.get(1), other.latency.get(1));
    }
    
    /** 
    * M14 
    * Method 14: toString
    * Description: Retures network <name> + <ipAddress> + <metric> 
    */ 
    
    @Override
    public String toString() {
        return "Network{" + "name=" + name + ", ipAddress=" + ipAddress + ", metric=" + metric + '}';
    }
    
    /** 
    * M15
    * Method 15: main 
    * Description: Tests Constructor, toString and equal methods 
    */ 
    
    public static void main(String[] args) {
    
        //  Tests Contructor, toString() and equal() method
        
        Network n1 = new Network("wwan1", "1000000", 100);
        Network n2 = new Network("wwan2", "2000000", 110);
        Network n3 = new Network("wwan3", "3000000", 120);
        Network n4 = new Network("wwan4", "4000000", 130);
        Network n5 = new Network("wwan5", "5000000", 140);
        System.out.println(n1);
        System.out.println(n1.equals(n2));
        
        // Tests setLatency() and sort algorithm
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
        
        
        // Sorting algorithm
        
        List<Network> sortedNetworks = new ArrayList<>();
        List<Network> badNetworks = new ArrayList<>();
        sortedNetworks = networks;
        
        // First step: Filter out networks with high latency or packet loss
        
        for (Network network : sortedNetworks) {
            if (network.getLatency().get(1) > 150 || network.getPacketLoss() > 2) {
                badNetworks.add(network);
            }
        }
        
        sortedNetworks.removeIf(network -> network.getLatency().get(1) > 150 || network.getPacketLoss() > 2);
        
        // Second step: sort the remaining networks based on latency
        
        Collections.sort(sortedNetworks);
        
        // Third step: compare differences in latency and packet loss
        
        for (int i = 0; i < sortedNetworks.size() - 1; i++) {
            Network currentNetwork = sortedNetworks.get(i);
            Network nextNetwork = sortedNetworks.get(i+1);
            
            double latencyDiff = nextNetwork.getLatency().get(1) - currentNetwork.getLatency().get(1);
            double packetLossDiff = nextNetwork.getPacketLoss() - currentNetwork.getPacketLoss();
            
            if (latencyDiff <= 20) {
                if (packetLossDiff < 0) {
                    Collections.swap(sortedNetworks, i, i+1);
                }
            } 
        }
        for (Network network : badNetworks) {
            sortedNetworks.add(network);
        }
        System.out.println("\n");
        for (Network network : networks) {
            System.out.println(network.getName() + ": " + network.getLatency().get(1) + " ms" + "packet loss: " + network.getPacketLoss() + " %");
        }
        
        // Fourth step: Change the Metric 
        
        for (int i = 1; i < networks.size(); i++) {
            networks.get(i).setMetric(networks.get(0).getMetric()+i);
        }
        for (Network network : networks) {
            System.out.println(network.getName() + ": " + network.getLatency().get(1) + " ms" + "packet loss: " + network.getPacketLoss() + " %, metric" + network.getMetric());
        }
    }
}
