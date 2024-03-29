import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Class: NetJumperVer2.java
 Description: A class that provide a basic user interface of node and network
 * @author: Chitipat Marsri
 * @Javadoc Comments: Gobi Jegarajasingham & Chitipat Marsri
 * @create: 16 May 2023
 * @LastUpdate: 25 May 2023
 */
public class NetJumperVer2 {
    //scan to recieve input from user
    private Scanner scan =new Scanner(System.in);
    /** 
    * Method:init 
    * Description: Run all subsequent methods.
    */ 
    public void init() {
        //read file
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("config.properties"))) {
            properties.load(reader);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        //extract data
        String ipAddr = properties.getProperty("ip_addr");
        String user = properties.getProperty("username");
        String password = properties.getProperty("password");
        Node n0 = new Node(ipAddr, user, password);
        
        System.out.println("Welcome to Cognitive Advantage");
        n0.extractFile();
        n0.getName_Metric_IP();
        n0.getConnectionName();
        while (true) {
            System.out.println("""
                               **************************************************************
                               * What do you want to do? (1-8 only)                         *
                               * 1: Show the network bearer list                            *
                               * 2: Turn off network                                        *
                               * 3: Turn on network                                         *
                               * 4: change metric network                                   *
                               * 5: quick ping all (100 times 0.01 interval)                *
                               * 6: mod ping all                                            *
                               * 7: sort network bearers                                    *
                               * 8: iterate the selection program every 30 seconds          *
                               * 9: fully automate Net Jumper (until press Enter)           *
                               * 10: exit                                                   *
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
                    System.out.println("Enter connection name");
                    String conName = scan.nextLine();
                    n0.turnOffNetwork(conName);
                }catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("3")) {
                try {
                    System.out.println("Enter connection name");
                    String conName = scan.nextLine();
                    n0.turnOnNetwork(conName);
                    n0.updateNetworkInfo();
                }catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("4")) {
                try{
                    System.out.println("Enter connection name");
                    String conName = scan.nextLine();
                    System.out.println("Enter your new metric");
                    int ans1 = scan.nextInt();
                    n0.changeMetric(conName, ans1);
                    n0.turnOffNetwork(conName);
                    n0.turnOnNetwork(conName);
                    n0.updateNetworkInfo();
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("5")) {     
                try{
                    n0.pingAll(100, 0.01);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("6")) {     
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
            else if (ans.equals("7")){
                try {
                    n0.pingAll(100, 0.01);
                    n0.networkSelection();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (ans.equals("8")) {
                try{
                    System.out.println("Enter the number of 30 seconds intervals you want to run");
                    int ans1 = scan.nextInt();
                    n0.monitor(ans1);
                    scan.nextLine();
                } catch (InputMismatchException wrongType) {
                    System.out.println("Invalid input");
                    scan.nextLine();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }       
            }
            else if (ans.equals("9")){
                System.out.println("Press enter to stop, please wait a few seconds after pressing Enter for Net Jumper to finish last iteration");
                InfiniteCounter infiniteCounter = new InfiniteCounter(n0);
                Thread thread = new Thread(infiniteCounter);
                thread.start();
                Scanner scanner = new Scanner(System.in);
                while (!scanner.hasNextLine());
                infiniteCounter.booleanRun = false;
                thread.interrupt();
                System.out.println("Please wait a few seconds for Net Jumper to finish last iteration");
                break;
            }
            else if (ans.equals("10")){
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
    public static void main(String[] args) {
        NetJumperVer2 testUI = new NetJumperVer2();
        testUI.init();
    }
}