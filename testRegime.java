import java.util.*; 
import com.jcraft.jsch.*; 
import java.io.*; 

// Change to java file to demonstrate push / pull request 

/** 
* C3 
* Class 3: testRegime.java 
* Description: A consolidated test regime for the active part of the Dynamic Network Selection program
* Features: 22 Serials to adequately demonstratic dynamic switching between network interfaces 
* Enables: Java program to parse through serials in the format of 'sudo tc' linux commands 
* Demonstration: Run even more consolidated serials to display network interface swithcing faster
* 
*/ 

public class testRegime {
  
  /** 
  * A1
  * Attribute 1: bw 
  * Description: Bandwidth is instantiated as a variable to enable the alteration of serial conditions 
  * Reason: This will enable the developer / experienced operator to ascertain whether the software application
  * will switch based on very specific parameters 
  */ 
  
  private bw; 
  
  /** 
  * A2
  * Attribute 2: maxBurst
  * Description: Max Burst Size is instantiated as a variable to enable the alteration of serial conditions 
  * Reason: This will enable the developer / experienced operator to ascertain whether the software application
  * will switch based on very specific parameters 
  */ 
  
  private maxBurst; 
  
  /** 
  * A3
  * Attribute 3: latencyDegradation
  * Description: Max Burst Size is instantiated as a variable to enable the alteration of serial conditions 
  * Reason: This will enable the developer / experienced operator to ascertain whether the software application
  * will switch based on very specific parameters.
  * The paramters of max burst to test % degradation 
  */ 
  
  /** 
  * Extract from Node.java 
  * Instantiates an SSH connection to the testbed in the same method as Node.java 
  */ 
  
  private latencyDegradation; 
  
  /** 
  * A4
  * Attribute 4: jsch 
  * Description: Java implementation of SSH 2 that enables connection to an SSH server 
  */ 
  
  private Jsch jsch; 
  
  /**
  * A5 
  * Attribute 5: session 
  * Description: Initialises a session with the testbed through the Java implementation of SSH
  */ 
  
  private Session session; 
  
  /**
  * A5 
  * Attribute 5: password 
  * Description: Provides the program the password credential to access the testbed 
  */ 
  
  private String password; 
  
  /** 
  * M1 
  * Method 1: Constructor of testRefime that establishes connection with PuTTY 
  * Description: Connects the java program to the Limux Ubuntu testbed server 
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
    
     /**
     * M2
     * Method 2: This method will parse required commands through PuTTY
     * Description: Gives the testbed commands to extract the network names, set metrics, packet loss and latency 
     * @param command string of command 
     * @return ArrayList<String> of the result from Putty
     */
     
