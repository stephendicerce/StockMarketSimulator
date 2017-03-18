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

@Path("/users")
    public class UsersPost {
	
	@POST
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response postUser(InputStream data) {
	    String json = "";
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(data));
		String line = null;
		while((line = in.readLine()) != null)
		    json += line;
		
		JSONObject obj = new JSONObject(json);
		String name = obj.getString("name");
		
		//Attempt to add the user
		// If it fails, return a server error
		if (com.simulator.User.loadUser(name) == null) {
		    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	    } catch(IOException | JSONException e) {
		return Response.status(Response.Status.BAD_REQUEST).build();
	    }
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

    }