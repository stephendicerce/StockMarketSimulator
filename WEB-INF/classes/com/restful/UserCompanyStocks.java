package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.simulator.User;
import com.simulator.Company;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

// username for user
// symbol for company
@Path("/stocks/{username}/{symbol}")
    public class UserCompanyStocks {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getStocks( @PathParam("username") String name, @PathParam("symbol") String symbol ) {

	    updatePrices();

	    com.simulator.User user = com.simulator.User.loadUser(name);
	    com.simulator.Company company = com.simulator.Company.getCompanyBySymbol(symbol);
	    if(user == null) {
		return Response.status(Response.Status.NOT_FOUND).entity("User " + name + " not found.").build();
	    }
	    if(company == null) {
		return Response.status(Response.Status.NOT_FOUND).entity("Company " + symbol + " not found.").build();
	    }

	    String json = getUserCompanyStockJSON(user, company);
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	private String getUserCompanyStockJSON(com.simulator.User user, com.simulator.Company company) {
	    String json = "{\n";
	    String cname = company.getName();
	    json += "  \"price\": " + company.getStockValue() + ",\n";
	    json += "  \"available\": " + company.getNumberOfAvailableStocks() + ",\n";
	    json += "  \"stocks\": " + user.getNumberOfStocks(cname) + ",\n";
	    json += "  \"averagePurchasePrice\": " + user.getAveragePriceBoughtAt(cname) + "\n";
	    json += "}";
	    return json;
	}
	
	@POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response performTransaction( @PathParam("username") String name, @PathParam("symbol") String symbol, InputStream data) {
	    
	    com.simulator.User user = com.simulator.User.loadUser(name);
	    com.simulator.Company company = com.simulator.Company.getCompanyBySymbol(symbol);
	    if(user == null) {
		return Response.status(Response.Status.NOT_FOUND).entity("User " + name + " not found.").build();
	    }
	    if(company == null) {
		return Response.status(Response.Status.NOT_FOUND).entity("Company " + symbol + " not found.").build();
	    }

	    String json = "";
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(data));
		String line = null;
		while((line = in.readLine()) != null)
		    json += line;
		
		JSONObject obj = new JSONObject(json);
		String action = obj.getString("action");
		
		json = "";
		String cname = company.getName();
		if(action.equals("buy")) {
		    if(user.purchaseStock(cname)) {
			json = "{ ";
			json += "\"status\" : \"success\","; 
			json += "\"balance\" : " + user.getMoney() + ",";
			json += "\"stockValue\" : " + user.getStockValue() + ",";
			json += "\"available\" : " + company.getNumberOfAvailableStocks() + ",";
			json += "\"owned\" : " + user.getNumberOfStocks(cname) + ",";
			json += "\"averagePurchasePrice\" : " + user.getAveragePriceBoughtAt(cname);
			json += "}";
		    } else // not enough money or stocks available
			json = "{ \"status\" : \"Failed\" }";
		} else if(action.equals("sell")) {
		    if(user.sellStock(cname)) {
			json = "{ ";
			json += "\"status\" : \"success\","; 
			json += "\"balance\" : " + user.getMoney() + ",";
			json += "\"stockValue\" : " + user.getStockValue() + ",";
			json += "\"available\" : " + company.getNumberOfAvailableStocks() + ",";
			json += "\"owned\" : " + user.getNumberOfStocks(cname) + ",";
			json += "\"averagePurchasePrice\" : " + user.getAveragePriceBoughtAt(cname);
			json += "}";
		    } else // not enough money or stocks available
			json = "{ \"status\" : \"Failed\" }";
		}
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	    } catch(IOException | JSONException e) { // bad json format
		return Response.status(Response.Status.BAD_REQUEST).build();
	    } 
	}

	// Updates the stock prices of the companies.
	public void updatePrices() {
	    com.simulator.Company[] companies = com.simulator.Company.getCompanies();
	    String[] symbols = new String[companies.length];
	    for(int i=0, length=companies.length; i<length; ++i) {
		symbols[i] = companies[i].getSymbol();
	    }
	    com.simulator.StockReader.updateStocks(symbols);
	}

    }