    public ArrayList<String> giveCommand(String command) {
        ArrayList<String> out = new ArrayList<>();
        try {
        
            // Create a Channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            
            // Connect channel
            InputStream in = channel.getInputStream();
            channel.connect();
            
            // Read the Output 
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                String line = new String(buffer);
                out.add(line); 
            //add output into ArrayList
            //System.out.println(line + "\n");
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    
    /**
     * M3
     * Method 3: giveSudoCommand
     * Description: Parses sudo commands to the testbed through PuTTY 
     * @param command string of command 
     * @param sudoPassword password for sudo command
     * @return ArrayList<String> of the result from Putty
     */
     
    public ArrayList<String> giveSudoCommand(String command, String sudoPassword) {
        ArrayList<String> output = new ArrayList<>();
        try {
        
            // Create a channel
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
            /**
            * Chitipat Marsri: Please explain the process of Java parsing linux commands 
            * Explain how it is parsed, and how the output is read / collected 
            */ 
            channel.setInputStream(null);
            OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            ((ChannelExec) channel).setPty(true);
            
            // Connects the channel
            channel.connect();
            
            // Puts in the Password
            out.write((sudoPassword + "\n").getBytes());
            out.flush();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    String line = new String(tmp, 0, i);
                    output.add(line);
                    //System.out.print(line);
                }
                if (channel.isClosed()) {
                    //System.out.println("Exit status: " + channel.getExitStatus());
                    break;
                }
            }
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    
  /**
  * M4
  * Method 4: degradeBandwidth 
  * Description: Test 1: Bandwidth Degradation @ 100% & 50% Static performance 
  * Case 1: enp11s0 - Ethernet - Starlink Satellite 
  * Case 2: wwan0 - Wireless 5G Modem - Telstra 5G 
  * Serial 1 - 4 
  */ 
  
  public String degradeBandwidth { 
    //Test 1: Bandiwdth Degradation 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite 
        // Serial 1: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit
        // Serial 2: sudo tc qdisc add dev enp11s0 root tbf rate  6mbit
    
      // Case 2: wwan0 - Wireless 5G Modem - Telstra 5G 
        // Serial 3: sudo tc qdisc add dev wwan0 root tbf rate 12mbit
        // Serial 4: sudo tc qdisc add dev wwan0 root tbf rate  6mbit
        
  }
    
  /** 
  * M5
  * Method 5: degradeBurst
  * Description: Test 2 & 5 : Max Burst Size Degradation @ 100% & 50% Static Bandwidth 
  * Case 1: enp11s0 - Ethernet - Starlink Satellite 
  * Case 2: wwan0 - Wireless 5G Modem - Telstra 5G 
  * Serial 5 - 8 
  */ 
    
  public String degradeBurst { 
    // Test 3: Burst Size Degradation @ 100% Static Bandwidth 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite 
        // Serial 5: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 12kbit 
        // Serial 6: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 6kbit
      // Case 2: wwan0 - Wireless 5G Modem - Telstra 5G 
        // Serial 7: sudo tc qdisc add dev wwan0 root tbf rate 12mbit burst 12kbit 
        // Serial 8: sudo tc qdisc add dev wwan0 root tbf rate 12mbit burst 6kbit 
    
    // Test 5: Burst Size Degradation @ 50% Static Bandwidth 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite - 50% max burst size 
        // Serial 9: sudo tc qdisc add dev enp11s0 root tbf rate 6mbit burst 3kbit
      //Case 2: wwan0 - Wireless 5G Modem - Telstra 5G - 50% max burst size 
        // Serial 10: sudo tc qdisc add dev wwan0 root tbf rate 6mbit burst 3kbit 
    
  } 
  
  /** 
  * M6 
  * Method 6: degradeLatency 
  * Description: Test 10, 12, 18, 20
  * Test Conditions: 100% & 50% Static Bandwidth, 100% & 50% Max Burst Size
  * Latency Contitions: 50%, 125% of Accepted Latency 
  */ 
  
  public String degradeLatency { 
    // Test 10: Latency Degradation @ 100% Static Bandwidth and 100% Max Burst Size 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite - 100% Bandwidth, Max Burst Size
        // Serial 11: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 12kbit latency 15ms
          // 50% Accepted Latency 
        // Serial 12: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 12kbit latency 37.5ms
          // 125% Accepted Latency 
    
      // Case 2: wwan0 - Wireless 5G Modem - Telstra 5G - 100% Bandwidth, Max Burst Size 
        // Serial 13: sudo tc qdisc add dev wwan0 root tbf rate 12mbit burst 12kbit latency 15ms 
          // 50% Accepted Latency 
        // Serial 14: sudo tc qdisc add dev wwan0 root tbf rate 12mbit burst 12kbit latency 37.5ms
          // 125% Accepted Latency 
    
    // Test 12: Latency Degradation @ 100% Static Bandwidth and 50% Max Burst Size 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite - 100% Bandwidth, 50% Max Burst Size 
        // Serial 15: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 6kbit latency 15ms
          // 50% Accepted Latency 
        // Serial 16: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 6kbit latency 37.5ms
          // 125% Accepted Latency 
    
      // Case 2: wwan0 - Wireless 5G Modem - Telstra 5G - 100% Static Bandwidth and 50% Max Burst Size 
        // Serial 17: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 6kbit latency 15ms
          // 50% Accepted Latency 
        // Serial 18: sudo tc qdisc add dev enp11s0 root tbf rate 12mbit burst 6kbit latency 37.5ms
          // 125% Accepted Latency
    
    // Test 18: Latency Degradation @ 50% Static Bandwidth and 100% Max Burst Size 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite - 50% Static Bandwidth and 100% Max Burst Size 
        // Serial 19: sudo tc qdisc add dev enp11s0 root tbf rate 6mbit burst 6kbit latency 15ms 
          // 50% Accepted Latency 
        // Serial 20: sudo tc qdisc add dev enp11s0 root tbf rate 6mbit burst 6kbit latency 37.5ms 
          // 125% Accepted Latency 
    
      // Case 2: wwan0 - Wireless 5G Modem - Telstra 5G - 50% Static Bandwidth and 100% Max Burst Size 
        // Serial 21: sudo tc qdisc add dev wwan0 root tbf rate 6mbit burst 6kbit latency 15ms 
          // 50% Accepted Latency 
        // Serial 22: sudo tc qdisc add dev wwan0 root tbf rate 6mbit burst 6kbit latency 37.5ms 
          // 125% Accepted Latency 
      
    // Test 20: Latency Degradation @ 50% Static Bandwidth and 50% Max Burst Size 
      // Case 1: enp11s0 - Ethernet - Starlink Satellite - 50% Static Bandwidth and 50% Max Burst Size 
        // Serial 23: sudo tc qdisc add dev enp11s0 root tbf rate 6mbit burst 3kbit latency 15ms 
          // 50% Accepted Latency 
        // Serial 24: sudo tc qdisc add dev enp11s0 root tbf rate 6mbit bust 3kbit latency 37.5ms 
          // 125% Accepted Latency 
    
      // Case 2: wwan0 - Wireless 5G Modem - Telstra 5G - 50% Static Bandwidth and 50% Max Burst Size 
        // Serial 25: sudo tc qdisc add dev wwan0 root tbf rate 6mbit burst 3kbit latency 15ms 
          // 50% Accepted Latency 
        // Serial 26: sudo tc qdisc add dev wwan0 root tbf rate 6mbit burst 3kbit latency 37.5ms 
          // 125% Accepted Latency 
        
  }  
  
  

  } 
} 
