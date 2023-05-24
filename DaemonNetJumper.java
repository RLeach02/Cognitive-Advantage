import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
/**
 * Class: DaemonNetJumper.java
 * Description: A class that provide a daemon for NetJumper
 * @author: Chitipat Marsri
 * @Javadoc Comments: Chitipat Marsri
 * @create: 22 May 2023
 * @LastUpdate: 24 May 2023
 */
public class DaemonNetJumper {
    /** 
    * Method: main 
    * Description: Runs NetJumper
    * @param args
    */ 
    public static void main(String args[]) {
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
        
        Node node = new Node(ipAddr, user, password);
        //run NetJumper
        Thread thread1 = new Thread(node);
        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            node.disconnectSSHConnection();
        }
    }
}
