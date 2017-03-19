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
          xmlHttp.open( "POST", url, false );
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
            document.getElementById("status").innerHTML = "Transaction failed.";
	    reloadPage();
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

	function reloadPage() {
	  alert("An error occurred. Please reload the stock market.");
          location.reload();
	}

	function sendToDashboard() {
	  var url = location.origin + "/StockMarketSimulator/restDashboard";
	  window.location.replace(url);
	}
	
	function update() {
	  document.getElementById("status").innerHTML = "Updating...";
          var url = location.origin + "/StockMarketSimulator/restful/stocks/" + <%= "\"" + user.getName() + "\"" %>;
          var xmlHttp = new XMLHttpRequest();
          xmlHttp.open( "GET", url, false );
          xmlHttp.send( null );
	  if(xmlHttp.status !== 200) {
	    reloadPage();
	  } else {
            var companies = JSON.parse(xmlHttp.responseText);
	    for(var symbol in companies) {
	      var company = companies[symbol];
              document.getElementById(symbol+"price").innerHTML = company.price.toFixed(2);
              updateCompany(symbol, company.available, company.averagePurchasePrice, company.stocks);
	    }
	  }
	  document.getElementById("status").innerHTML = "Update Complete.";
	}

	update();

	// update stock prices every ten seconds
	setInterval(function() {
	  update();
	}, 10000);
	
      </script>
  </body>
</html>
<% } %>
