package simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.NullPointerException;

public class Leaderboard extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	HttpSession session = req.getSession();
	User user = (User)session.getAttribute("user");
	String sortMethod = req.getParameter("sort");
	User[] users = Users.getUsers();
	User temp;
	String json = "";
	if (user == null) {
	    json = "{ \"timeout\" : true }";
	} else {
	    
	    

	    try {
		if (sortMethod == null || sortMethod.equals("money")) {
		    for (int i = 0; i < users.length; ++i) {
			for (int j = i + 1; j < users.length; ++j) {
			    if (users[j].getMoney() > users[i].getMoney()) {
				temp = users[i];
				users[i] = users[j];
				users[j] = temp;
			    }
			}  
		    }
		    json = "{";
		    json += " \"users\" : [";
		    for (int i = 0; i < users.length; ++i) {
			double total;
			int row = i + 1;
			User u = users[i];
			json += "{";
			json += "\"name\" : \"" + u.getName() + "\", ";
			json += "\"money\" : " + String.format("%.2f", u.getMoney()) + ",";
			json += "\"stockValue\" : " + String.format("%.2f", u.getStockValue()) + ",";
			total = u.getMoney() + u.getStockValue();
			json += "\"totalMoney\" : " + String.format("%.2f", total);
			json += "}";
			if (i != users.length - 1)
			    json += ",";
		    }
		    json += "]";
		    json += "}";
		} else if (sortMethod.equals("stocks")) {
		    String companyName = req.getParameter("company");
		    Company company = Company.getCompany(companyName);
		    for (int i = 0; i <users.length; ++i) {
			for (int j = i + 1; j <users.length; ++j) {
                            if (users[j].getNumberOfStocks(companyName) > users[i].getNumberOfStocks(companyName)) {
				temp = users[i];
				users[i] = users[j];
				users[j] = temp;
                            }
                        }
                    }
                    json = "{";
		    json += "\"users\" : [";
                    for (int i = 0; i < users.length; ++i) {
			int row = i+1;
			double total;
			User u = users[i];
			json += "{";
                        json += "\"name\" : \"" + u.getName() + "\",";
		        int stocks = u.getNumberOfStocks(companyName);
                        json += "\"numberStocks\" : " + stocks + ",";
			double averagePrice = u.getAveragePriceBoughtAt(companyName);
                        json += "\"averagePrice\" : " + String.format("%.2f", averagePrice) + ",";
                        total = stocks * averagePrice;
                        json += "\"totalInvestment\" : "+ String.format("%.2f", total);
			json +="}";
			if (i != users.length - 1)
			    json += ",";
		    }
		    json += "]";
		    json += "}";
		}
		
		
	    } catch (NullPointerException e) {
		json = "{ \"error\" : true }";
	    }
	}
	res.setContentType("application/json");
	res.setCharacterEncoding("UTF-8");
	res.getWriter().write(json);
    }
}