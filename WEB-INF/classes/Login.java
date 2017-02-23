import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.NumberFormatException;

public class Login extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String name = req.getParameter("name");
	User user = TestUsers.getUser(name);
	HttpSession session = req.getSession();
	session.setMaxInactiveInterval(30);
	session.setAttribute("user", user);

	try {
	    out.println("<html>");
	    out.println("  <head>");
	    out.println("    <script>");
	    out.println("      window.location.replace(location.origin + \"/csc435hw1/simulate\");");
	    out.println("    </script>");
	    out.println("  </head>");
	    out.println("</html>");
	} finally {
	    out.close();
	}
    }
}
