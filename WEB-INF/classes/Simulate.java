import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class Simulate extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	
	HttpSession session = req.getSession();
	User user = (User)session.getAttribute("user");
	if(user == null) {
	    String html = notifyNotLoggedIn();
	    try {
		out.println(html);
	    } finally {
		out.close();
	    }
	} else {
	    Company[] companies = TestCompanies.getCompanies();

	    String action = (String)req.getParameter("action");
	    String company = (String)req.getParameter("company");
	    boolean successfulAction = false;

	    if(action != null && company != null) {
		if(action.equals("buy")) {
		    successfulAction = user.purchaseStock(company);
		} else if(action.equals("sell")) {
		    successfulAction = user.sellStock(company);
		}
	    }
	    
	    try {
		out.println("<html>");
		out.println("  <head>");
		out.println("    <title>Stock Simulator</title>");
		out.println("    <style>");
		out.println("      li {");
		out.println("        margin: 10px 0;");
		out.println("      }");
		out.println("    </style>");
		out.println("  </head>");
	
		out.println("  <body>");
	
		out.println("    <h2>Welcome, " + user.getName() + "!</h2>");
		out.println("    <h4>Balance: $" + String.format("%.2f", user.getMoney()) + "</h4>");
		out.println("    <h4>Stock Value: $" + String.format("%.2f", user.getStockValue()));
		out.println("    <br/>");

		out.println("    <h3>" + companies.length + " Companies:</h3>");
		out.println("    <ol>");
		for(Company c : companies) {
		    String cname = c.getName();
		    int owned = user.getNumberOfStocks(cname);
		    out.println("      <li>");
		    out.println("        <b>" + cname + "</b>" +
				" &nbsp&nbsp&nbsp Stock Price: " +
				String.format("%.2f", c.getStockValue()) + "<br/>");
		    out.println("        Number Available: " +
				c.getNumberOfAvailableStocks() +
				"<br/> Number Owned: " + owned + "<br/>");
		    out.println("        <button OnClick='act(\"" + cname + "\", \"buy\"" +
				")'>Purchase</button>" +
				" <button OnClick='act(\"" + cname + "\", \"sell\"" +
				")'>Sell</button><br/>");
		    out.println("      </li>");
		}
		out.println("    </ol>");
	
		out.println("    <script>");
		out.println("      setTimeout(refresh, " + TestCompanies.timeToNextUpdate() + ")");
		out.println();
		out.println("      function refresh() {");
		out.println("        location.reload();");
		out.println("      }");
		out.println();
		out.println("      function act(company, action) {");
		out.println("        var url = location.origin + \"/csc435hw1" +
			    "/simulate?action=\" + action + \"&company=\" + company;");
		out.println("        window.location.replace(url);");
		out.println("      }");
		out.println("    </script>");

		out.println("</body>");
		out.println("</html>");
	    } finally {
		out.close();
	    }
	}
    }
    private String notifyNotLoggedIn() {
	String html = "";
	html += "<html>";
	html += "  <body>";
	html += "    <h1>Session has expired. Please <a href=\"#\" onclick=login()>Log in</a>.";
	html += "    <script>";
	html += "      function login() {";
	html += "        window.location.replace(location.origin + \"/csc435hw1\");";
	html += "      }";
	html += "    </script>";
	html += "  </body>";
	html += "</html>";
	return html;
    }
}
