package simulator;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class Test extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	
	PrintWriter out = res.getWriter();
	
	Connection conn = DBConnector.getConnection();
	if(conn == null) {
	    try {
		out.println("An error occurred. Unable to access the database.");
	    } finally {
		out.close();
		return;
	    }
	}

	try {
	    Statement statement = conn.createStatement();
	    String name = "Sarah";
	    ResultSet rs = statement.executeQuery("SELECT * FROM User WHERE name='" + name + "' AND password='SarahPas'");
	    if(rs.next()) {
		out.println(rs.getString("name").equals(name));
	    } else {

	    }
	    
	} catch(SQLException e) {
	    out.println(e);
	} finally {
            out.close();
	}
	
    // 	String nextJSP = "/test.jsp";
    // 	RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
    // 	dispatcher.forward(req,res);
    }
}
