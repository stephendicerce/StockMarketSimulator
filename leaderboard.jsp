<%@ page import="simulator.*" %>
<%
  User user = (User)session.getAttribute("user");
  Company[] companies = Company.getCompanies();
  if(user == null) { %>
<html>
  <body>
    <script>
      window.location.replace(location.origin + "/StockMarketSimulator/timeout");
    </script>
  </body>
</html>
<% } else if (companies == null) { %>
<html>
  <body>
    <script>
      window.location.replace(location.origin + "/StockMarketSimulator/error");
    </script>
  </body>
</html>
<%  } else { %>
<html>
  <head>
    <title>LeaderBoard</title>
    <style>
      li {
      margin: 10px 0;
      }

      table {
      border: 2px solid black;
      }

      td, th {
      border-right: 1px solid black;
      border-top: 1px solid black;
      }

    </style>
  </head>

  <body>
    <button OnClick='sendToDashboard()'>Dashboard</button>
    <h2>Leaderboard</h2>
    <input type="radio" name="view" value="money" checked="true" OnClick='sortByAmountOfMoney()'>Money</input>
    <input type="radio" name="view" value="stocks" OnClick='sortByAverageStockPrice()'>Stocks</input>

    <br>
    <br>
    <span id=companySelectorSpan>Company:
      <select id=companySelector onchange="sortByAverageStockPrice()">
	<% for (Company c : companies) {
	   out.println("<option value=\"" + c.getName() + "\">" + c.getName() + "</option>");
	   } %>
      </select>
    </span>
    <br>
    <br>
    
    <table id=leaderboard>
    </table>
    <script>
      sortByAmountOfMoney();

      function sendToDashboard() {
        var url = location.origin + "/StockMarketSimulator/dashboard";
        window.location.replace(url);
      }

      function sortByAmountOfMoney() {
        document.getElementById("companySelectorSpan").style.display = "none";
        var url = location.origin + "/StockMarketSimulator/leaderboardUpdate?sort=money";
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", url, false ); // false for a synchronous request
        xmlHttp.send( null );
        console.log(xmlHttp.responseText);
        var response = JSON.parse(xmlHttp.responseText);
        if (response.timeout) {
          window.location.replace(location.origin + "/StockMarketSimulator/timeout");
        } else if(response.error) {
           alert("An error occured.");
           location.reload();
        } else if(response.users) {
          leaderboardMode = "money";
          populateTable(response.users);
        }
      }

      function sortByAverageStockPrice() {
        document.getElementById("companySelectorSpan").style.display = "inline";
        var company = document.getElementById("companySelector").value;
        var url = location.origin + "/StockMarketSimulator/leaderboardUpdate?sort=stocks&company=" + company;
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", url, false ); // false for a synchronous request
        xmlHttp.send( null );
        console.log(xmlHttp.responseText);
        var response = JSON.parse(xmlHttp.responseText);
        if (response.timeout) {
          window.location.replace(location.origin + "/StockMarketSimulator/timeout");
        } else if(response.error) {
           alert("An error occured.");
           location.reload();
        } else if(response.users) {
          leaderboardMode = "stocks";
          populateTable(response.users);  
        }
      }
      
      // Takes a sorted array of users and builds the leaderboard
      function populateTable(users) {
        if(leaderboardMode === "money") {
          var html = "<tr>";
	  html += "<th>Number</th>";
	  html += "<th>Name</th>";
	  html += "<th>Money</th>";
	  html += "<th>Stock Value</th>";
	  html += "<th>Total</th>";
          html += "</tr>";

          for(var i=0; i<users.length; ++i) {
            var user = users[i];
            html += "<tr>";
	    html += "<td>" + (i+1) + "</td>";
	    html += "<td>" + user.name + "</td>";
	    html += "<td>" + user.money + "</td>";
            html += "<td>" + user.stockValue + "</td>";
	    html += "<td>" + user.totalMoney + "</td>";
            html += "</tr>";
          }
        } else if(leaderboardMode === "stocks") {
          html = "<tr>";
          html += "<th>Number</th>";
	  html += "<th>Name</th>";
          html += "<th>Number of Stocks</th>";
          html += "<th>Average Price</th>";
          html += "<th>Total Investment</th>";
          html += "</tr>";

          for(var i=0; i<users.length; ++i) {
            var user = users[i];
            html += "<tr>";
            html += "<td>" + (i+1) + "</td>";
	    html += "<td>" + user.name + "</td>";
            html += "<td>" + user.numberStocks + "</td>";
            html += "<td>" + user.averagePrice + "</td>";
            html += "<td>" + user.totalInvestment + "</td>";
	    html += "</tr>";
          }
        }
        document.getElementById("leaderboard").innerHTML = html;
      }
      </script>
    </body>
</html>
<% } %>

