import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class: CognitiveAdvantageDegredationUI.java
 * Description: A class that provide a basic user interface for degradation network.
 * @author: Chitipat Marsri
 * @Javadoc Comments: Chitipat Marsri
 * @create: 01 May 2023
 * @LastUpdate: 11 May 2023
 */
public class CognitiveAdvantageDegradationUI {
    private Scanner scan =new Scanner(System.in);
    private String ipAddr = ;
    private String user = ;
    private String password = ;
    private Node n0 = new Node(ipAddr, user, password);
    /**
     * Method: fixDegradation
     * Description: provide a test case for quick degradation
     * @param networkName name of network
     * @param serialNum serial number
     */
    public void fixDegradation(String networkName, int serialNum) {
        String serial = "";
            String profile0 = " tc qdisc add dev " + networkName + " root netem delay 0ms loss 0%"; 
            String profile1 = " tc qdisc add dev " + networkName + " root netem delay 40ms loss 15%";
            String profile2 = " tc qdisc add dev " + networkName + " root netem delay 80ms loss 30%"; 
            String profile3 = " tc qdisc add dev " + networkName + " root netem delay 120ms loss 45%";
            String profile4 = "tc qdisc add dev " + networkName + " root netem delay 160ms loss 60%";
            String profile5 = "tc qdisc add dev " + networkName + " root netem delay 200ms loss 75%";
        switch (serialNum) {
            case 0 -> serial = profile0;
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
                               * 3: test case for wwan0                                     *
                               * 4: test case for enp10s0                                   *
                               * 5: restore enp10s0                                         *
                               * 6: restore wwan0                                           *
                               * 7: quick ping current network (100 times 0.01 interval)    *
                               * 8: mod ping the current network                            *
                               * 9: quick ping all (100 times 0.01 interval)                *
                               * 10: mod ping all                                           *
                               * 11: exit                                                   *
                               **************************************************************""");   
            String ans = scan.nextLine();
            if (ans.equals("1")) {
                try {
                    System.out.println("There are " + n0.getNetworkListSize() + " network bearers");
                    n0.printNetworkList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("2")) {
                try {
                    System.out.println("Enter network");
                    String ans1 = scan.nextLine();
                    System.out.println("Enter delay");
                    int ans2 = scan.nextInt();
                    System.out.println("Enter packet loss");
                    int ans3 = scan.nextInt();
                    modDegradation(ans1, ans2, ans3);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("3")) {
                try {
                    System.out.println("Choose your test case (0-6)");
                    int testNum = scan.nextInt();
                    fixDegradation("wwan0", testNum);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("4")) {
                try {
                    System.out.println("Choose your test case (0-6)");
                    int testNum = scan.nextInt();
                    fixDegradation("enp10s0", testNum);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("5")) {
                try {
                    restore("enp10s0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("6")) {
                try {
                    restore("wwx0a8a48a35927"); 
                    restore("wwan0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("7")) {
                try {
                    n0.pingNetwork(n0.getNetworkList().get(0), 100, 0.01);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("8")) {
                try {
                    System.out.println("Enter your ping time");
                    int ans1 = scan.nextInt();
                    System.out.println("Enter your ping interval");
                    double ans2 = scan.nextDouble();
                    n0.pingNetwork(n0.getNetworkList().get(0), ans1, ans2);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("9")) {     
                try{
                    n0.pingAll(100, 0.01);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("10")) {     
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
                    e.printStackTrace();
                }
            }
            else if (ans.equals("11")) {
                try {
                    n0.disconnectSSHConnection();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
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
    */ 
    public static void main(String args[]) {
        CognitiveAdvantageDegradationUI dUI = new CognitiveAdvantageDegradationUI();
        dUI.init();
    }
}
