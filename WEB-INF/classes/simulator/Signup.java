package simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;

public class Signup extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String name = req.getParameter("name");
	String pass = req.getParameter("pass");
	
	boolean userNameAvailable = true;
	try {
	    Connection conn = DBConnector.getConnection();
	    Statement statement = conn.createStatement();
	    ResultSet rs = statement.executeQuery("SELECT * FROM Users WHERE name='" + name + "'");
	    while(rs.next()) {
		userNameAvailable = false;
	    }
	} catch(SQLException | NullPointerException e) {
	    
	}

	if(userNameAvailable) { // create user
	    try {
		Connection conn = DBConnector.getConnection();
		Statement statement = conn.createStatement();
		statement.executeUpdate("INSERT INTO Users Values('"
						      + name + "', '"
						      + pass + "', "
						      + User.START_MONEY + ")"
						      );
		Company[] companies = Company.getCompanies();
		for(Company c : companies) {
		    statement.executeUpdate("INSERT INTO Stocks VALUES('"
					    + name + "', '"
					    + c.getName() + "', "
					    + 0 + ")"
					    );
		}
		String next = "/login";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(next);
		dispatcher.forward(req,res);
	    } catch(SQLException | NullPointerException e) {
		
	    }
	} else { // Username taken
	    String json = "{ \"login_status\" : false }";
	    res.setContentType("application/json");
	    res.setCharacterEncoding("UTF-8");
	    res.getWriter().write(json);
	}
    }
}
