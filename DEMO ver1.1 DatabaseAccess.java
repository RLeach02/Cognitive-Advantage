import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
/**
 * A class for create, drop, populate table and provide access to mysql. 
 * DEMO ONLY
 * @author chitipat marsri
 * @version 27 Mar 2023
 */
public class DatabaseAccess {
    //private Attributes to be used in the class
    private Connection conn;
    private Statement stmt;
    private ResultSet result;
    /**
     * initialize connection and initialise statement as stmt that will be used later.
     */
    public DatabaseAccess() {
        try {
            //establish the driver
            Class.forName("com.mysql.jdbc.Driver");
            //initialise connection 
            conn = DriverManager.getConnection(".......");
            //initialise statement 
            stmt = conn.createStatement();
        } catch (Exception error) {
            System.out.println("Unable to load MySQL driver");
            error.printStackTrace();
        }
    }
    /**
     * create Tables and handle error if necessary.
     */
    public void createTables() throws SQLException{
        //prepared sql about creating Olympics table
        String cOlympics = "CREATE TABLE .... (" + 
                           "...... INTEGER NOT NULL AUTO_INCREMENT, " + 
                           "...... INTEGER, " + 
                           "...... VARCHAR(7), " + 
                           "...... VARCHAR(23), " + 
                           "PRIMARY KEY (ID))";
        //if table exist, inform the user 
        if (isTableExist("....")) {
            System.out.println("...... Table is already exist");
        }
        //else create table
        else {
            stmt.executeUpdate(cOlympics);
            System.out.println("...... table created");
        }
    }
    /**
     * drop tables if the tables exist.
     */
    public void dropTables() {
        String dTable = "DROP TABLE ......";
        try {
            //if the table is not exist, will move to the next one, else drop table 
            if (isTableExist("......")) {
                stmt.executeUpdate(dTable);
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }   
    }
    /**
     * populate tables.
     */
    public void populateTables() {
        try {
            if (!isTableExist("......")) {
                System.out.println("Table is not exist, create first");
            }
            else {
                //USING StringBuilder
                StringBuilder sql = new StringBuilder();
                String infoToInsert;
                //append the sql format
                sql.append("INSERT INTO ......(......) VALUES ");
                BufferedReader br = new BufferedReader(new FileReader(".......csv"));
                //read file until find empty line
                /*
                while ((line = br.readLine()) != null) {
                    //split data
                    String[] info = line.split(",");               
                    year = info[0];
                    season = info[1];
                    cityName = info[2];
                    //append the values
                    infoToInsert = "(" + year + "," + season + "," + cityName + "),";
                    sql.append(infoToInsert);
                }
                */
                //delete comma at the end 
                sql = sql.deleteCharAt(sql.length()-1);
                //change to String
                stmt.executeUpdate(sql.toString());
                //close buffere reader
                br.close();
                System.out.println("...... Populate  succeed");
            }
        } catch (Exception error) {
            error.getMessage();
        }
    }
    /**
     * check if the corresponding table exist or not
     * @param tableName
     * @return boolean true if table exist, false otherwise
     * @throws SQLException 
     */
    public boolean isTableExist(String tableName) throws SQLException {
        //create DatabaseMetaData object
        DatabaseMetaData ddmd = conn.getMetaData();
        ResultSet isExist = ddmd.getTables(null, null, tableName, new String[] {"TABLE"});
        //check if isExist is empty or not
        return isExist.next();
    }
    public static void main(String args[]) {
        
    }
}
