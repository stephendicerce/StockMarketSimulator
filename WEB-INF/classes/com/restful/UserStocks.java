package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.simulator.User;
import com.simulator.Company;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

@Path("/stocks/{username}")
    public class UserStocks {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getStocks( @PathParam("username") String name, @Context UriInfo uriInfo ) {

	    updatePrices();

	    com.simulator.User user = com.simulator.User.loadUser(name);
	    if(user == null) {
		return Response.status(Response.Status.NOT_FOUND).entity("User " + name + " not found.").build();
	    }

	    String json = getUserStockJSON(user, uriInfo);
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	private String getUserStockJSON(com.simulator.User user, UriInfo uriInfo) {
	    com.simulator.Company[] companies = com.simulator.Company.getCompanies();
	    String json = "{\n";
	    
	    for(int i=0, length=companies.length; i<length; ++i) {
		com.simulator.Company company = companies[i];
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		String currentPath = builder.build().toString();
		String cname = company.getName();
		String csym = company.getSymbol();
		json += "  \"" + csym + "\": {\n";
		json += "    \"price\": " + company.getStockValue() + ",\n";
		json += "    \"available\": " + company.getNumberOfAvailableStocks() + ",\n";
		json += "    \"stocks\": " + user.getNumberOfStocks(cname) + ",\n";
		json += "    \"averagePurchasePrice\": " + user.getAveragePriceBoughtAt(cname) + ",\n";
		json += "    \"links\": [\n";
		json += "      {\n";
		json += "        \"rel\": \"self\",\n";
		json += "        \"href\": \"" + currentPath + "/" + csym + "\"\n";;
		json += "      }\n";
		json += "    ]\n";
		json += "  }";
		if(i<length-1) json += ",";
		json += "\n";
	    }
	    json += "}";
	    return json;
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

