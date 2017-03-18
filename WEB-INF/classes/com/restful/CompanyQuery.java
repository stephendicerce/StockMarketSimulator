package com.restful;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.simulator.Company;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /companies/{symbol}
@Path("/companies/{symbol}")
    public class CompanyQuery {

       	// This method is called if HTML is request
	@GET
	    @Produces(MediaType.TEXT_HTML)
	    public String sayHtmlHello( @PathParam("symbol") String sym ) {
	    com.simulator.Company c = com.simulator.Company.getCompanyBySymbol(sym);
	    String response = "Company " + sym + " not found.";
	    if(c != null)
		response = c.getName();
	    return "<html> " + "<title>Company Query</title>"
		+ "<body><h1>" + response + "</body></h1>" + "</html> ";
	}
	
    }