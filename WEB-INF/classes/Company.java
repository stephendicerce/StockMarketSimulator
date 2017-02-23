public class Company {
    private String name;
    private double stockValue;
    private int stocksAvailable;
	
    public Company(String n, double v) {
	name = n;
	stockValue = v;
	stocksAvailable = 10;
    }

    public String getName() {
	return name;
    }

    public double getStockValue() {
	return stockValue;
    }

    public int getNumberOfAvailableStocks() {
	return stocksAvailable;
    }

    public boolean buyStock() {
	if(stocksAvailable > 0) {
	    stocksAvailable--;
	    return true;
	}
	return false;
    }

    public boolean sellStock() {
	stocksAvailable++;
	return true;
    }

    public void updatePrice(double v) {
	stockValue = v;
    }
    
}
