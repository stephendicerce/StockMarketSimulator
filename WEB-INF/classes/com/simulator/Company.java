package com.simulator;

import java.util.ArrayList;
import java.sql.*;
import java.lang.NullPointerException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Company {
    private static final int DEFAULT_NUMBER_OF_STOCKS = 100;
    private static final int DEFAULT_STOCK_VALUE = 50;

    private String name;
    private String symbol;
    private double stockValue;
    private int stocksAvailable;
	
    public Company(String n, String sym, double sv, int as) {
	name = n;
	symbol = sym;
	stockValue = sv;
	stocksAvailable = as;
    }

    // Pull from the google stock API to update prices
    public static void updatePrices() {
        Company[] companies = Company.getCompanies();
	String[] symbols = new String[companies.length];
	for(int i=0; i<companies.length; ++i) {
	    symbols[i] = companies[i].getSymbol();
	}
	StockReader.updateStocks(symbols);
    }

    public static boolean addCompany(String name, String symbol) {
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ) {
		statement.executeUpdate("INSERT INTO Companies Values('"
					+ name + "', '"
					+ symbol + "', "
					+ Company.DEFAULT_STOCK_VALUE + ", "
					+ Company.DEFAULT_NUMBER_OF_STOCKS + ");"
					);
	    } catch(SQLException e) {
	    return false;
	}
	return true;
    }
    
    public static Company[] getCompanies() {
	ArrayList<Company> companies = null;
        try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ResultSet rs = statement.executeQuery("SELECT * FROM Companies;");
	     ) {
	    while(rs.next()) {
		if(companies == null) companies = new ArrayList<>();
		String n = rs.getString("name");
		String sym = rs.getString("symbol");
		double sv = rs.getDouble("stockValue");
		int as = rs.getInt("availableStocks");
		companies.add(new Company(n, sym, sv, as));
	    }
	    return companies.toArray(new Company[companies.size()]);
	} catch(SQLException e) {
	    return null;
	}
    }
    
    public static Company getCompany(String name) {
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ResultSet rs = statement.executeQuery("SELECT * FROM Companies WHERE name='"+ name + "';");
	     ) {
	    if(rs.next()) {
		name = rs.getString("name");
		String sym = rs.getString("symbol");
		double sv = rs.getDouble("stockValue");
		int as = rs.getInt("availableStocks");
		return new Company(name, sym, sv, as);
	    } else 
		return null;
	} catch(SQLException e) {
	    return null;
	}
    }

    public static Company getCompanyBySymbol(String sym) {
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ResultSet rs = statement.executeQuery("SELECT * FROM Companies WHERE symbol='"+ sym + "';");
	     ) {
	    if(rs.next()) {
		String name = rs.getString("name");
		sym = rs.getString("symbol");
		double sv = rs.getDouble("stockValue");
		int as = rs.getInt("availableStocks");
		return new Company(name, sym, sv, as);
	    } else 
		return null;
	} catch(SQLException e) {
	    return null;
	}
    }

    public String getName() {
	return name;
    }

    public String getSymbol() {
	return symbol;
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
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ) {
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
