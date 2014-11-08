package fi.jamk.productlister;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import org.json.*;

public class DBConnector {
	private String server;
	
	public DBConnector(){
		server = "http://128.199.60.131/";
	}
	
	public ArrayList<Product> searchProducts(String keyword) throws IOException, JSONException{
		ArrayList<Product> results = new ArrayList<Product>();
		if(keyword.length()<1){
			return results;
		}
		
		String responseString;
		
		// Get data string
		String url = server + "searchproduct.php?keyword=" + keyword;
	    responseString = getPage(url);
	    
	    // parse json and return arraylist
		JSONObject json = new JSONObject(responseString);
		if(json.getBoolean("Success")==true){
			JSONArray products = json.getJSONArray("Products");
			for(int i=0; i< products.length(); i++){
				JSONObject p = products.getJSONObject(i);
				results.add(new Product(p.getInt("idProduct"),
										p.getInt("ProductCategoryId"),
										p.getString("ProductName"),
										p.getString("ProductBarcode")
						));
			}
		}
		
		// dummy data:
		/*results.add(new Product(1, 1, "testiolut", "bar1"));
		results.add(new Product(2, 1, "testiolut2", "bar2"));
		results.add(new Product(3, 1, "testiolut3", "bar3"));
		results.add(new Product(4, 1, "testiolut4", "bar4"));*/
		
		return results;
	}
	
	private String getPage(String url) throws MalformedURLException, IOException {
	    HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
	    con.connect();

	    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        return inputStreamToString(con.getInputStream());
	    } else {
	        return null;
	    }
	}

	private String inputStreamToString(InputStream in) throws IOException {
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
	    StringBuilder stringBuilder = new StringBuilder();
	    String line = null;

	    while ((line = bufferedReader.readLine()) != null) {
	        stringBuilder.append(line + "\n");
	    }

	    bufferedReader.close();
	    return stringBuilder.toString();
	}
}
