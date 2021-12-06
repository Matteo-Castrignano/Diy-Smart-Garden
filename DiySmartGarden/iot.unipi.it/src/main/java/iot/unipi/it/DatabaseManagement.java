package iot.unipi.it;
import java.sql.*;

public class DatabaseManagement {
	
	private static String IP = "localhost";
	private static String Port = "3306";
	private static String Username = "root";
	private static String Password = "root";
	private static String databaseName = "SmartGarden";

    @SuppressWarnings("finally")
	private static Connection makeJDBCConnection() {

        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + Port + "/" + databaseName + "?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET",
            Username,Password);
            if (connection == null)
                System.err.println("Connection to database failed");

        } catch (SQLException e) {
            System.err.println("MySQL Connection Failed!");
            e.printStackTrace();
        }
        finally {
            return connection;
        }
    }


    public static void save(String table, String node, float value)
    {
        String insertQueryStatement = "INSERT INTO " + table + " (nodeId, value) VALUES (?, ?)";

        try (Connection gardenConnection = makeJDBCConnection();
             PreparedStatement gardenPrepareStat = gardenConnection.prepareStatement(insertQueryStatement);
        ) {
            gardenPrepareStat.setString(1, node);
            gardenPrepareStat.setFloat(2, value);

            gardenPrepareStat.executeUpdate();

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public static void read(final String table)
    {
        String getQueryStatement = "SELECT * FROM " + table;

        try (Connection gardenConnection = makeJDBCConnection();
             PreparedStatement gardenPrepareStat = gardenConnection.prepareStatement(getQueryStatement);
        ){
            ResultSet rs = gardenPrepareStat.executeQuery();

            System.out.println("Log " + table);

            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String node = rs.getString("nodeId");
                float value = rs.getInt("value");

                System.out.format("Timestamp:%s -- NodeId:%s -- Value:%.2f\n", timestamp, node, value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static float getMean(String table)
    {
        String getQueryStatement = "SELECT value FROM " + table;
        int n = 0;
        float sum = 0;

        try (Connection gardenConnection = makeJDBCConnection();
             PreparedStatement gardenPrepareStat = gardenConnection.prepareStatement(getQueryStatement);
        ){
            ResultSet rs = gardenPrepareStat.executeQuery(); 

            while (rs.next()) {

                sum += rs.getInt("value");
                n++;
                
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sum/n;
    }   

    public static void saveWaterActuator(String status)
    {
        String insertQueryStatement = "INSERT INTO water_actuator (status) VALUES (?)";

        try (Connection gardenConnection = makeJDBCConnection();
             PreparedStatement gardenPrepareStat = gardenConnection.prepareStatement(insertQueryStatement);
        ) {
            gardenPrepareStat.setString(1, status);

            gardenPrepareStat.executeUpdate();

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

   public static void saveFertilizeActuator(String status)
    {
        String insertQueryStatement = "INSERT INTO fertilize_actuator (status) VALUES (?)";

        try (Connection gardenConnection = makeJDBCConnection();
             PreparedStatement gardenPrepareStat = gardenConnection.prepareStatement(insertQueryStatement);
        ) {
            gardenPrepareStat.setString(1, status);

            gardenPrepareStat.executeUpdate();

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public static void readActuatorLog(String table)
    {
        String getQueryStatement = "SELECT * FROM " + table;

        try (Connection gardenConnection = makeJDBCConnection();
             PreparedStatement gardenPrepareStat = gardenConnection.prepareStatement(getQueryStatement);
        ){
            ResultSet rs = gardenPrepareStat.executeQuery();

            System.out.println("Log " + table);

            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String status = rs.getString("status");

                System.out.format("Timestamp:%s -- Status:%s\n", timestamp, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}