package simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class Companies extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	Company[] companies = Company.getCompanies();
	
	boolean list = new Boolean(req.getParameter("list"));
	String companyName = req.getParameter("name");

	String response = "";

	if(list) { // List out company names
	    response = "<h2>Companies:</h2><ul>";
	     for(Company c : companies) {
		 response += "<li>" + c.getName() + " &nbsp&nbsp&nbsp Stock Price: $"
		     + String.format("%.2f", c.getStockValue()) + "</li>";
	     }
	    response += "</ul>";
	} else if(companyName != null) { // Give company name and value
	    boolean invalidName = true;
	    for(Company c : companies) {
		if(c.getName().equals(companyName)) {
		    response = "<p>" + c.getName() + " is valued at $"
			+ String.format("%.2f", c.getStockValue()) + ".</p>";
		    response += "<p>Stocks available: " + c.getNumberOfAvailableStocks() + "</p>";
		    
		    invalidName = false;
		    break;
		}
	    }
	    if(invalidName) {
		response = "<p>No company by the name " + companyName + " exists.</p>";
	    }
	}
	
	try {
	    out.println(response);
	} finally {
	    out.close();
	}
    }
}
