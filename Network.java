import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;
/**
 * Class: Node.java
 * Description: A class that represent each Node that can connect to different types of network.
 * @author: Chitipat Marsri
 * @Javadoc Comments: Gobi Jegarajasingham
 * @create: 20 Mar 2023
 * @Version 2.1
 * @LastUpdate: 03 May 2023
 */
public class Node {
    //Attributes
    /**
    * Attribute: Network Size 
    * Description: Number of Networks 
    */ 
    private int networkSize; 
    /**
    * Attribute: networkList 
    * Description: Array of available connected networks 
    */ 
    private ArrayList<Network> networkList = new ArrayList<>();
    /**
    * Attribute: jsch
    * Description: Java implementation of SSH 2 that enables connection to an SSH server 
    */ 
    private JSch jsch;
    /** 
    * Attribute: session 
    */
    private Session session;
    /** 
    * Attribute: password 
    */ 
    private String password;
    /**
    * Method: Constructor of Node.java class that establish connection with PuTTY
    * Description: Connects the java program to the Linux Ubuntu testbed server 
    * @param host
    * @param username
    * @param password 
    */
    public Node(String host, String username, String password) {
        this.password = password;
        networkSize = 0;
        //establish connection
        try {
            jsch = new JSch();
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            // Disable strict host key checking
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            // Connect to the remote server
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
    /**
     * Method: This method will parse required commands through PuTTY
     * Description: Gives the testbed commands to extract the network names, set metrics, packet loss and latency 
     * @param command string of command 
     * @return ArrayList<String> of the result from Putty
     */
    public ArrayList<String> giveCommand(String command) {
        ArrayList<String> out = new ArrayList<>();
        try {
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();     
            // connect channel
            channel.connect();
            //read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                out.add(line);
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    /**
     * Method: This method will give sudo command to the testbed through PuTTY 
     * @param command string of command 
     * @param sudoPassword password for sudo command
     * @return ArrayList<String> of the result from Putty
     */
    public ArrayList<String> giveSudoCommand(String command) {
        ArrayList<String> output = new ArrayList<>();
        try {
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
            channel.setInputStream(null);
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            // connect channel
            channel.connect();
            // put in password
            out.write((password + "\n").getBytes());
            out.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                output.add(line);
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    /**
     * Method: getName_Metric_IP
     * Description: Pastes the command to giveCommand and extracts the relevant information such as name, IP address, metric
     */
    public void getName_Metric_IP() {
        String command = "ip route";
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> ip = new ArrayList<>();
        ArrayList<Integer> metric = new ArrayList<>();
        ArrayList<String> connectionName = new ArrayList<>();
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(command);   
        //loop for extracting data
        int count = 0;
        int num = 0;
        //loop for extracting data
        while (num <= in.size()-1) {
            String[] data = in.get(num).split(" ");
            for (int j = 0;j <= data.length-1; j++) {
                if (data[j].equals("default")||data[j].equals("\ndefault")){
                    if (data[j+7].equals("metric")) {
                        //create a Network from data and add into networkList
                        ip.add(data[j+2]);
                        name.add(data[j+4]);
                        int met = Integer.parseInt(data[j+8]);
                        metric.add(met);
                        networkList.add(new Network(name.get(count), ip.get(count), metric.get(count))); //create Network object
                        count++;
                    }
                }
            } 
            num++;
        }
        networkSize = count;
    }  
    public void getNetworkInfo() {
        
    }
    /**
    * Method: changeMetric
    * Description: Pastes the command to giveCommand method and changes the metric of a network 
    * @param networkName a network that will change the metric
    * @param newMetric new metric number
    */
    public void changeMetric(String networkName, int newMetric) {
        String cmd1 = "nmcli con edit '" + networkName + "'";
        String cmd2 = "set ipv4.route-metric " + newMetric;
        String cmd3 = "save";
        String cmd4 = "quit";
        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p '' " + cmd1);
            channel.setInputStream(null);
            OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            ((ChannelExec) channel).setPty(true);
            channel.connect();
            out.write((password + "\n").getBytes());
            out.flush();
            timer(1);
            out.write((cmd2 + "\n").getBytes());
            out.flush();
            timer(1);
            out.write((cmd3 + "\n").getBytes());
            out.flush();
            timer(1);
            out.write((cmd4 + "\n").getBytes());
            out.flush();
            timer(1);
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    //System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    //System.out.println("Exit status: " + channel.getExitStatus());
                    break;
                }
            }
            channel.disconnect();
            System.out.println("change metric successful");
            for (Network network : networkList) {
                if (network.getName().equals(networkName)) {
                    network.setMetric(newMetric);
            }
        }
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
    /**
    * Method: turnOffNetwork
    * Description: This method will turn the network off
    * @param networkName a required network name
    */
    public void turnOffNetwork(String networkName) {
        ArrayList<String> in = new ArrayList<>();
        String command = "nmcli con down '" + networkName + "'";
        in = giveSudoCommand(command);
        System.out.println("Turn off " + networkName + " successfully");
    }
    /**
    * Method: turnOnNetwork
    * Description: Turns the Network on 
    * @param networkName a required network name
    */
    public void turnOnNetwork(String networkName) {
        ArrayList<String> in = new ArrayList<>();
        String command = "nmcli con up '" + networkName + "'";
        in = giveSudoCommand(command);
        System.out.println("Turn on " + networkName + " successfully");
    }
    /**
    * Method: pingNetwork
    * Description: Pastes the command to giveCommand method and ping the required network
    * @param net a network that will be pinged
    * @param pingTime the number of time to ping
    */
    public void pingNetwork(Network net, int pingTime) {
        String packetLoss = new String();
        String[] latency = new String[4];   
        String pingCmd = "ping -c " + pingTime + " " +  net.getIpAddress();
        ArrayList<String> in = new ArrayList<>();
        
        in = giveCommand(pingCmd);
        String ping1 = in.get(0);
        String laten = "";
        String[] data1 = ping1.split(" ");
        for (int i = 0;i <= data1.length; i++) {
            if (data1[i].equals("packet")) {
                packetLoss = data1[i-1];
            }
            else if (data1[i].equals("time")) {
                laten = data1[i+1].split("m")[0];
                break;
            }
        }
        ArrayList<Double> latencyList = new ArrayList<>(List.of(Double.parseDouble(laten), Double.parseDouble(laten), Double.parseDouble(laten), Double.parseDouble(laten)));
        System.out.println("\n" + net + "\nPacket loss: " + packetLoss
                     + "\naverage latency: " + latencyList.get(1));
        net.setLatency(latencyList);
        net.setPacketLoss(Double.parseDouble(packetLoss.substring(0, packetLoss.length()-1)));  
    }
    /**
    * Method: getNetorkList 
    * Description: Returns a list of networks
    * @return a list of network
    */
    public ArrayList<Network> getNetworkList() {
        return networkList;
    }
    /**
    * Method: getNetworkListSize
    * Description: Returns the size of network list
    * @return int of size
    */
    public int getNetworkListSize() {
        return networkList.size();
    }
    /**
    * Method: printNetworkList
    * Description: Method to print the network bearer in the network list
    */
    public void printNetworkList() {
        for (Network net : networkList) {
            System.out.println(net);
        }
    }
    /**
    * Method: sortNetworks
    * Description: Sorts network list 
    */
    public void sortNetworks() {
        int latencyMax = 150; //max acceptable latency
        int plMax = 2; //max acceptable packet loss
        int acceptDiff = 20; //acceptable different between latency
        List<Network> badNetworks = new ArrayList<>();
        //check packet loss to be less than 2%, latency < 150 ms
        // First step: filter out networks with high latency or packet loss
        for (Network network : networkList) {
            if (network.getLatency().get(1) > latencyMax || network.getPacketLoss() > plMax) {
                badNetworks.add(network);
            }
        }
        networkList.removeIf(network -> network.getLatency().get(1) > latencyMax || network.getPacketLoss() > plMax);
        // Second step: sort the remaining networks based on latency
        Collections.sort(networkList);
        // Third step: compare differences in latency and packet loss
        for (int i = 0; i < networkList.size() - 1; i++) {
            Network currentNetwork = networkList.get(i);
            Network nextNetwork = networkList.get(i+1);
            
            double latencyDiff = nextNetwork.getLatency().get(1) - currentNetwork.getLatency().get(1);
            double packetLossDiff = nextNetwork.getPacketLoss() - currentNetwork.getPacketLoss();
            
            if (latencyDiff <= acceptDiff) {
                if (packetLossDiff < 0) {
                    Collections.swap(networkList, i, i+1);
                }
            } 
        }
        for (Network network : badNetworks) {
            networkList.add(network);
        }
        System.out.println("\n");
        for (Network network : networkList) {
            System.out.println(network.getName() + ": avg latency: " + network.getLatency().get(1) + " ms packet loss: " + network.getPacketLoss() + " % metric" + network.getMetric());
        }
    }
    /**
    * Method: timer 
    * Description: Sets a timer between each command
    * @param second 
    */
    public void timer(int second) {
        try {
            Thread.sleep(second*1000); // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            e.getMessage();// handle the exception if needed
        }
    }
    /**
    * Method: monitor 
    * Description: Automatically change network
    * @param times 
    */
    public void monitor(int times) {
        /* 
        for (int n = 0; n < times; n++) {
            for (Network network : networkList) {
                pingNetwork(network, 5);
            }
            sortNetworks();
            for (int i = 1; i < networkList.size(); i++) {
                int newMetric =networkList.get(0).getMetric()+i;
                changeMetric(networkList.get(i).getName(), newMetric);
                turnOffNetwork(networkList.get(i).getName());
                turnOnNetwork(networkList.get(i).getName());
            }
            timer(30);
        }
        */
        // for currently Testbed
        for (int n = 0; n < times; n++) {
            for (Network network : networkList) {
                pingNetwork(network, 5);
            }
            sortNetworks();
            if (networkList.get(0).getName().equals("wwan0")) {
                int newMetric =networkList.get(1).getMetric()-10;
                changeMetric(networkList.get(0).getName(), newMetric);
                turnOffNetwork(networkList.get(0).getName());
                turnOnNetwork(networkList.get(0).getName());
            } else {
                int newMetric =networkList.get(0).getMetric()+10;
                changeMetric(networkList.get(1).getName(), newMetric);
                turnOffNetwork(networkList.get(1).getName());
                turnOnNetwork(networkList.get(1).getName());
            }
            timer(30);
        }
    }
    /**
    * Method: disconnectSSHConnection
    * Description: Disconnects the Putty 
    */ 
    public void disconnectSSHConnection() {
        try {
            session.disconnect();
            System.out.println("Disconnect successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /** 
    * Method:init 
    * Description: Run all subsequent methods
    */ 
    public void init() {
        getName_Metric_IP();
        printNetworkList();
        /*
        changeMetric("wwan0", 140);
        turnOffNetwork("wwan0");
        turnOnNetwork("wwan0");
        */
        
        //test sortNetworkList()
        
        Collections.swap(networkList, 0, 1);
        printNetworkList();
        sortNetworks();
        
        
        //test monitor()
        /*
        Collections.swap(networkList, 0, 1);
        printNetworkList();
        monitor(1);
        */
        disconnectSSHConnection();
        //printNetworkList();
    }
    /** 
    * Method: main 
    * Description: Runs the init() method for the existing node with all subsequent methods as per the method's comments
    * @param args
    */ 
    public static void main(String[] args) {
        String ipAddr = ;
        String user = ;
        String password = ;
        Node n1 = new Node(ipAddr, user, password);
        n1.init();
    }
}
