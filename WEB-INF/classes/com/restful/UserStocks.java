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

@Path("/stocks/{username}")
    public class UserStocks {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getStocks( @PathParam("username") String name ) {

	    com.simulator.User user = com.simulator.User.loadUser(name);
	    if(user == null) {
		return Response.status(Response.Status.NOT_FOUND).entity("User " + name + " not found.").build();
	    }

	    String json = getUserStockJSON(user);
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	private String getUserStockJSON(com.simulator.User user) {
	    com.simulator.Company[] companies = com.simulator.Company.getCompanies();
	    String json = "{\n";
	    
	    for(int i=0, length=companies.length; i<length; ++i) {
		com.simulator.Company c = companies[i];
		String cname = c.getName();
		json += "  \"" + c.getSymbol() + "\": {\n";
		json += "    \"stocks\": " + user.getNumberOfStocks(cname) + ",\n";
		json += "    \"averagePurchasePrice\": " + user.getAveragePriceBoughtAt(cname) + "\n";
		json += "  }";
		if(i<length-1) json += ",";
		json += "\n";
	    }
	    json += "}";
	    return json;
	}
	
    }

