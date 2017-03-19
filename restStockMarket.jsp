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
	   String csym = c.getSymbol();
           int owned = user.getNumberOfStocks(cname);
      %>
	<li>
	  <b> <% out.print(cname); %> </b>
	  &nbsp&nbsp&nbsp Stock Price:
	  <span id=<% out.print("\"" + csym + "price\""); %> >
	  </span>
	  <br/>
	  Number Available:
	  <span id=<% out.print("\"" + csym + "available\""); %> >
	  </span>
	  &nbsp&nbsp&nbsp Average Purchased Price:
	  <span id=<% out.print("\"" + csym + "average\""); %> >
	  </span>
	  <br/> Number Owned:
	  <span id=<% out.print("\"" + csym + "owned\""); %> >
	  </span>
	  <br/>
	  <button OnClick='act(<% out.print("\"" + csym + "\""); %>, "buy")'>Purchase</button>
	  <button OnClick='act(<% out.print("\"" + csym + "\""); %>, "sell")'>Sell</button><br/>
	</li>
	<% } %>
      </ol>
      
      <script>
	function act(company, action) {
	  document.getElementById("status").innerHTML = "Requesting transaction...";
	  var url = location.origin + "/StockMarketSimulator/restful/stocks/" + <%= "\"" + user.getName() + "\"" %> + "/" + company;
	  var body = {};
	  body.action = action;
          var xmlHttp = new XMLHttpRequest();
          xmlHttp.open( "POST", url, false ); // false for synchronous request
	  xmlHttp.setRequestHeader("Content-Type", "application/json");
          xmlHttp.send( JSON.stringify(body) );
	  var responseText = xmlHttp.responseText;
	  var response = JSON.parse(responseText);
          var status = response.status;
          if(status == "success") {
	    updateBalance(response.balance, response.stockValue);
            updateCompany(company, response.available, response.averagePurchasePrice, response.owned);
	    document.getElementById("status").innerHTML = "Transaction successful!";
	  } else if(status == "Failed") {
            alert("The transaction could not be completed.");
            document.getElementById("status").innerHTML = "Transaction failed.";
	  } else {
	    alert("An error occurred. Please reload the stock market.");
            document.getElementById("status").innerHTML = "Transaction failed.";
            location.reload();
	  }
	}

	function updateBalance(balance, stockValue) {
	  document.getElementById("balance").innerHTML = balance.toFixed(2);
	  document.getElementById("stockValue").innerHTML = stockValue.toFixed(2);
	}

	function updateCompany(company, available, average, owned) {
	  document.getElementById(company + "available").innerHTML = available;
	  document.getElementById(company + "average").innerHTML = average.toFixed(2);
	  document.getElementById(company + "owned").innerHTML = owned;
	}

	function sendToDashboard() {
	  var url = location.origin + "/StockMarketSimulator/restDashboard";
	  window.location.replace(url);
	}
	
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
