package simulator;

import java.util.HashMap;
import java.lang.NullPointerException;
import java.sql.*;

public class User {
    public static final double START_MONEY = 5000;
    private String name;
    private double money;
    private HashMap<String, Integer> stocks;

    public static User loadUser(String n) {
	double m;
	HashMap<String, Integer> s = new HashMap<>();
	Connection conn = null;
	Statement statement = null;
	ResultSet rs = null;
	try {
	    conn = DBConnector.getConnection();
	    statement = conn.createStatement();
	    rs = statement.executeQuery("SELECT * FROM Users WHERE name='" + n + "'");
	    if(rs.next()) {
		m = rs.getDouble("money");
		rs = statement.executeQuery("SELECT * FROM Stocks WHERE user='" + n + "'");
		while(rs.next()) {
		    String company = rs.getString("company");
		    int stocks = rs.getInt("number");
		    s.put(company, stocks);
		}
		return new User(n, m, s);
	    } else {
		return null;
	    }
	} catch(SQLException | NullPointerException e) {
	    return null;
	} finally {
	    try { rs.close(); } catch(SQLException | NullPointerException e) { }
	    try { statement.close(); } catch(SQLException | NullPointerException e) { }
	    try { conn.close(); } catch(SQLException | NullPointerException e) { }
	}

	// try {
	//     conn = DBConnector.getConnection();
	//     statement = conn.createStatement();
	//     rs = statement.executeQuery("SELECT * FROM Stocks WHERE user='" + n + "'");
	//     while(rs.next()) {
	// 	String company = rs.getString("company");
	// 	int stocks = rs.getInt("number");
	// 	s.put(company, stocks);
	//     }
	//     return new User(n, m, s);
		
	// } catch(SQLException | NullPointerException e) {
	//     return null;
	// } finally {
	//     try { rs.close(); } catch(SQLException | NullPointerException e) { }
	//     try { statement.close(); } catch(SQLException | NullPointerException e) { }
	//     try { conn.close(); } catch(SQLException | NullPointerException e) { }
	// }
    }
    
    public User(String n, double m, HashMap<String, Integer> s) {
	name = n;
	money = m;
	stocks = s;
    }

    public int getNumberOfStocks(String cname) {
	Integer n = stocks.get(cname);
	return (n == null) ? 0 : (int)n;
    }

    public String getName() {
	return name;
    }

    public double getMoney() {
	return money;
    }

    public boolean purchaseStock(String cname) throws NullPointerException {
	Company comp = Company.getCompany(cname);
	double price = comp.getStockValue();
	if(price <= money && comp.buyStock()) {
	    money -= price;
	    stocks.put(cname, getNumberOfStocks(cname) + 1);
	    if(this.saveData())
		return true;
	    else {
	    	money += price;
	    	stocks.put(cname, getNumberOfStocks(cname) - 1);
	    	comp.sellStock();
	    }
	}
	return false;
    }

    public boolean sellStock(String cname) throws NullPointerException{
	Company comp = Company.getCompany(cname);
	double price = comp.getStockValue();
	if(getNumberOfStocks(cname) > 0 && comp.sellStock()) {
	    money += price;
	    stocks.put(cname, getNumberOfStocks(cname) - 1);
	    if(this.saveData())
		return true;
	    else {
	    	money -= price;
	    	stocks.put(cname, getNumberOfStocks(cname) + 1);
	    	comp.buyStock();
	    }
	}
	return false;
    }

    public double getStockValue() throws NullPointerException {
	double total = 0;
	Company[] companies = Company.getCompanies();
	for(int i=0; i<companies.length; ++i) {
	    Company c = companies[i];
	    String cname = c.getName();
	    double price = c.getStockValue();
	    int numStocks = getNumberOfStocks(cname);
	    total += numStocks*price;
	}
	return total;
    }

    public boolean saveData() {
	Connection conn = null;
	Statement statement = null;
	try {
	    conn = DBConnector.getConnection();
	    statement = conn.createStatement();
	    statement.executeUpdate("UPDATE Users SET "
				    + "money=" + money + " "
				    + "WHERE name='" + name + "'"
				    );
	    Company[] companies = Company.getCompanies();
	    for(Company c : companies) {
		int stocks = getNumberOfStocks(c.getName());
		statement.executeUpdate("UPDATE Stocks SET "
					+ "number=" + stocks + " "
					+ "WHERE user='" + name + "'"
					+ "AND company='" + c.getName() + "'"
					);
	    }
	    return true;
	} catch(SQLException | NullPointerException e) {
	    return false;
	} finally {
	    try { statement.close(); } catch(SQLException | NullPointerException e) { }
	    try { conn.close(); } catch(SQLException | NullPointerException e) { }
	}
    }
}
