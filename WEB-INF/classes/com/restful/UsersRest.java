package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.simulator.User;

//Plain old Java Object. It does not extend as a class or implements
//an interface

//Sets the path to base URL + /users/{name}
@Path("/users/{name}")
    public class UsersRest {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getUser( @PathParam("name") String name ) {
	    
	    if(name == null || name.trim().length() == 0) {
		return Response.serverError().entity("name cannot be blank").build();
	    }

	    com.simulator.User u = com.simulator.User.getUserByName(name);
	    if(u == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }
	    String json = "{\n";
	    json += "  name: " + u.getName() + ",\n";
	    json += "  money: " + u.getMoney() + "\n";
	    json += "}";
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
    }