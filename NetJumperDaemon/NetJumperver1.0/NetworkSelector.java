import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Class: NetworkSelector.java
 * Description: A class that provide selection algorithm using weighted sum method
 * @author: Chitipat Marsri
 * @Javadoc Comments: Chitipat Marsri
 * @create: 09 May 2023
 * @LastUpdate: 24 May 2023
 */
public class NetworkSelector {
        
    // Define weights for each attribute (latency, packet loss and mean deviation) and acceptable threshold 
    private static double weightLatency;//weight latency 
    private static double weightPacketLoss;//weight packet loss 
    private static double weightMeanDeviation;//weight mean deviation 
    private static double diffThreshold;//acceptable threshold
    /**
     * Constructor of NetworkSelector.java class.
     */
    public NetworkSelector() {
        weightLatency = 0.40;
        weightPacketLoss = 0.30;
        weightMeanDeviation = 0.20;
        diffThreshold = 20;
    }
    /**
     * Constructor of NetworkSelector.java class
     * @param weightLatency weight of latency
     * @param weightPacketLoss weight od packet loss
     * @param weightMeanDeviation weight of mean deviation
     * @param diffThreshold acceptable threshold
     */
    public NetworkSelector (double weightLatency, double weightPacketLoss, double weightMeanDeviation, double diffThreshold) {
        this.weightLatency = weightLatency;
        this.weightPacketLoss = weightPacketLoss;
        this.weightMeanDeviation = weightMeanDeviation;
        this.diffThreshold = diffThreshold;
    }
    /**
     * Method: selectBestNetwork
     * Description: Select best network using weighted sum method
     * @param networks list of network objects
     * @param currentNet current network
     * @return best network object
     */
    public ArrayList<Network> selectBestNetwork(ArrayList<Network> networks, Network currentNet) {
        int currentIndex = 0;
        // Normalize the attribute values of each network
        normalize(networks);

        // Calculate the weighted sum of each network
        for (Network network : networks) {
            double weightedSum = weightLatency * network.getNormalizedLatency() + weightPacketLoss * network.getNormalizedPacketLoss() + weightMeanDeviation * network.getNormalizedMeanDeviation();
            network.setWeightedSum(weightedSum);
        }

        // Sort the networks based on their weighted sum in descending order
        Collections.sort(networks, Comparator.comparing(Network::getWeightedSum));
        // check for index of current network
        for (int i = 0; i < networks.size(); i++) {
            if (networks.get(i).getName().equals(currentNet.getName())) {
                currentIndex = i;
            }
        }
        //check that is it worth switching network
        if (!currentNet.equals(networks.get(0)) && !shouldSwitch(currentNet, networks.get(0))) {
            Collections.swap(networks, 0, currentIndex);
        } 
        // Return the network with the highest weighted sum
        return networks;
    }
    /**
     * Method: normalize
     * Description: normalize data list of network
     * @param networks list of network objects
     */
    private void normalize(ArrayList<Network> networks) {
        // Find the minimum and maximum values for each attribute
        double minLatency = Double.MAX_VALUE;
        double maxLatency = Double.MIN_VALUE;
        double minPacketLoss = Double.MAX_VALUE;
        double maxPacketLoss = Double.MIN_VALUE;
        double minMeanDeviation = Double.MAX_VALUE;
        double maxMeanDeviation = Double.MIN_VALUE;
        
        for (Network network : networks) {
            double latency = network.getLatency().get(1);
            double packetLoss = network.getPacketLoss();
            double meanDeviation = network.getLatency().get(3);
            if (latency < minLatency) {
                minLatency = latency;
            }
            if (latency > maxLatency) {
                maxLatency = latency;
            }
            if (packetLoss < minPacketLoss) {
                minPacketLoss = packetLoss;
            }
            if (packetLoss > maxPacketLoss) {
                maxPacketLoss = packetLoss;
            }
            if (meanDeviation < minMeanDeviation) {
                minMeanDeviation = meanDeviation;
            }
            if (meanDeviation > maxMeanDeviation) {
                maxMeanDeviation = meanDeviation;
            }
        }

        // Normalize the values of each attribute to the range [0, 1]
        for (Network network : networks) {
            double latency = network.getLatency().get(1);
            double packetLoss = network.getPacketLoss();
            double meanDeviation = network.getLatency().get(3);

            double normalizedLatency = (latency - minLatency) / (maxLatency - minLatency);
            double normalizedPacketLoss = (packetLoss - minPacketLoss) / (maxPacketLoss - minPacketLoss);
            double normalizedMeanDeviation = (meanDeviation - minMeanDeviation) / (maxMeanDeviation - minMeanDeviation);
            network.setNormalizedLatency(normalizedLatency);
            network.setNormalizedPacketLoss(normalizedPacketLoss);
            network.setNormalizedMeanDeviation(normalizedMeanDeviation);
        }
    }
    /**
     * Method: shouldSwitch
     * Description: method to check that should we switch the network or not
     * @param currentNet current network
     * @param bestNet best network
     * @return 
     */
    private boolean shouldSwitch(Network currentNet, Network bestNet) {
        diffThreshold = 20; //acceptable different between latency
        double latencyDiff = currentNet.getLatency().get(1) - bestNet.getLatency().get(1);
        
        boolean acceptableLatency = latencyDiff < diffThreshold;
        boolean betterPacketLoss = bestNet.getPacketLoss() < currentNet.getPacketLoss();
        boolean betterMeanDeviation = bestNet.getLatency().get(3) < currentNet.getLatency().get(3);
                
        if (acceptableLatency) { // modify the threshold as needed
            if (betterPacketLoss) {
                return true;
            } else {
                return betterMeanDeviation;
            }
        }
        else {
            return true;
        }
    }
    /** 
    * Method: main 
    * Description: main method to test the functionality of the class.
     * @param args
    */ 
    public static void main(String[] args) {
        /*
        //test case
        Network n1 = new Network("net1", "1000000", 100);
        Network n2 = new Network("net2", "2000000", 110);
        Network n3 = new Network("net3", "3000000", 120);
        Network n4 = new Network("net4", "4000000", 130);
        Network n5 = new Network("net5", "5000000", 140);
        ArrayList<Network> networks = new ArrayList<>();
        networks.add(n1);
        networks.add(n2);
        networks.add(n3);
        networks.add(n4);
        networks.add(n5);
        //test setLatency() and sort algorithm
        ArrayList<Double> latencyList1 = new ArrayList<>(List.of(0.0, 20.0, 0.0, 20.0));
        ArrayList<Double> latencyList2 = new ArrayList<>(List.of(0.0, 50.0, 0.0, 9.0));
        ArrayList<Double> latencyList3 = new ArrayList<>(List.of(0.0, 30.0, 0.0, 13.0));
        ArrayList<Double> latencyList4 = new ArrayList<>(List.of(0.0, 100.0, 0.0, 2.0));
        ArrayList<Double> latencyList5 = new ArrayList<>(List.of(0.0, 100.0, 0.0, 5.0));
        n1.setLatency(latencyList1);
        n2.setLatency(latencyList2);
        n3.setLatency(latencyList3);
        n4.setLatency(latencyList4);
        n5.setLatency(latencyList5);
        n1.setPacketLoss(3.0);
        n2.setPacketLoss(0.0);
        n3.setPacketLoss(3.0);
        n4.setPacketLoss(15.0);
        n5.setPacketLoss(10.0);
        
        NetworkSelector test = new NetworkSelector();
        ArrayList<Network> sortedNetworkList = test.selectBestNetwork(networks, n5);
        for (Network network : sortedNetworkList) {
            System.out.println(network + "\tavg latency: " + network.getLatency().get(1) + "\tpacket loss: " + network.getPacketLoss()+ "\tmean deviation: " + network.getLatency().get(3));
        }
        System.out.println("\n\n");
        NetworkSelector test2 = new NetworkSelector(0.7,0.2,0.1,20);
        ArrayList<Network> sortedNetworkList2 = test2.selectBestNetwork(networks, n5);
        for (Network network : sortedNetworkList2) {
            System.out.println(network + "\tavg latency: " + network.getLatency().get(1) + "\tpacket loss: " + network.getPacketLoss()+ "\tmean deviation: " + network.getLatency().get(3));
        }*/
    }
}