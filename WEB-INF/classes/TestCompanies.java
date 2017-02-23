import java.util.Random;

public class TestCompanies {
    private static Company[] companies = createCompanies();
    private static boolean initialized = false;
    private static double nextUpdateTime = calculateNextUpdateTime();
    
    public static Company[] getCompanies() {
	updateCompanies();
	return companies;
    }

    public static boolean updateCompanies() {
	double time = System.currentTimeMillis();
	if(time - nextUpdateTime >= 0) {
	    for(int i=0; i<companies.length; ++i) {
		double price = getRandomPrice();
		companies[i].updatePrice(price);
	    }
	    nextUpdateTime = calculateNextUpdateTime();
	    return true;
	} else {
	    return false;
	}
    }

    private static Company[] createCompanies() {
	Company[] comps = new Company[20];
	for(int i=0; i<comps.length; ++i) {
	    String name = "Company" + (i+1);
	    double value = getRandomPrice();
	    comps[i] = new Company(name, value);
	}
	return comps;
    }

    public static double timeToNextUpdate() {
	return nextUpdateTime - System.currentTimeMillis();
    }

    public static double calculateNextUpdateTime() {
	double time = System.currentTimeMillis();
	double minutes = time / 60000;
        return (minutes+1)*60000;
    }

    private static double getRandomPrice() {
	Random r = new Random();
	double price = (r.nextDouble() * 99) + 1;
	return price;
    }
}
