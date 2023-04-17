package example;

import java.util.List;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

/** 
* C3 
* Class 3: SynchronousQuery 
* Description: Issues a synchronous query, it blocks and waits until it receives a response from the server before moving on to the next instruction.
*/ 

public class SynchronousQuery {

    /** 
    * A1 
    * Attribute 1: token 
    * Description: Converts the token to an array of characters 
    */ 

    private static char[] token = "my-token".toCharArray();
    
    /** 
    * A2
    * Attribute 2: org 
    * Description: Parses the name of the organisation as a variable 
    */ 
    
    private static String org = "my-org";


    /** 
    * M1 
    * Method 1: main 
    * Description: Instantiates the InfluxDB Client 
    */ 
    
    public static void main(final String[] args) {

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org);

        String flux = "from(bucket:\"my-bucket\") |> range(start: 0)";

        QueryApi queryApi = influxDBClient.getQueryApi();

        /**
        * Query data
        */ 
        
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
            }
        }

        influxDBClient.close();
    }
}

/**
* M2 
* Method 2: buildConnection 
* Description: Build the Java connection the InfluxDB TSDB using the url, token, bucket and org credentials
* @param url  
* @param token 
* @param bucket 
* @param org 
*/ 

public InfluxDBClient buildConnection(String url, String token, String bucket, String org) {

  

setToken(token);

  

setBucket(bucket);

  

setOrg(org);

  

setUrl(url);

  

return InfluxDBClientFactory.create(getUrl(), getToken().toCharArray(), getOrg(), getBucket());

  

}
