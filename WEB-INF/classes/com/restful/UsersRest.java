package com.restful;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.simulator.User;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

@Path("/users")
    public class UsersRest {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getUsers(@Context UriInfo uriInfo) {
	    com.simulator.User[] users = com.simulator.User.getUsers();
	    if(users == null)
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

	    String json = "[\n";
	    for(int i = 0, length = users.length; i < length; ++i) {
		com.simulator.User u = users[i];
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		String currentPath = builder.build().toString();
		String uname = u.getName();
		json += "  {\n";
		json += "    \"name\": \"" + uname + "\",\n";
		json += "    \"password\": \"" + u.getPassword() + "\",\n";
		json += "    \"money\": " + u.getMoney() + ",\n";
		json += "    \"stockValue\": " + u.getStockValue() + ",\n";
		json += "    \"links\": [\n";
		json += "      {\n";
		json += "        \"rel\": \"self\",\n";
		json += "        \"href\": \"" + currentPath + "/" + uname + "\"\n";;
		json += "      }\n";
		json += "    ]\n";
		json += "  }";
		if(i < length - 1)
		    json += ",";
		json += "\n";
	    }
	    json += "]";
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@POST
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response postUsers (InputStream data) {

	    String json = "";
		try {
		    BufferedReader in = new BufferedReader(new InputStreamReader(data));
		    String line = null;
		    while((line = in.readLine()) != null)
			json += line;

		    JSONObject obj = new JSONObject(json);
		    String name = obj.getString("name");
		    String password = obj.getString("password");

		    if( name == null || password == null || password.equals("")
			|| name.equals("") )
			return Response.status(Response.Status.BAD_REQUEST).entity("Must provide a name.").build();

		    com.simulator.User u = com.simulator.User.loadUser(name);
		    if(u != null) {
			return Response.status(Response.Status.CONFLICT).entity("User " + name + " already exists").build();
		    }

		    if(!com.simulator.User.addUser(name, password)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		    }

		} catch(IOException | JSONException e) {
		    return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.status(Response.Status.CREATED).build();
	}
		    

    }











