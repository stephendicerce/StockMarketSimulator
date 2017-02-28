package simulator;

import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class StockReader {
    private static final String BASE_URL = "http://finance.google.com/finance/info?client=ig&q=NASDAQ:";

    public static void updateStocks(String[] symbols) {
	String urlString = BASE_URL;
	URL url = null;
	HttpURLConnection connection = null;
	JSONArray arr = null;
	for(String sym : symbols)
	    urlString += sym + ",";
	try {
	    url = new URL(urlString);
	    connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "text/plain");

	    connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()
                    )
            );
            
            String inputLine;
            String response = "";
            while ((inputLine = in.readLine()) != null) {
                response = response + inputLine + "\n";
            }
            in.close();
            if(response == null) {
		return;
	    }
	    // the url returns two '/' characters at the start.
	    // Remove them for valid JSON.
	    response = response.substring(3);
	    arr = new JSONArray(response);
	} catch(IOException e) {
	    return;
	} finally {
	    connection.disconnect();
	}

	if(arr != null) {
	    int length = arr.length();
	    for(int i=0; i<length; ++i) {
		JSONObject obj = arr.getJSONObject(i);
		String sym = obj.getString("t");
		double price = Double.parseDouble(obj.getString("l"));
		Connection conn = null;
		Statement statement = null;
		try {
		    conn = DBConnector.getConnection();
		    statement = conn.createStatement();
		    statement.executeUpdate("UPDATE Companies SET stockValue=" + price
					    + " WHERE symbol='" + sym + "';");
		} catch(SQLException | NullPointerException e) {
		    
		} finally {
		    try { statement.close(); } catch(SQLException | NullPointerException e) { }
		    try { conn.close(); } catch(SQLException | NullPointerException e) { }
		}
	    }
	}
    }
}
