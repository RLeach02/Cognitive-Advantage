import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Class: NetJamVer2.java
 * Description: A class that provide a basic user interface for degradation network.
 * @author: Chitipat Marsri
 * @Javadoc Comments: Chitipat Marsri
 * @create: 16 May 2023
 * @LastUpdate: 17 May 2023
 */
public class NetJamVer2 {
    private Scanner scan =new Scanner(System.in);
    private String ipAddr;
    private String user;
    private String password;
    private Node n0;
    /**
    * Method: Constructor 
    * Description: Get information from config.properties to create Node object.
    */
    public NetJamVer2() {
        //read file
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("config.properties"))) {
            properties.load(reader);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        //extract data
        ipAddr = properties.getProperty("ip_addr");
        user = properties.getProperty("username");
        password = properties.getProperty("password");
        n0 = new Node(ipAddr, user, password);
    }
    /**
     * Method: fixDegradation
     * Description: provide a test case for quick degradation
     * @param networkName name of network
     * @param serialNum serial number
     */
    public void fixDegradation(String networkName, int serialNum) {
        String serial = "";
            String profile1 = " tc qdisc add dev " + networkName + " root netem delay 37.5ms loss 5%";
            String profile2 = " tc qdisc add dev " + networkName + " root netem delay 75ms loss 10%"; 
            String profile3 = " tc qdisc add dev " + networkName + " root netem delay 112.5ms loss 15%";
            String profile4 = "tc qdisc add dev " + networkName + " root netem delay 150ms loss 20%";
            String profile5 = "tc qdisc add dev " + networkName + " root netem delay 200ms loss 25%";
        switch (serialNum) {
            case 1 -> serial = profile1;
            case 2 -> serial = profile2;
            case 3 -> serial = profile3;
            case 4 -> serial = profile4;
            case 5 -> serial = profile5;
            default -> System.out.println("Invalid serial number");
        }
        n0.giveSudoCommand(serial);
        System.out.println("degrade successfully");
    }
    /**
     * Method: modDegradation
     * Description: degrade a network's latency and packet loss
     * @param networkName name network 
     * @param delay additional delay
     * @param packetLoss packet loss
     */
    public void modDegradation(String networkName, int delay, int packetLoss) {
        String cmd = "tc qdisc add dev " + networkName + " root netem delay " + delay + "ms loss " + packetLoss + "%"; 
        n0.giveSudoCommand(cmd);
        System.out.println("degrade successfully");
    }
    /**
     * Method: restore
     * Description: restore network to its original state
     * @param network network name
     */
    public void restore(String network) {
        String restoreCommand = "tc qdisc del dev " + network +" root";
        n0.giveSudoCommand(restoreCommand);
        System.out.println("restore successfully");
    }
    /** 
    * Method:init 
    * Description: Run all subsequent methods.
    */ 
    public void init() {
        
        n0.getName_Metric_IP();
        n0.getConnectionName();
            while (true) {
            System.out.println("""
                               **************************************************************
                               * What do you want to do? (1-8 only)                         *
                               * 1: show network                                            *
                               * 2: degrade latency and packet loss of the network          *
                               * 3: choose test case                                        *
                               * 4: restore network                                         *
                               * 5: restore all                                             *
                               * 6: quick ping all (100 times 0.01 interval)                *
                               * 7: mod ping all                                            *
                               * 8: exit                                                    *
                               **************************************************************""");   
            String ans = scan.nextLine();
            if (ans.equals("1")) {
                try {
                    System.out.println("There are " + n0.getNetworkListSize() + " network bearers");
                    n0.printNetworkList();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("2")) {
                try {
                    System.out.println("Enter network");
                    String netName = scan.nextLine();
                    System.out.println("Enter delay");
                    int ans2 = scan.nextInt();
                    System.out.println("Enter packet loss");
                    int ans3 = scan.nextInt();
                    modDegradation(netName, ans2, ans3);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("3")) {
                try {
                    System.out.println("Enter network");
                    String netName = scan.nextLine();
                    System.out.println("Choose your test case (1-6)");
                    int testNum = scan.nextInt();
                    restore(netName);
                    fixDegradation(netName, testNum);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("4")) {
                try {
                    System.out.println("Enter network");
                    String netName = scan.nextLine();
                    restore(netName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("5")) {
                try {
                    for (int i = 0; i < n0.getNetworkListSize(); i++) {
                        restore(n0.getNetworkList().get(i).getName()); 
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("6")) {     
                try{
                    n0.pingAll(100, 0.01);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("7")) {     
                try{
                    System.out.println("Enter your ping time");
                    int ans1 = scan.nextInt();
                    System.out.println("Enter your ping interval");
                    double ans2 = scan.nextDouble();
                    n0.pingAll(ans1, ans2);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("8")) {
                try {
                    n0.disconnectSSHConnection();
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    System.out.println("Disconnect successfully");
                }
            }
            else {
                System.out.println("Invalid Input, please select available option only.");
            }
        }
    }
    /** 
    * Method: main 
    * Description: Runs the init() method for the existing node with all subsequent methods as per the method's comments.
    * @param args
    */ 
    public static void main(String args[]) {
        NetJamVer2 netJam = new NetJamVer2();
        netJam.init();
    }
}