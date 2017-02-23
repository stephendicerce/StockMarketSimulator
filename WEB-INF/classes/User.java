public class User {
    private String name;
    private double money;
    private Company[] companies;
    private int[] stocks;

    public User(String n, double m) {
	name = n;
	money = m;
	companies = TestCompanies.getCompanies();
	stocks = new int[companies.length];
	for(int i=0; i<stocks.length; ++i) {
	    stocks[i] = 0;
	}
    }

    public int getNumberOfStocks(String companyName) {
	for(int i=0; i<companies.length; ++i) {
	    if(companies[i].getName().equals(companyName)) {
		return stocks[i];
	    }
	}
	return 0;
    }

    public String getName() {
	return name;
    }

    public double getMoney() {
	return money;
    }

    public boolean purchaseStock(String companyName) {
	for(int i=0; i<companies.length; ++i) {
	    Company comp = companies[i];
	    if(comp.getName().equals(companyName)) {
		double price = comp.getStockValue();
		if(price <= money && comp.buyStock()) {
		    money -= price;
		    ++stocks[i];
		    return true;
		} else {
		    return false;
		}
	    }
	}
	return false;
    }

    public boolean sellStock(String companyName) {
	for(int i=0; i<companies.length; ++i) {
	    Company comp = companies[i];
	    if(comp.getName().equals(companyName)) {
		double price = comp.getStockValue();
		if(getNumberOfStocks(comp.getName()) > 0 && comp.sellStock()) {
		    money += price;
		    if(--stocks[i] < 0)
			stocks[i] = 0;
		    return true;
		} else {
		    return false;
		}
	    }
	}
	return false;
    }

    public double getStockValue() {
	double total = 0;
	for(int i=0; i<stocks.length; ++i) {
	    int numStocks = stocks[i];
	    if(numStocks > 0) {
		total += numStocks*companies[i].getStockValue();
	    }
	}
	return total;
    }

}
