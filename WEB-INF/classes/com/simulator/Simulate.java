package com.simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.NullPointerException;

public class Simulate extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	
	HttpSession session = req.getSession();
	User user = (User)session.getAttribute("user");
	if(user == null) {
	    String next = "/timeout.jsp";
	    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(next);
	    dispatcher.forward(req,res);
	} else {
	    Company[] companies = Company.getCompanies();

	    String action = (String)req.getParameter("action");
	    String company = (String)req.getParameter("company");
	    String json = "{}";
	    
	    if(action != null && company != null) {
		if(action.equals("buy")) {
		    if(user.purchaseStock(company)) {
			json = "{ ";
			json += "\"status\" : \"success\","; 
			json += "\"balance\" : " + user.getMoney();
			json += "}";
		    } else // not enough money or stocks available
			json = "{ \"status\" : \"The transaction could not be completed\" }";
		} else if(action.equals("sell")) {
		    if(user.sellStock(company)) {
			json = "{ ";
			json += "\"status\" : \"success\","; 
			json += "\"balance\" : " + user.getMoney();
			json += "}";
		    } else // not enough money or stocks available
			json = "{ \"status\" : \"The transaction could not be completed\" }";
		}
	    }

	    res.setContentType("application/json");
	    res.setCharacterEncoding("UTF-8");
	    res.getWriter().write(json);
	}
    }
}
