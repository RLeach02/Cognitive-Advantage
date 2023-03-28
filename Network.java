import java.util.Objects;

/**
 *
 * @author chitipatmarsri
 */
public class Network {
    //Attributes
    //network name
    private String name;
    //network IP address
    private String ipAddress;
    //network metric
    private Integer metric;
    
    public Network(String name, String ipAddress, Integer metric) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.metric = metric;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getMetric() {
        return metric;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setMetric(Integer metric) {
        this.metric = metric;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Network other = (Network) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.ipAddress, other.ipAddress)) {
            return false;
        }
        if (!Objects.equals(this.metric, other.metric)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Network{" + "name=" + name + ", ipAddress=" + ipAddress + ", metric=" + metric + '}';
    }
    
    public static void main(String[] args) {
        Network n1 = new Network("wwan0", "1000000", 100);
        Network n2 = new Network("wwan0", "1000000", 100);
        System.out.println(n1);
        System.out.println(n1.equals(n2));
    }
}
