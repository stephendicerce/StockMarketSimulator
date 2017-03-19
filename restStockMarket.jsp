<%@ page import="com.simulator.*" %>
<%
   com.simulator.Company.updatePrices();
   com.simulator.User user = (com.simulator.User)session.getAttribute("user");
   com.simulator.Company[] companies = com.simulator.Company.getCompanies();
   if(user == null) { %>  
<html>
  <body>
    <script>
      window.location.replace(location.origin + "/StockMarketSimulator/restTimeout");
    </script>
  </body>
</html>
<%  } else if(companies == null) { %>
<html>
  <body>
    <script>
      window.location.replace(location.origin + "/StockMarketSimulator/restError");
    </script>
  </body>
</html>
<%  } else { %>
<html>
  <head>
    <title>Stock Simulator</title>
    <style>
      li {
      margin: 10px 0;
      }
    </style>
  </head>
  
  <body>
    <button OnClick='sendToDashboard()'>Dashboard</button>&nbsp
    Status: <span id=status></span>
    <h2>Welcome to the stock market, <% out.println(" " + user.getName() + "!"); %></h2>
    <h4>Balance: $
      <span id=balance>

      </Span>
    </h4>
    
    <h4>Stock Value: $
      <span id=stockValue>

      </span>
    </h4>
    <br/>
    
    <h3> <% out.print(companies.length); %> Companies:</h3>
    <ol>
      <% for(com.simulator.Company c : companies) {
           String cname = c.getName();
           int owned = user.getNumberOfStocks(cname);
      %>
	<li>
	  <b> <% out.print(cname); %> </b>
	  &nbsp&nbsp&nbsp Stock Price:
	  <span id=<% out.print("\"" + cname + "price\""); %> >
	    <!-- <% out.print(String.format("%.2f", c.getStockValue())); %> -->
	  </span>
	  <br/>
	  Number Available:
	  <span id=<% out.print("\"" + cname + "available\""); %> >
	    <!-- <% out.print(c.getNumberOfAvailableStocks()); %> -->
	  </span>
	  &nbsp&nbsp&nbsp Average Purchased Price:
	  <span id=<% out.print("\"" + cname + "average\""); %> >
	  </span>
	  <br/> Number Owned: <span id=<% out.print("\"" + cname + "owned\""); %> >
	    <!-- <% out.print(owned); %> -->
	  </span>
	  <br/>
	  <button OnClick='act(<% out.print("\"" + cname + "\""); %>, "buy")'>Purchase</button>
	  <button OnClick='act(<% out.print("\"" + cname + "\""); %>, "sell")'>Sell</button><br/>
	</li>
	<% } %>
      </ol>
      
      <script>
	function act(company, action) {
	  document.getElementById("status").innerHTML = "Requesting transaction...";
	  var url = location.origin + "/StockMarketSimulator/simulate?action=" + action + "&company=" + company;
          var xmlHttp = new XMLHttpRequest();
          xmlHttp.open( "GET", url, false ); // false for synchronous request
          xmlHttp.send( null );
	  var response = JSON.parse(xmlHttp.responseText);
          var status = response.status;
          if(status == "success") {
            update(false);
	    document.getElementById("status").innerHTML = "Transaction successful!";
	  } else {
	    alert(status);
            document.getElementById("status").innerHTML = "Transaction failed.";
	    location.reload();
	  }
	}

	function sendToDashboard() {
	  var url = location.origin + "/StockMarketSimulator/restDashboard";
	  window.location.replace(url);
	}
	
	// Write a servlet to respond with JSON with all information for this page
	// Then use this function to call it, and update the values of the page.
	//
	// If updatePrices is true, updates the prices of stocks
	function update(updatePrices) {
	  document.getElementById("status").innerHTML = "Updating...";
          var url = location.origin + "/StockMarketSimulator/marketUpdate";
	  if(updatePrices)
	    url += "?prices=true";
          var xmlHttp = new XMLHttpRequest();
          xmlHttp.open( "GET", url, false ); // false for synchronous request
          xmlHttp.send( null );
	  var response = JSON.parse(xmlHttp.responseText);
	  if(response.timeout) {
	    window.location.replace(location.origin + "/StockMarketSimulator/restTimeout");
          } else if(response.error) {
	    alert("An error occurred.");
	    location.reload();
          } else {
	    document.getElementById("balance").innerHTML = response.balance.toFixed(2);
	    document.getElementById("stockValue").innerHTML = response.stockValue.toFixed(2);
	    var companies = response.companies;
	    for(var company in companies) {
	      if(companies.hasOwnProperty(company)) {
                document.getElementById(company+"price").innerHTML = companies[company].stockValue.toFixed(2);
	        document.getElementById(company+"available").innerHTML = companies[company].available;
	        document.getElementById(company+"average").innerHTML = companies[company].averagePrice.toFixed(2);
	        document.getElementById(company+"owned").innerHTML = companies[company].owned;
	      }
	    }
	  }
	  document.getElementById("status").innerHTML = "Update complete.";
	}

	update(true);

	// update stock prices every ten seconds
	setInterval(function() {
	  update(true);
	}, 10000);
	
      </script>
  </body>
</html>
<% } %>
