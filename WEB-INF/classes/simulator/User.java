package simulator;

import java.util.HashMap;
import java.lang.NullPointerException;
import java.sql.*;

public class User {
    public static final double START_MONEY = 5000;
    private String name;
    private double money;
    private HashMap<String, Integer> stocks;
    private HashMap<String, Double> averagePrices;

    public static User loadUser(String n) {
	double m, p;
	HashMap<String, Integer> s = new HashMap<>();
	HashMap<String, Double> ap = new HashMap<>();
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ) {
	    ResultSet rs = statement.executeQuery("SELECT * FROM Users WHERE name='" + n + "'");
	    if(rs.next()) {
		m = rs.getDouble("money");
		rs = statement.executeQuery("SELECT * FROM Stocks WHERE user='" + n + "'");
		while(rs.next()) {
		    String company = rs.getString("company");
		    int stocks = rs.getInt("number");
		    double aPrice = rs.getDouble("averagePrice");
		    s.put(company, stocks);
		    ap.put(company, aPrice);
		}
		return new User(n, m, s, ap);
	    } else {
		return null;
	    }
	    } catch(SQLException | NullPointerException e) {
	    return null;
	}
    }
    
    public User(String n, double m, HashMap<String, Integer> s, HashMap<String, Double> a) {
	name = n;
	money = m;
	stocks = s;
	averagePrices = a;
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
	double newAverage, avPrice;
	int numberOfStocks = getNumberOfStocks(cname);
	if(price <= money && comp.buyStock()) {
	    money -= price;
	    avPrice = averagePrices.get(cname);
            newAverage = (((avPrice * numberOfStocks) + price) / (numberOfStocks + 1));
            averagePrices.put(cname, newAverage);
	    stocks.put(cname, getNumberOfStocks(cname) + 1);
            
	    if(this.saveData())
		return true;
	    else {
		averagePrices.put(cname, avPrice);
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
	    if (getNumberOfStocks(cname) == 0) {
		averagePrices.put(cname, 0.0);
	    }
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
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ) {
	    statement.executeUpdate("UPDATE Users SET "
				    + "money=" + money + " "
				    + "WHERE name='" + name + "'"
				    );
	    Company[] companies = Company.getCompanies();
	    for(Company c : companies) {
		int stocks = getNumberOfStocks(c.getName());
		double averagePrice = averagePrices.get(c.getName());
		statement.executeUpdate("UPDATE Stocks SET "
					+ "number=" + stocks + ", "
					+ "averagePrice=" + averagePrice + " "
					+ "WHERE user='" + name + "'"
					+ "AND company='" + c.getName() + "'"
					);
	    }
	    return true;
	} catch(SQLException | NullPointerException e) {
	    return false;
	}
    }

    public double getAveragePriceBoughtAt(String cname) {
	return averagePrices.get(cname);
    }
}
    