package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.simulator.Company;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /companies/{symbol}
@Path("/companies/{symbol}")
    public class CompanyRest {

	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getCompany( @PathParam("symbol") String sym ) {

	    com.simulator.Company c = com.simulator.Company.getCompanyBySymbol(sym);
	    if(c == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }

	    String json = "{\n";
	    json += "  name: " + c.getName() + ",\n";
	    json += "  symbol: " + c.getSymbol() + ",\n";
	    json += "  stockValue: " + c.getStockValue() + ",\n";
	    json += "  availableStocks: " + c.getNumberOfAvailableStocks() + "\n";
	    json += "}";
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@PUT
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response updateCompany( @PathParam("symbol") String sym, InputStream data) {
	    com.simulator.Company c = com.simulator.Company.getCompanyBySymbol(sym);
	    if(c == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }
	    
	    String json = "";
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(data));
		String line = null;
		while((line = in.readLine()) != null)
		    json += line;

		JSONObject obj = new JSONObject(json);
		String name = obj.getString("name");
		
		// Attempt to update the company
		// If it fails, return a server error
		if(!com.simulator.Company.updateCompany(name, sym)) {
		    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	    } catch(IOException | JSONException e) { // bad json format
		return Response.status(Response.Status.BAD_REQUEST).build();
	    }
	    return Response.status(Response.Status.OK).build();
	}
	
	@DELETE
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response deleteCompany( @PathParam("symbol") String sym ) {
	    com.simulator.Company c = com.simulator.Company.getCompanyBySymbol(sym);
	    if(c == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }

	    // Attempt to add the company
	    // If it fails, return a server error
	    if(!com.simulator.Company.deleteCompany(sym)) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	    }
	    return Response.status(Response.Status.OK).build();
	}
    }