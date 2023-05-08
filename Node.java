import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;
/**
 * A class that represent each Node that can connect to different types of network.
 * @author chitipat marsri
 * @version 1.4 - 30 Mar 2023
 */
public class Node {
    //Attributes
    private int networkSize; //number of network
    private ArrayList<Network> networkList = new ArrayList<>();
    private JSch jsch;
    private Session session;
    private String password;
    /**
     * Constructor of Node.java class that establish connection with Putty
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
     * This method will give sudo command to the Putty
     * @param command string of command 
     * @param sudoPassword password for sudo command
     * @return ArrayList<String> of the result from Putty
     */
    public ArrayList<String> giveSudoCommand(String command, String sudoPassword) {
        ArrayList<String> output = new ArrayList<>();
        try {
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
            channel.setInputStream(null);
            OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            ((ChannelExec) channel).setPty(true);
            // connect channel
            channel.connect();
            // put in pass word
            out.write((sudoPassword + "\n").getBytes());
            out.flush();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    String line = new String(tmp, 0, i);
                    output.add(line);
                    System.out.print(line);
                }
                if (channel.isClosed()) {
                    //System.out.println("Exit status: " + channel.getExitStatus());
                    break;
                }
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    /**
     * This method will paste the command to giveCommand method and extract the relevant information such as name, IP address, metric
     * @param command string of command
     */
    public void getName_Metric_IP(String command) {
                ArrayList<String> name = new ArrayList<>();
        ArrayList<String> ip = new ArrayList<>();
        ArrayList<Integer> metric = new ArrayList<>();
        
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
                    else if (data[j+9].equals("metric")) {
                        //create a Network from data and add into networkList
                        ip.add(data[j+2]);
                        name.add(data[j+4]);
                        int met = Integer.parseInt(data[j+10]);
                        metric.add(met);
                        networkList.add(new Network(name.get(count), ip.get(count), metric.get(count))); //create Network object
                        count++;
                    }
                }
            } 
            num++;
        }
        networkSize = count;
        System.out.println("There are "+ networkSize+ " network");
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
        ArrayList<String> in = new ArrayList<>();
        if (net.getName().contains("wwan")) {
            String command = "nmcli con down '" + net.getName() + "'";
            in = giveSudoCommand(command, password);
        }
        else if(net.getName().contains("enp")) {
            String command = "ip link set " + net.getName() + " down";
            in = giveSudoCommand(command, password);
        }
        System.out.println("Turn off " + net.getName() + " successfully");
    }
    /**
     * This method will turn the network on
     * @param net a required network
     */
    public void turnOnNetwork(Network net) {
        ArrayList<String> in = new ArrayList<>();
        if (net.getName().contains("wwan")) {
            String command = "nmcli con up '" + net.getName() + "'";
            in = giveSudoCommand(command, password);
        }
        else if(net.getName().contains("enp")) {
            String command = "ip link set " + net.getName() + " up";
            in = giveSudoCommand(command, password);
        }
        String command = "ip link set enp10s0 up";
        //in = giveSudoCommand(command, password);
        System.out.println("Turn on " + net.getName() + " successfully");
    }
    /**
     * This method will be used to set a timer between each command
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
        for (Network net : networkList) {
            System.out.println(net);
        }
        //pingNetwork(networkList.get(0), 5);
        turnOffNetwork(networkList.get(1));
        timer(10);
        turnOnNetwork(networkList.get(0));
        disconnectSSHConnection();
    }
    public static void main(String[] args) {
        Node n1 = new Node("Host", "User", "Password");
        n1.init();
    }
}
