package simulator;

import java.util.ArrayList;
import java.sql.*;
import java.lang.NullPointerException;

public class Company {
    private String name;
    private double stockValue;
    private int stocksAvailable;
	
    public Company(String n, double sv, int as) {
	name = n;
	stockValue = sv;
	stocksAvailable = as;
    }

    public static Company[] getCompanies() {
	ArrayList<Company> companies = new ArrayList<>();
	try {
	    Connection conn = DBConnector.getConnection();
	    Statement statement = conn.createStatement();
	    ResultSet rs = statement.executeQuery("SELECT * FROM Companies;");
	    while(rs.next()) {
		String n = rs.getString("name");
		double sv = rs.getDouble("stockValue");
		int as = rs.getInt("availableStocks");
		companies.add(new Company(n, sv, as));
	    }
	    return companies.toArray(new Company[companies.size()]);
	} catch(SQLException | NullPointerException e) {
	    return null;
	}
    }
    
    public static Company getCompany(String name) {
	try {
	    Connection conn = DBConnector.getConnection();
	    Statement statement = conn.createStatement();
	    ResultSet rs = statement.executeQuery("SELECT * FROM Companies WHERE name='"
						  + name + "';");
	    if(rs.next()) {
		double sv = rs.getDouble("stockValue");
		int as = rs.getInt("availableStocks");
		return new Company(name, sv, as);
	    }
	} catch(SQLException | NullPointerException e) {
	}
	return null;
    }

    public String getName() {
	return name;
    }

    public double getStockValue() {
	return stockValue;
    }

    public int getNumberOfAvailableStocks() {
	return stocksAvailable;
    }

    public boolean buyStock() {
	if(stocksAvailable > 0) {
	    stocksAvailable--;
	    if(this.saveData())
		return true;
	    else
		stocksAvailable++;
	}
	return false;
    }

    public boolean sellStock() {
	stocksAvailable++;
	if(this.saveData())
	    return true;
	else
	    stocksAvailable--;
	return false;
    }

    public void updatePrice(double v) {
	double oldValue = v;
	stockValue = v;
	if(!this.saveData())
	    stockValue = oldValue;
    }

        public boolean saveData() {
	try {
	    Connection conn = DBConnector.getConnection();
	    Statement statement = conn.createStatement();
	    statement.executeUpdate("UPDATE Companies SET "
				    + "stockValue=" + stockValue + ", "
				    + "availableStocks=" + stocksAvailable + " "
				    + "WHERE name='" + name + "'"
				    );
	    return true;
	} catch(SQLException | NullPointerException e) {
	    return false;
	}
    }
    
}
