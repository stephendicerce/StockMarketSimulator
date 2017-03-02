package simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;

public class Login extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String name = req.getParameter("name");
	String pass = req.getParameter("pass");
	
	User user = null;
	
	try (
	     Connection conn = DBConnector.getConnection();
	     Statement statement = conn.createStatement();
	     ResultSet rs = statement.executeQuery("SELECT * FROM Users WHERE name='" + name + "' AND password='" + pass + "'");
	     ) {
	    if(rs.next()) {
		if(rs.getString("name").equals(name)
		   && rs.getString("password").equals(pass))
		    user = User.loadUser(name);
	    }
	} catch(SQLException | NullPointerException e) {
	    user = null;
	}
	
	String json;
	if(user != null) {
	    json = "{ \"login_status\" : true }";
	    HttpSession session = req.getSession();
	    session.setMaxInactiveInterval(5*60);
	    session.setAttribute("user", user);
	} else {
	    json = "{ \"login_status\" : false }";
	}
	res.setContentType("application/json");
	res.setCharacterEncoding("UTF-8");
	res.getWriter().write(json);
    }
}
