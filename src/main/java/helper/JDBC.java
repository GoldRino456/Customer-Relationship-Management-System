package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class JDBC {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props = new Properties();
    public static Connection connection;

    static
    {
        try(InputStream input = JDBC.class.getClassLoader().getResourceAsStream(CONFIG_FILE))
        {
            if(input == null)
            {
                throw new RuntimeException("config.properties not found in classpath. See config.properties.example.");
            }

            props.load(input);
        }
        catch(IOException e)
        {
            throw new RuntimeException("Failed to load database configuration.", e);
        }
    }

    public static void openConnection()
    {
        try {
            Class.forName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver")); //Locate Driver
            connection = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            ); // Reference Connection object

            System.out.println("Connection successful!");
        }
        catch (Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch (Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }
}
