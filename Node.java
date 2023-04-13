import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;

/**
 * Class 1: Node.java
 *
 * A class that represent each Node that can connect to different types of network.
 *
 * @author: Chitipat Marsri
 *
 * @version: 2.0
 *
 * Date Updated: 04 Apr 2023
 *
 */

public class Node_preMVP {

    /**
     * Attribute 1: networkSize
     * Type: int
     * Description Number of Networks
     */

    private int networkSize = 2; //number of network

    /**
     * Attribute 2: networkList
     * Type: ArrayList<Network>
     * Description: Stores a list of network bearers
     */

    private ArrayList<Network> networkList = new ArrayList<>();

    /**
     * Attribute 3: jsch
     * Type: Jsch
     * Description: Java implementation of SSH2, to connect to a SSH Server, using Port forwarding
     * Requirement: import com.java.craft.jsch
     */

    private JSch jsch;

    /**
     * Attribute 4: session
     * Type: Session
     * Description: Enables connection to Linux Ubuntu Testbed server using PuTTY
     */

    private Session session;

    /**
     * Attribute 5: password
     * Type: String
     * Description: User password exists as an attribute as it is recurringly used, particularly in the sudo command
     */

    private String password;

    // private Channel channel;
    //Unsure about this attribute, no usages in the code, unsure on it's functionality

    /**
     * M1
     * Method 1: Constructor of Node.java
     * Description: Establishes connection with PuTTY
     * @param host
     * @param username
     * @param password
     *
     */

    public Node(String host, String username, String password) {
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
     * M2
     * Method 2: giveCommand
     * Description: This method will parse relevant command through Putty into the Ubuntu Testbed
     * @param command string of command 
     * @return ArrayList<String> of the result from Putty
     *
     */

    public ArrayList<String> giveCommand(String command) {
        ArrayList<String> out = new ArrayList<>();
        try {
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            // connect channel
            InputStream in = channel.getInputStream();
            channel.connect();
            
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.add(new String(buffer));
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    /**
     * M3
     * Method 3: getName_Metric_IP
     * Description:  This method will paste the command to giveCommand method
     * and extract the relevant information such as name, IP address and metrics
     * @param command string of command
     *
     */
    public void getName_Metric_IP(String command) {
        String[] name = new String[networkSize];
        String[] ip = new String[networkSize];
        Integer[] metric = new Integer[networkSize];
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(command);
        //loop for extracting data
        for (int i = 0;i <= networkSize-1; i++) {     
            String[] data = in.get(i).split(" ");
            for (int j = 0;j <= data.length; j++) {
                if (data[j].equals("dev")) {
                    name[i] = data[j+1];
                }
                else if (data[j].equals("via")) {
                    ip[i] = data[j+1];
                }
                else if (data[j].equals("metric")) {
                    metric[i] = Integer.parseInt(data[j+1]);
                    break;
                }
            }
            networkList.add(new Network(name[i], ip[i], metric[i])); //create Network object
        }  
    }
    /**
     * M4
     * Method 4: changeMetric
     * Description: Pastes the command into Linux Ubuntu Testbed enabling the change of network metric
     * Example: 100 -> 700 : In the case of high latency or packet loss
     * @param net a network that will change the metric
     * @paaram command string of command
     * @param newMetric new metric number
     *
     */
    public void changeMetric(Network net, String command, int newMetric) {
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(command);
        
    }
    /**
     * M5
     * Method 5: pingNetwork
     * Description: Parses the ping commands to the giveCommand method and pings the required network
     * This method will paste the command to giveCommand method and ping the required network
     * @param net a network that will be pinged
     * @param pingTime the number of time to ping
     *
     */

    public void pingNetwork(Network net, int pingTime) {
        String packetLoss = new String();
        String[] latency = new String[4];   
        String pingCmd = "ping -c " + pingTime + " " +  net.getIpAddress();
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(pingCmd);
        String ping = in.get(pingTime-1);

        String[] data = ping.split(" ");
            for (int i = 0;i <= data.length; i++) {
                if (data[i].equals("packet")) {
                    packetLoss = data[i-1];
                }
                else if (data[i].equals("min/avg/max/mdev")) {
                    String allLatency = data[i+2];
                    latency = allLatency.split("/");
                    break;
                }
            }
            System.out.println(net + "\nPacket loss: " + packetLoss
                             + "\nmin latency: " + latency[0]
                             + "\naverage latency: " + latency[1]
                             + "\nmax latency: " + latency[2]
                             + "\nmdev latency: " + latency[3]);
    }
    /**
     * M6
     * Method 6: disconnectSSHConnection
     * Description: This method will disconnect from the Putty SSH connection
     *
     */
    public void disconnectSSHConnection() {
        try {
            session.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    
    public void init() {
        String cmd = "ip route";
        getName_Metric_IP(cmd);
        for (int i = 0;i <= networkSize-1; i++) {
            System.out.println(networkList.get(i));
        }
        pingNetwork(networkList.get(0), 5);
        
        
        
        
        
        disconnectSSHConnection();
    }
    public static void main(String[] args) {
        Node n1 = new Node("HOST IP ADDRESS", "USERNAME", "PASSWORD");
        n1.init();
        
    }
}
