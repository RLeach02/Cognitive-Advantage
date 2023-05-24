import java.util.Scanner;
/**
 * Class: InfiniteCounter.java
 * Description: A class that link to Node object and provide full automation for iteration of selection algorithm until press enter
 * @author: Chitipat Marsri
 * @Javadoc Comments: Chitipat Marsri
 * @create: 18 May 2023
 * @LastUpdate: 24 May 2023
 */
public class InfiniteCounter implements Runnable{
    //variable to stop automation when false
    volatile boolean booleanRun = true;
    //node
    private static Node node;
    /**
    * Method: Constructor of InfiniteCounter.java class
    * Description: Connects to the Node object
    * @param node
    */
    public InfiniteCounter(Node node){
        this.node = node;
    }
    /**
     * Method: run
     * Description: Start Net Jumper until iteration is interrupted.
     */
    @Override
    public void run() {
        int i = 1;
        System.out.println("Start Net Jumper");
        while (booleanRun){
            try {
                System.out.println("Iteration: " + i++);
                node.fullyAutomation();
            } catch(InterruptedException e) {
                System.out.println(e.getMessage());
            } 
        }
        try {
            System.out.println("Net Jumper stop");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            node.disconnectSSHConnection();
            System.out.println("Disconnect successfully");    
            System.out.println("Good bye");
        }
    }
    /** 
    * Method: main 
    * @param args
    */ 
    public static void main(String args[]) {
        /*
        InfiniteCounter infiniteCounter = new InfiniteCounter(node);
        Thread thread = new Thread(infiniteCounter);
        thread.start();
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextLine());
        infiniteCounter.booleanRun = false;
        thread.interrupt();
        */
    }
}
