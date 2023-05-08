import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class: CognitiveAdvantageUserInterface.java
 * Description: A class that provide a basic user interface of node and network
 * @author: Chitipat Marsri
 * @Javadoc Comments: Gobi Jegarajasingham
 * @create: 30 Mar 2023
 * @LastUpdate: 05 May 2023
 */
public class CognitiveAdvantageUserInterface {
    //scan to recieve input from user
    Scanner scan =new Scanner(System.in);
    /** 
    * Method:init 
    * Description: Run all subsequent methods
    */ 
    public void init() {
        System.out.println("Welcome to Cognitive Advantage");
        String ipAddr = "100.122.154.164";
        String user = "pat";
        String password = "Thunderpat123";
        Node n0 = new Node(ipAddr, user, password);
        n0.getName_Metric_IP();
        while (true) {
            System.out.println("""
                               
                               What do you want to do? (1-8 only)
                               1: Show the network bearer list
                               2.1: Turn off wwan0
                               2.2: Turn off enp10s0
                               3.1: Turn on wwan0
                               3.2: Turn on enp10s0
                               4.1: change metric wwan0
                               4.2: change metric enp10s0
                               5.1: quick ping the current network 
                               5.2: mod ping the current network 
                               6: exit 
                               7: sort network bearers
                               8: automatic change every 30 seconds""");
            String ans = scan.nextLine();
            if (ans.equals("1")) {
                System.out.println("There are " + n0.getNetworkListSize() + " network bearers");
                n0.printNetworkList();
            }
            else if (ans.equals("2.1")) {
                n0.turnOffNetwork("wwan0");
            }
            else if (ans.equals("2.2")) {
                //n0.turnOffNetwork("enp10s0");
                n0.turnOffNetwork("enp10s0");
            }
            else if (ans.equals("3.1")) {
                n0.turnOnNetwork("wwan0");
            }
            else if (ans.equals("3.2")) {
                //n0.turnOnNetwork("enp10s0");
                n0.turnOnNetwork("enp10s0");
            }
            else if (ans.equals("4.1")) {
                try{
                    System.out.println("Enter your new metric");
                    int ans1 = scan.nextInt();
                    n0.changeMetric("wwan0", ans1);
                    n0.turnOffNetwork("wwan0");
                    n0.turnOnNetwork("wwan0");
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                }
            }
            else if (ans.equals("4.2")) {
                try{
                    System.out.println("Enter your new metric");
                    int ans1 = scan.nextInt();
                    n0.changeMetric("enp10s0", ans1);
                    n0.turnOffNetwork("enp10s0");
                    n0.turnOnNetwork("enp10s0");
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                }
            }
            else if (ans.equals("5.1")) {
                n0.pingNetwork(n0.getNetworkList().get(0), 5);
            }
            else if (ans.equals("5.2")) {     
                try{
                    System.out.println("Enter your ping time");
                    int ans1 = scan.nextInt();
                    n0.pingNetwork(n0.getNetworkList().get(0), ans1);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                }
            }
            else if (ans.equals("6")) {
                n0.disconnectSSHConnection();
                break;
            }
            else if (ans.equals("7")){
                n0.pingNetwork(n0.getNetworkList().get(0), 5);
                n0.sortNetworks();
            }
            else if (ans.equals("8")){
                try{
                    System.out.println("Enter the number of 30 seconds intervals you want to run");
                    int ans1 = scan.nextInt();
                    n0.monitor(ans1);
                    scan.nextLine();
                    break;
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                }
            }
            else {
                System.out.println("Invalid Input, please select available option only.");
            }
        }
    }
    /** 
    * Method: main 
    * Description: Runs the init() method for the existing node with all subsequent methods as per the method's comments 
    */ 
    public static void main(String[] args) {
        CognitiveAdvantageUserInterface ui = new CognitiveAdvantageUserInterface();
        ui.init();
    }
}
