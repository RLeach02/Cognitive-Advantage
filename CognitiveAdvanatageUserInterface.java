import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class: CognitiveAdvantageUserInterface.java
 * Description: A class that provide a basic user interface of node and network
 * @author: Chitipat Marsri
 * @Javadoc Comments: Gobi Jegarajasingham & Chitipat Marsri
 * @create: 30 Mar 2023
 * @LastUpdate: 10 May 2023
 */
public class CognitiveAdvantageUserInterface {
    //scan to recieve input from user
    private Scanner scan =new Scanner(System.in);
    /** 
    * Method:init 
    * Description: Run all subsequent methods.
    */ 
    public void init() {
        String ipAddr = ;
        String user = ;
        String password = ;
        Node n0 = new Node(ipAddr, user, password);
        System.out.println("Welcome to Cognitive Advantage");
        n0.getName_Metric_IP();
        n0.getConnectionName();
        while (true) {
            System.out.println("""
                               **************************************************************
                               * What do you want to do? (1-8 only)                         *
                               * 1: Show the network bearer list                            *
                               * 2: Turn off wwan0                                          *
                               * 3: Turn off enp10s0                                        *
                               * 4: Turn on wwan0                                           *
                               * 5: Turn on enp10s0                                         *
                               * 6: change metric wwan0                                     *
                               * 7: change metric enp10s0                                   *
                               * 8: quick ping the current network (100 times 0.01 interval)*
                               * 9: mod ping the current network                            *
                               * 10: quick ping all (100 times 0.01 interval)               *
                               * 11: mod ping all                                           *
                               * 12: automatic change every 30 seconds                      *
                               * 13: sort network bearers                                   *
                               * 14: exit                                                   *
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
                    n0.turnOffNetwork("wwan0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("3")) {
                try {
                    n0.turnOffNetwork("enp10s0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("4")) {
                try {
                    n0.turnOnNetwork("wwan0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("5")) {
                try {
                    n0.turnOnNetwork("enp10s0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("6")) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("7")) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("8")) {
                try {
                    n0.pingNetwork(n0.getNetworkList().get(0), 100, 0.01);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("9")) {     
                try{
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
            else if (ans.equals("10")) {     
                try{
                    n0.pingAll(100, 0.01);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("11")) {     
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
            else if (ans.equals("12")) {
                try{
                    System.out.println("Enter the number of 30 seconds intervals you want to run");
                    int ans1 = scan.nextInt();
                    n0.monitor(ans1);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }       
            }
            else if (ans.equals("13")){
                try {
                    n0.pingAll(100, 0.01);
                    n0.networkSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (ans.equals("14")){
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
    public static void main(String[] args) {
        CognitiveAdvantageUserInterface ui = new CognitiveAdvantageUserInterface();
        ui.init();
    }
}
