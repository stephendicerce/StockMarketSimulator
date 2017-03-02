package simulator;

import java.util.HashMap;
import java.util.ArrayList;
import java.sql.*;
import java.lang.NullPointerException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Users {

    public static User[] getUsers() {
        ArrayList<User> users = new ArrayList<>();
        try (
             Connection conn = DBConnector.getConnection();
             Statement statement = conn.createStatement();
	     Statement statement2 = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM users;");
             ) {
		while(rs.next()) {
		    HashMap<String, Integer> s = new HashMap<>();
		    HashMap<String, Double> ap = new HashMap<>();
		    String n = rs.getString("name");
		    double money = rs.getDouble("money");
		    ResultSet rs2 = statement2.executeQuery("SELECT * FROM stocks WHERE user='" + n + "'");
		    while (rs2.next()) {
			String company = rs2.getString("company");
			int stocks = rs2.getInt("number");
			double aPrice = rs2.getDouble("averagePrice");
			s.put(company, stocks);
			ap.put(company, aPrice);
		    }
		    users.add(new User(n, money, s, ap));
		}
		return users.toArray(new User[users.size()]);
	    } catch(SQLException | NullPointerException e) {
	    return null;
	}
    }
}