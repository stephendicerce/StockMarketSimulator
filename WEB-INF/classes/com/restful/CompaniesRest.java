package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.simulator.Company;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /companies/{symbol}
@Path("/companies/{symbol}")
    public class CompaniesRest {

	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getCompany( @PathParam("symbol") String sym ) {

	    if(sym == null || sym.trim().length() == 0) {
		return Response.serverError().entity("sym cannot be blank").build();
	    }

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
	
    }