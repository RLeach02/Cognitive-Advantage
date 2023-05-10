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
 * @LastUpdate: 10 May 2023
 */
public class NetworkSelector {
    // Define weights for each attribute (latency and packet loss)
    private static final double WEIGHT_LATENCY = 0.3;
    private static final double WEIGHT_PACKET_LOSS = 1 - WEIGHT_LATENCY;
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
            double weightedSum = WEIGHT_LATENCY * network.getNormalizedLatency() + WEIGHT_PACKET_LOSS * network.getNormalizedPacketLoss();
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
        for (Network network : networks) {
            double latency = network.getLatency().get(1);
            double packetLoss = network.getPacketLoss();
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
        }

        // Normalize the values of each attribute to the range [0, 1]
        for (Network network : networks) {
            double latency = network.getLatency().get(1);
            double packetLoss = network.getPacketLoss();
            double normalizedLatency = (latency - minLatency) / (maxLatency - minLatency);
            double normalizedPacketLoss = (packetLoss - minPacketLoss) / (maxPacketLoss - minPacketLoss);
            network.setNormalizedLatency(normalizedLatency);
            network.setNormalizedPacketLoss(normalizedPacketLoss);
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
        final double DIFF_THRESHOLD = 20; //acceptable different between latency
        double latencyDiff = Math.abs(currentNet.getLatency().get(1) - bestNet.getLatency().get(1));
        double packetLossDiff = bestNet.getPacketLoss() - currentNet.getPacketLoss();
        if (latencyDiff < DIFF_THRESHOLD) { // modify the threshold as needed
            if (packetLossDiff < 0) {
                return true;
            }
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
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
        ArrayList<Double> latencyList1 = new ArrayList<>(List.of(0.0, 80.0, 0.0, 0.0));
        ArrayList<Double> latencyList2 = new ArrayList<>(List.of(0.0, 50.0, 0.0, 0.0));
        ArrayList<Double> latencyList3 = new ArrayList<>(List.of(0.0, 70.0, 0.0, 0.0));
        ArrayList<Double> latencyList4 = new ArrayList<>(List.of(0.0, 50.0, 0.0, 0.0));
        ArrayList<Double> latencyList5 = new ArrayList<>(List.of(0.0, 75.0, 0.0, 0.0));
        n1.setLatency(latencyList1);
        n2.setLatency(latencyList2);
        n3.setLatency(latencyList3);
        n4.setLatency(latencyList4);
        n5.setLatency(latencyList5);
        n1.setPacketLoss(6.0);
        n2.setPacketLoss(6.0);
        n3.setPacketLoss(7.0);
        n4.setPacketLoss(15.0);
        n5.setPacketLoss(6.0);
        
        NetworkSelector test = new NetworkSelector();
        ArrayList<Network> sortedNetworkList = test.selectBestNetwork(networks, n5);
        for (Network network : sortedNetworkList) {
            System.out.println(network + "\tavg latency: " + network.getLatency().get(1) + "\tpacket loss: " + network.getPacketLoss());
        }
    }
}
