package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.simulator.User;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

//Plain old Java Object. It does not extend as a class or implements
//an interface

//Sets the path to base URL + /users/{name}
@Path("/users/{name}")
    public class UserRest {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getUser( @PathParam("name") String name ) {

	    com.simulator.User u = com.simulator.User.loadUser(name);	    
	    if(u == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }

	    String json = getJsonForUser(u);
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response updateUser (@PathParam("name") String name, InputStream data) {
	    com.simulator.User u = com.simulator.User.loadUser(name);
	    if (u == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }

	    String json = "";
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(data));
		String line = null;
		while ((line = in.readLine()) != null)
		    json += line;

		JSONObject obj = new JSONObject(json);
		String password = obj.getString("password");

		if (!com.simulator.User.updateUser(name, password)){
		    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	    } catch(IOException | JSONException e) {
		return Response.status(Response.Status.BAD_REQUEST).build();
	    }
	    return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response deleteUser( @PathParam("name") String name ) {
	    com.simulator.User u = com.simulator.User.loadUser(name);
	    if(u == null) {
		return Response.status(Response.Status.NOT_FOUND).build();
	    }

	    if(!com.simulator.User.deleteUser(name)) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	    }
	    String json = getJsonForUser(u);
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	private String getJsonForUser(com.simulator.User u) {
	    String json = "{\n";
	    json += " \"name\": \"" + u.getName() + "\",\n";
	    json += " \"password\": \"" + u.getPassword() + "\",\n";
	    json += " \"money\": " + u.getMoney() + ",\n";
	    json += " \"stockValue\": " + u.getStockValue() + "\n";
	    json += "}";
	    return json;
	}

    }