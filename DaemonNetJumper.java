import java.util.Scanner;

/**
 * Class: DaemonNetJumper.java
 * Description: A class that provide a daemon for NetJumper
 * @author: Chitipat Marsri
 * @Javadoc Comments: Chitipat Marsri
 * @create: 22 May 2023
 * @LastUpdate: 24 May 2023
 */
public class DaemonNetJumper {
    public static void main(String args[]) {
        String ipAddr = "100.122.154.164";
        String user = "pat";
        String password = "Thunderpat123";
        Node node = new Node(ipAddr, user, password);
        Thread thread1 = new Thread(node);
        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
