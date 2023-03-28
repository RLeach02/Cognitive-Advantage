/**
 *
 * @author chitipatmarsri
 */
import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;

public class Node {
//Attributes
    private int networkSize = 2;
    private ArrayList<Network> networkList = new ArrayList<>();
    private JSch jsch;
    private Session session;
    private Channel channel;
    
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
    public void getName_Metric_IP(String command) {
        String[] line = new String[networkSize];
        String[] name = new String[networkSize];
        String[] ip = new String[networkSize];
        Integer[] metric = new Integer[networkSize];
        try {
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
           
            byte[] buffer = new byte[1024];
            for (int i = 0;i <= networkSize-1; i++) {
                if (in.read(buffer) != -1) {
                    line[i] = new String(buffer);
                }
            }  
            for (int i = 0;i <= networkSize-1; i++) {     
                String[] data = line[i].split(" ");
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
                networkList.add(new Network(name[i], ip[i], metric[i]));
            }
            
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }     
    
    public void pingNetwork(Network net, int pingTime) {
        String packetLoss = new String();
        String[] latency = new String[4];
        String ping = new String();
        
        String pingCmd = "ping -c " + pingTime + " " +  net.getIpAddress();
        try {
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(pingCmd);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            
            byte[] buffer = new byte[1024];
            
            while (in.read(buffer) != -1) {
                ping = new String(buffer);
                System.out.println(ping);
            }
            System.out.println(ping);
            
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
            System.out.println("Packet loss: " + packetLoss
                             + "\nmin latency: " + latency[0]
                             + "\naverage latency: " + latency[1]
                             + "\nmax latency: " + latency[2]
                             + "\nmdev latency: " + latency[3]);
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
    
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
        pingNetwork(networkList.get(0), 5);
        disconnectSSHConnection();
        for (int i = 0;i <= networkSize-1; i++) {
            System.out.println(networkList.get(i));
        }
    }
    public static void main(String[] args) {
        Node n1 = new Node("100.122.154.164", "your usename", "your password");
        n1.init();
        
    }
}
