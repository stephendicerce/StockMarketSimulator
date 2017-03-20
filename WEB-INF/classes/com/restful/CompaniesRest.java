package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.simulator.Company;
import com.simulator.StockReader;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

@Path("/companies")
    public class CompaniesRest {

	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getCompanies() {

	    updatePrices();

	    com.simulator.Company[] companies = com.simulator.Company.getCompanies();
	    if(companies == null)
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	    
	    String json = "[\n";
	    for(int i=0, length=companies.length; i<length; ++i) {
		com.simulator.Company c = companies[i];
		json += "  {\n";
		json += "    \"name\": \"" + c.getName() + "\",\n";
		json += "    \"symbol\": \"" + c.getSymbol() + "\",\n";
		json += "    \"stockValue\": " + c.getStockValue() + ",\n";
		json += "    \"availableStocks\": " + c.getNumberOfAvailableStocks() + "\n";
		json += "  }";
		if(i<length-1)
		    json += ",";
		json += "\n";
	    }
	    json += "]";
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@POST
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response postCompany(InputStream data) {

	    String json = "";
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(data));
		String line = null;
		while((line = in.readLine()) != null)
		    json += line;
		
		JSONObject obj = new JSONObject(json);
		String name = obj.getString("name");
		String symbol = obj.getString("symbol");

		if( symbol == null || symbol.equals("") ||
		    name == null || name.equals("") )
		    return Response.status(Response.Status.BAD_REQUEST).entity("Must provide a symbol and name.").build();
		
		com.simulator.Company c = com.simulator.Company.getCompanyBySymbol(symbol);
		if(c != null) {
		    return Response.status(Response.Status.CONFLICT).entity("Company " + symbol + " already exists").build();
		}

		// Attempt to add the company
		// If it fails, return a server error
		if(!com.simulator.Company.addCompany(name, symbol)) {
		    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	    } catch(IOException | JSONException e) { // bad json format
		return Response.status(Response.Status.BAD_REQUEST).build();
	    }
	    return Response.status(Response.Status.CREATED).build();
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