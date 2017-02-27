package simulator;

import java.sql.*;

public class DBConnector {

    public static Connection getConnection() {
	Connection conn;
    	try {  
	    Class.forName("com.mysql.jdbc.Driver");  
	    conn = DriverManager.getConnection(  
	       "jdbc:mysql://localhost:3306/test","testUser","testPass"
	    );
	} catch(SQLException | ClassNotFoundException e) {
	    return null;
	}
	return conn;
    }
}