<%@ page import="simulator.*" %>
<%
  User user = (User)session.getAttribute("user");
  if(user == null) { %>
<html>
  <body>
    <script>
      window.location.replace(location.origin + "/StockMarketSimulator/timeout");
    </script>
  </body>
</html>
<% } else { %>
<html>
  <head>
    <title>Dashboard</title>
  </head>
  <body>
    <h2>Welcome to your Dashboard, <% out.println(" " + user.getName() + "!"); %></h2>
    <h4>Balance: $ <% out.println(String.format("%.2f", user.getMoney())); %> </h4>
    <button OnClick="enterStockMarket()">Stock Market</button>
    <script>
      function enterStockMarket() {
        var url = location.origin + "/StockMarketSimulator/stockMarket";
        window.location.replace(url);
      }
    </script>
  </body>
</html>
<% } %>
