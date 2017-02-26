package simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.NullPointerException;    

public class StockUpdate extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

	HttpSession session = req.getSession();
	User user = (User)session.getAttribute("user");
	Company[] companies = Company.getCompanies();
	String json;
	if(user == null) {
	    json = "{ \"timeout\" : true }";
	} else {
	    try {
		json = "{";
		json += " \"balance\" : " + user.getMoney() + ",";
		double stockValue = user.getStockValue();
		json += " \"stockValue\" : " + user.getStockValue() + ",";
		json += " \"companies\" : {";
		for(int i=0; i<companies.length; ++i) {
		    Company c = companies[i];
		    json += "\"" + c.getName() + "\" : {";
		    json += " \"stockValue\" : " + c.getStockValue() + ",";
		    json += " \"available\" : " + c.getNumberOfAvailableStocks() + ",";
		    json += " \"owned\" : " + user.getNumberOfStocks(c.getName());
		    json += "}";
		    if(i != companies.length - 1)
			json += ",";
		}
		json += " }";
		json += "}";
	    } catch(NullPointerException e) {
		json = "{ \"error\" : true }";
	    }
	}
	res.setContentType("application/json");
	res.setCharacterEncoding("UTF-8");
	res.getWriter().write(json);
    }
}

    /* example response:
    {
	balance: 300,
	stockValue: 200,
	companies: {
	    company0: {
		stockValue: 50,
		available: 96,
		owned: 4,
	    }, ...
	}
    }
    */
