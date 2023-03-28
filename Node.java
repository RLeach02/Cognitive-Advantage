/**
 *
 * @author chitipatmarsri
 */
import java.util.*;
import com.jcraft.jsch.*;
import java.io.*;

public class IT_proj1_connection {
    //Attributes
    private int networkSize = 2;
    private ArrayList<Network> networkList = new ArrayList<>();
    private JSch jsch;
    private Session session;
    private Channel channel;
    
    public IT_proj1_connection(String host, String username, String password) {
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

    public void disconnectSSHConnection() {
        try {
            session.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void init() {
        //String cmd = "Is -la";
        String cmd = "ip route";
        //String cmd = "ping -c 5 100.119.74.240"; //ping cellular
        getName_Metric_IP(cmd);
        disconnectSSHConnection();
        for (int i = 0;i <= networkSize-1; i++) {
            System.out.println(networkList.get(i));
        }
    }
    public static void main(String[] args) {
        IT_proj1_connection test1 = new IT_proj1_connection("100.122.154.164", "pat", "yourpassword");
        test1.init();
        
    }
}
