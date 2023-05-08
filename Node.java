import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;
/**
 * TEST
 * A class that represent each Node that can connect to different types of network.
 * @author chitipat marsri
 * @version 1.1 - 29 Mar 2023
 */
public class Node {
    //Attributes
    private int networkSize = 2; //number of network
    private ArrayList<Network> networkList = new ArrayList<>();
    private JSch jsch;
    private Session session;
    private Channel channel;
    /**
     * Constructor of Node.java class that establish connection with Putty
     * @param host
     * @param username
     * @param password 
     */
    public Node(String host, String username, String password) {
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
     * This method will give the parameter command to the Putty
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
            // connect channel
            InputStream in = channel.getInputStream();
            channel.connect();
            //read the output
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                String line = new String(buffer);
                out.add(line); //add output into ArrayList
                System.out.println(line + "\n");
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    /**
     * This method will paste the command to giveCommand method and extract the relevant information such as name, IP address, metric
     * @param command string of command
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
            //create a Network object and add to collection
            networkList.add(new Network(name[i], ip[i], metric[i])); //create Network object
        }  
    }
    /**
     * This method will paste the command to giveCommand method and change the metric of a network
     * @param net a network that will change the metric
     * @param newMetric new metric number
     */
    public void changeMetric(Network net, int newMetric) {
        ArrayList<String> in = new ArrayList<>();
        //command for change a metric
        String cmd = "sudo nmcli con edit wwan0 "
                + "password"
                + "set ipv4.route-metric 200"
                + "save"
                + "quit";
        in = giveCommand(cmd);
    }
    /**
     * This method will turn the network off
     * @param net a required network
     */
    public void turnOffNetwork(Network net) {
        String cmd = "sudo nmcli con down '" + net.getName() + "'";
        //String cmd = "sudo ip link set " + net.getName() + " down";
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(cmd);
        System.out.println("Turn off " + net.getName() + " succesfully");
    }
    /**
     * This method will turn the network on
     * @param net a required network
     */
    public void turnOnNetwork(Network net) {
        String cmd = "sudo nmcli con up '" + net.getName() + "'";
        //String cmd = "sudo ip link set " + net.getName() + " up";
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(cmd);
        System.out.println("Turn on " + net.getName() + " succesfully");
    }
    /**
     * This method will be used to set a timer between each command
     * @param second 
     */
    public void timer(int second) {
        try {
            Thread.sleep(second*1000); // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            // handle the exception if needed
        }
    }
    /**
     * This method will paste the command to giveCommand method and ping the required network
     * @param net a network that will be pinged
     * @param pingTime the number of time to ping
     */
    public void pingNetwork(Network net, int pingTime) {
        String packetLoss = new String();
        String[] latency = new String[4];   
        String pingCmd = "ping -c " + pingTime + " " +  net.getIpAddress();
        ArrayList<String> in = new ArrayList<>();
        
        in = giveCommand(pingCmd);
        String ping = in.get(pingTime-1);
        //loop for extracting the data
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
     * This method will disconnect the Putty
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
        //turnOffNetwork(networkList.get(1));
        //timer(5);
        //turnOnNetwork(networkList.get(0));
        disconnectSSHConnection();
    }
    public static void main(String[] args) {
        Node n1 = new Node("HOST IP ADDRESS", "USERNAME", "PASSWORD");
        n1.init();
        
    }
}

