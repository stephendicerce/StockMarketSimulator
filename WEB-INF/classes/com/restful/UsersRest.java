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

@Path("/users")
    public class UsersRest {
	
	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getUsers(@Context UriInfo uriInfo, @QueryParam("sort") String sortingMethod, @QueryParam("company") String companyName) {
	    com.simulator.User[] users = com.simulator.User.getUsers();
	    com.simulator.Company[] companies = com.simulator.Company.getCompanies();
	    com.simulator.User temp;
	    String json = "";
	    if(users == null)
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	    if(sortingMethod != null) {
		if(!sortingMethod.equals("stocks") || companyName == null) {
		    if (sortingMethod.equals("money")){
			for (int i = 0; i < users.length; ++i) {
			    for (int j = i + 1; j < users.length; ++j) {
				if (users[j].getMoney() > users[i].getMoney()) {
				    temp = users[i];
				    users[i] = users[j];
				users[j] = temp;
				}
			    }
			}
			
		    } else if (sortingMethod.equals("total_money")) {
			for (int i = 0; i < users.length; ++i) {
			    double totalMoneyI = users[i].getMoney() + users[i].getStockValue();
			    for (int j = i + 1; j < users.length; ++j) {
				double totalMoneyJ = users[j].getMoney() + users[j].getStockValue();
				if (totalMoneyJ > totalMoneyI) {
				    temp = users[i];
				    users[i] = users[j];
				    users[j] = temp;
				}
			    }
			}
		    }
		    json = "[\n";
		    for(int i = 0, length = users.length; i < length; ++i) {
			com.simulator.User u = users[i];
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			String currentPath = builder.build().toString();
			String uname = u.getName();
			double totalMoney = u.getMoney() + u.getStockValue();
			json += "  {\n";
			json += "    \"name\": \"" + uname + "\",\n";
			json += "    \"password\": \"" + u.getPassword() + "\",\n";
			json += "    \"money\": " + u.getMoney() + ",\n";
			json += "    \"stockValue\": " + u.getStockValue() + ",\n";
			json += "    \"totalMoney\": " + totalMoney + ",\n";
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
		} else if (sortingMethod.equals("stocks") && companyName != null) {
		    for (int i = 0; i <users.length; ++i) {
			for (int j = i + 1; j <users.length; ++j) {
			    if (users[j].getNumberOfStocks(companyName) > users[i].getNumberOfStocks(companyName)) {
				temp = users[i];
				users[i] = users[j];
				users[j] = temp;
			    }
			}
		    }
		    
		    json = "[\n";
		    json += "{";
		    json += "\"users\" : [";
		    for (int i = 0; i < users.length; ++i) {
			com.simulator.User u = users[i];
			com.simulator.Company company = com.simulator.Company.getCompany(companyName);
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			String currentPath = builder.build().toString();
			String uname = u.getName();
			int row = i+1;
			double total;
			json += "{";
			json += "\"name\" : \"" + u.getName() + "\",";
			int stocks = u.getNumberOfStocks(companyName);
			json += "\"numberStocks\" : " + stocks + ",";
			double averagePrice = u.getAveragePriceBoughtAt(companyName);
			json += "\"averagePrice\" : " + String.format("%.2f", averagePrice) + ",";
			total = stocks * averagePrice;
			json += "\"totalInvestment\" : "+ String.format("%.2f", total);
			json +="}";
			json += "    \"links\": [\n";
			json += "      {\n";
			json += "        \"rel\": \"self\",\n";
			json += "        \"href\": \"" + currentPath + "/" + uname + "\"\n";;
			json += "      }\n";
			json += "    ]\n";
			if (i != users.length - 1)
			    json += ",";
		    }
		    json += "]\n";
		    json += "}\n";
		    json += "]";
		}
	    } else {
		json = "[\n";
			for(int i = 0, length = users.length; i < length; ++i) {
			    com.simulator.User u = users[i];
			    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			    String currentPath = builder.build().toString();
			    String uname = u.getName();
			    double totalMoney = u.getMoney() + u.getStockValue();
			    json += "  {\n";
			    json += "    \"name\": \"" + uname + "\",\n";
			    json += "    \"password\": \"" + u.getPassword() + "\",\n";
			    json += "    \"money\": " + u.getMoney() + ",\n";
			    json += "    \"stockValue\": " + u.getStockValue() + ",\n";
			    json += "    \"totalMoney\": " + totalMoney + ",\n";
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
	    }
// 	    if ((sortingMethod.equals("money") || sortingMethod.equals("total_money") || 
// 		 sortingMethod == null || sortingMethod.equals("")) && 
// 		(companyName == null || companyName.equals(""))) {
// 		if (sortingMethod.equals("money")){
// 		    for (int i = 0; i < users.length; ++i) {
// 			for (int j = i + 1; j < users.length; ++j) {
// 			    if (users[j].getMoney() > users[i].getMoney()) {
// 				temp = users[i];
// 				users[i] = users[j];
// 				users[j] = temp;
// 			    }
// 			}
// 		    }
		    
// 		} else if (sortingMethod.equals("total_money")) {
// 		    for (int i = 0; i < users.length; ++i) {
// 			double totalMoneyI = users[i].getMoney() + users[i].getStockValue();
// 			for (int j = i + 1; j < users.length; ++j) {
// 			    double totalMoneyJ = users[j].getMoney() + users[j].getStockValue();
// 			    if (totalMoneyJ > totalMoneyI) {
// 				temp = users[i];
// 				users[i] = users[j];
// 				users[j] = temp;
// 			    }
// 			}
// 		    }
		    
// 		}
// 		json = "[\n";
// 		for(int i = 0, length = users.length; i < length; ++i) {
// 		    com.simulator.User u = users[i];
// 		    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
// 		    String currentPath = builder.build().toString();
// 		    String uname = u.getName();
// 		    double totalMoney = u.getMoney() + u.getStockValue();
// 		    json += "  {\n";
// 		    json += "    \"name\": \"" + uname + "\",\n";
// 		    json += "    \"password\": \"" + u.getPassword() + "\",\n";
// 		    json += "    \"money\": " + u.getMoney() + ",\n";
// 		    json += "    \"stockValue\": " + u.getStockValue() + ",\n";
// 		    json += "    \"totalMoney\": " + totalMoney + ",\n";
// 		    json += "    \"links\": [\n";
// 		    json += "      {\n";
// 		    json += "        \"rel\": \"self\",\n";
// 		    json += "        \"href\": \"" + currentPath + "/" + uname + "\"\n";;
// 		    json += "      }\n";
// 		    json += "    ]\n";
// 		    json += "  }";
// 		    if(i < length - 1)
// 			json += ",";
// 		    json += "\n";
// 		}
// 		json += "]";
// 	    } else if(sortingMethod.equals("stocks")) {
// 		for (int i = 0; i <users.length; ++i) {
// 		    for (int j = i + 1; j <users.length; ++j) {
// 			if (users[j].getNumberOfStocks(companyName) > users[i].getNumberOfStocks(companyName)) {
// 			    temp = users[i];
// 			    users[i] = users[j];
// 			    users[j] = temp;
// 			}
// 		    }
// 		}
		
// 		json = "[\n";
// 		json += "{";
// 		json += "\"users\" : [";
// 		for (int i = 0; i < users.length; ++i) {
// 		    com.simulator.User u = users[i];
// 		    com.simulator.Company company = com.simulator.Company.getCompany(companyName);
// 		    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
// 		    String currentPath = builder.build().toString();
// 		    String uname = u.getName();
// 		    int row = i+1;
// 		    double total;
// 		    json += "{";
// 		    json += "\"name\" : \"" + u.getName() + "\",";
// 		    int stocks = u.getNumberOfStocks(companyName);
// 		    json += "\"numberStocks\" : " + stocks + ",";
// 		    double averagePrice = u.getAveragePriceBoughtAt(companyName);
// 		    json += "\"averagePrice\" : " + String.format("%.2f", averagePrice) + ",";
// 		    total = stocks * averagePrice;
// 		    json += "\"totalInvestment\" : "+ String.format("%.2f", total);
// 		    json +="}";
// 		    json += "    \"links\": [\n";
//                     json += "      {\n";
//                     json += "        \"rel\": \"self\",\n";
//                     json += "        \"href\": \"" + currentPath + "/" + uname + "\"\n";;
//                     json += "      }\n";
//                     json += "    ]\n";
// 		    if (i != users.length - 1)
// 			json += ",";
// 		}
// 		json += "]\n";
// 		json += "}\n";
// 		json += "]";
// 	    }
	    
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











