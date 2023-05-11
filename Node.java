import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;
/**
 * Class: Node.java
 * Description: A class that represent each Node that can connect to different types of network.
 * @author: Chitipat Marsri
 * @Javadoc Comments: Gobi Jegarajasingham & Chitipat Marsri
 * @create: 20 Mar 2023
 * @LastUpdate: 11 May 2023
 */
public class Node {
    //Attributes
    //Attribute Network Size : Number of Networks 
    private int networkSize; 
    //Attribute networkList: Array of available connected networks 
    private ArrayList<Network> networkList = new ArrayList<>();
    //Attribute jsch: Java implementation of SSH 2 that enables connection to an SSH server 
    private JSch jsch;
    //Attribute: session 
    private Session session;
    //Attribute: password 
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
     * @param command string of command1 
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
                //System.out.println(line);
                out.add(line);
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    /**
     * Method: This method will give sudo command1 to the testbed through PuTTY 
     * @param command string of command1 
     * @param sudoPassword password for sudo command1
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
                //System.out.println(line);
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
     * Description: Pastes the command1 to giveCommand and extracts the relevant information such as name, IP address, metric.
     */
    public void getName_Metric_IP() {
        String command1 = "ip route | grep default";
        String command2 = "ip -o -4 addr show | awk '{print $2, $4}' && ip route show | awk '{print $1, $2, $5}'";

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> ip = new ArrayList<>();
        ArrayList<Integer> metric = new ArrayList<>();
        ArrayList<String> in = new ArrayList<>();
        in = giveCommand(command1);   
        //loop for extracting data
        int count = 0;
        int num = 0;
        //loop for extracting data
        while (num <= in.size()-1) {
            String[] data = in.get(num).split(" ");
            for (int i = 0;i < data.length; i++) {
                if (data[i].equals("default")||data[i].equals("\ndefault")){
                    if (data[i+7].equals("metric")) {
                        //create a Network from data and add into networkList
                        name.add(data[i+4]);
                        int met = Integer.parseInt(data[i+8]);
                        metric.add(met);
                    }
                }
            } 
            num++;
        }
        //geting ip address
        in = giveCommand(command2);   
        //loop for extracting data
        for (int i = 0;i < name.size(); i++) {
            String[] data = in.get(i+1).split(" ");
            for (int j = 0;j < name.size(); j++) {
                if (data[0].equals(name.get(j))) {
                    String ipAddr = data[1].split("/")[0];
                    ip.add(ipAddr); 
                    networkList.add(new Network(name.get(j), ipAddr, metric.get(j))); //create Network object
                    count++;
                    break;
                }
            }
        }
        updateNetworkList();
        networkSize = count;
    }     
    /**
     * Method: getConnectionName
     * Description: Pastes the command1 to giveCommand and extracts the connection name.
     */
    public void getConnectionName() {
        String command1 = "nmcli -f Name con show";
        ArrayList<String> conName = new ArrayList<>();
        ArrayList<String> in = giveCommand(command1);
        //loop for extracting data
        for (int i = 1;i < in.size(); i++) {
            //String name = in.get(i).replaceAll("\\s", "");
            // find the index of the last alphabet in the string
            String name = in.get(i);
            
            // remove all white spaces after the last alphabet
            String regex = "(?<=\\w)\\s+$";
            name = name.replaceAll(regex, "");
            if (!name.equals("NAME") && !name.equals("meshnet0")) {
                conName.add(name);
            }
        }
        for (int i = 0;i < networkList.size(); i++) {
            networkList.get(i).setConnectionName(conName.get(i));
        }
    }
    /**
     * Method: updateNetworkList
     * Description: update network list.
     */
    public void updateNetworkList() {
        Collections.sort(networkList, Comparator.comparingInt(Network::getMetric));
    }
    /**
     * Method: updateIpAddress
     * Description: Update IP address for network.
     */
    public void updateIpAddress() {
        String command = "ip -o -4 addr show | awk '{print $2, $4}' && ip route show | awk '{print $1, $2, $5}'";
        ArrayList<String> in = new ArrayList<>();        
        //geting ip address
        in = giveCommand(command);   
        //loop for extracting data
        for (int i = 0;i < networkList.size(); i++) {
            String[] data = in.get(i+1).split(" ");
            for (int j = 0;j < networkList.size(); j++) {
                if (data[0].equals(networkList.get(i).getName())) {
                    String ipAddr = data[1].split("/")[0];   
                    networkList.get(i).setIpAddress(ipAddr);
                    break;
                }
            }
        }
    }
    /**
     * Method: updateMetric
     * Description: Update metric for network.
     */
    public void updateMetric() {
        String command1 = "ip route | grep default";
        ArrayList<String> in = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<Integer> metric = new ArrayList<>();
        //getting metric
        in = giveCommand(command1);   
        //loop for extracting data
        for (int i = 0;i < networkList.size(); i++) {
            String[] data = in.get(i).split(" ");
            for (int j = 0;j < data.length; j++) {
                if (data[j].equals("default")||data[j].equals("\ndefault")) {
                    if (data[j+7].equals("metric")) {
                        name.add(data[j+4]);
                        int met = Integer.parseInt(data[j+8]);
                        metric.add(met);
                        break;
                    }
                }
            }
        }
        for (Network network: networkList) {
            for (int i = 0;i < name.size(); i++) {
                if (network.getName().equals(name.get(i))) {
                    network.setMetric(metric.get(i));
                }
            }
        }
    }
    /**
     * Method: updateNetworkInfo
     * Description: Update information of networks.
     */
    public void updateNetworkInfo() {
        updateIpAddress();
        updateMetric();
        updateNetworkList();
    }
    /**
    * Method: changeMetric
    * Description: Pastes the command1 to giveCommand method and changes the metric of a network 
    * @param connectionName a network that will change the metric
    * @param newMetric new metric number
    */
    public void changeMetric(String connectionName, int newMetric) {
        String cmd1 = "nmcli con edit '" + connectionName + "'";
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
                if (network.getName().equals(connectionName)) {
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
    * @param connectionName a required network name
    */
    public void turnOffNetwork(String connectionName) {
        ArrayList<String> in = new ArrayList<>();
        String command = "nmcli con down '" + connectionName + "'";
        in = giveSudoCommand(command);
        System.out.println("Turn off " + connectionName + " successfully");
    }
    /**
    * Method: turnOnNetwork
    * Description: Turns the Network on 
    * @param connectionName a required network name
    */
    public void turnOnNetwork(String connectionName) {
        ArrayList<String> in = new ArrayList<>();
        String command = "nmcli con up '" + connectionName + "'";
        in = giveSudoCommand(command);
        System.out.println("Turn on " + connectionName + " successfully");
        updateNetworkInfo();
        System.out.println("Update networks' information successfully");
    }
    /**
    * Method: pingNetwork
    * Description: Pastes the command1 to giveCommand method and ping the required network
    * @param net a network that will be pinged
    * @param pingTime the number of time to ping
    * @param pingInterval interval between each ping
    */
    public void pingNetwork(Network net, int pingTime, double pingInterval) {
        String targetIP = "1.1.1.1"; // Cloudflare
        String packetLoss = new String();
        String[] latency = new String[4];
        //String pingCmd = "ping -I " + net.getName() + " -c " + pingTime + " " + targetIP;
        String pingCmd = "ping -I " + net.getName() + " -c " + pingTime + " -i "+ pingInterval + " " + targetIP;
        ArrayList<String> in = new ArrayList<>();
        
        in = giveCommand(pingCmd);
        String ping = in.get(in.size()-2).concat(" ").concat(in.get(in.size()-1));
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
        System.out.println("\n" + net + "\nPacket loss: " + packetLoss
                         + "\nmin latency: " + latency[0]
                         + "\naverage latency: " + latency[1]
                         + "\nmax latency: " + latency[2]
                         + "\nmdev latency: " + latency[3]);

        ArrayList<Double> latencyList = new ArrayList<>(List.of(Double.parseDouble(latency[0]), Double.parseDouble(latency[1]), Double.parseDouble(latency[2]), Double.parseDouble(latency[3])));
        //set packet loss and latency
        net.setLatency(latencyList);
        net.setPacketLoss(Double.parseDouble(packetLoss.substring(0, packetLoss.length()-1)));
    }
    /**
    * Method: pingAll
    * Description: Ping all networks in the list
    * @param pingTime the number of time to ping
    * @param pingInterval interval between each ping
    */
    public void pingAll(int pingTime, double pingInterval) {
        ArrayList<Thread> threadList = new ArrayList<>();
        updateNetworkInfo();
        System.out.println("Update networks' information successfully");
        // create a new thread for each network object
        for (Network network : networkList) {
            Thread thread = new Thread(() -> {
            // ping the network and extract its latency
            pingNetwork(network, pingTime, pingInterval);
            });
            threadList.add(thread);
            thread.start();
        }
        // wait for all threads to finish
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
    * Method: getNetworkList 
    * Description: Returns a list of networks
    * @return a list of network
    */
    public ArrayList<Network> getNetworkList() {
        updateNetworkInfo();
        System.out.println("Update networks' information successfully");
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
    * Description: Method to print the network bearer in the network list.
    */
    public void printNetworkList() {
        updateNetworkInfo();
        System.out.println("Update networks' information successfully");
        for (Network net : networkList) {
            System.out.println(net);
        }
    }
    /**
    * Method: networkSelection
    * Description: Sorts network list using NetworkSelector class.
    */
    public void networkSelection() {
        updateNetworkInfo();
        System.out.println("Update networks' information successfully");
        Network current = networkList.get(0);
        NetworkSelector agent = new NetworkSelector();
        this.networkList = agent.selectBestNetwork(networkList, current); 
        System.out.println("\n");
        System.out.println("Best bearer is " + networkList.get(0).getName() + "\tavg latency: " + networkList.get(0).getLatency().get(1) + " ms\tpacket loss: " + networkList.get(0).getPacketLoss() + " %\tmetric: " + networkList.get(0).getMetric());        
    }
    /**
    * Method: timer 
    * Description: Sets a timer between each command1
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
    * Description: Automatically change network for a period of time.
    * @param times 
    */
    public void monitor(int times) {
        // reset the metric if hit 1000
        int metricReset = 50;
        int maxMetric = 1000;
        int pingTime = 1000;
        double pingInterval = 10.0/pingTime;
        int time = 5;
        //reset metric if metric become large
        if (networkList.get(0).getMetric() > maxMetric) {
            changeMetric(networkList.get(0).getName(), metricReset);
            turnOffNetwork(networkList.get(0).getName());
            turnOnNetwork(networkList.get(0).getName());
            for (int i = 1; i < networkList.size(); i++) {
                changeMetric(networkList.get(i).getConnectionName(), metricReset+i);
                turnOffNetwork(networkList.get(i).getConnectionName());
                turnOnNetwork(networkList.get(i).getConnectionName());
            }
        }
        //iteration
        for (int n = 0; n < times; n++) {
            //ping network
            System.out.println("\nStart pinging");
            pingAll(pingTime, pingInterval);
            //sort network
            networkSelection();
            //change metric
            for (int i = 1; i < networkList.size(); i++) {
                int newMetric =networkList.get(0).getMetric()+i;
                changeMetric(networkList.get(i).getConnectionName(), newMetric);
                turnOffNetwork(networkList.get(i).getConnectionName());
                turnOnNetwork(networkList.get(i).getConnectionName());
            }
            System.out.println("Using " + networkList.get(0));
            timer(time);
        }
    }
    /**
    * Method: fullyAutomation
    * Description: Automatically change network until the user terminate the program.
    */
    public void fullyAutomation() {
        // reset the metric if hit 1000
        int metricReset = 50;
        int maxMetric = 1000;
        int pingTime = 1000;
        double pingInterval = 10.0/pingTime;
        int time = 5;
        //reset metric if metric become large
        if (networkList.get(0).getMetric() > maxMetric) {
            changeMetric(networkList.get(0).getName(), metricReset);
            turnOffNetwork(networkList.get(0).getName());
            turnOnNetwork(networkList.get(0).getName());
            for (int i = 1; i < networkList.size(); i++) {
                changeMetric(networkList.get(i).getConnectionName(), metricReset+i);
                turnOffNetwork(networkList.get(i).getConnectionName());
                turnOnNetwork(networkList.get(i).getConnectionName());
            }
        }
        //iteration
        while (true) {
            //ping network
            System.out.println("\nStart pinging");
            pingAll(pingTime, pingInterval);
            //sort network
            networkSelection();
            //change metric
            for (int i = 1; i < networkList.size(); i++) {
                int newMetric =networkList.get(0).getMetric()+i;
                changeMetric(networkList.get(i).getConnectionName(), newMetric);
                turnOffNetwork(networkList.get(i).getConnectionName());
                turnOnNetwork(networkList.get(i).getConnectionName());
            }
            System.out.println("Using " + networkList.get(0));
            timer(time);
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
        getConnectionName();
        //fullyAutomation();
        //updateMetric();
        //System.out.println("Update metric successful");
        //test pingNetwork()
        /*
        pingAll(100, 0.01);
        */
        /*
        changeMetric("wwan0", 140);
        turnOffNetwork("wwan0");
        turnOnNetwork("wwan0");
        */
        //test monitor()
        //monitor(1);
        //printNetworkList();
        disconnectSSHConnection();
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
