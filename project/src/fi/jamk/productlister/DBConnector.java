package fi.jamk.productlister;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.*;

public class DBConnector {
	private String server;

	public DBConnector() {
		server = "http://128.199.60.131/";
	}

	public ArrayList<Product> searchProducts(String keyword) {
		ArrayList<Product> results = new ArrayList<Product>();
		if (keyword.length() < 1) {
			return results;
		}

		String responseString;

		// Get data string
		String url = server + "searchproduct.php?keyword=" + keyword;
		responseString = getPage(url);

		// parse json and return arraylist
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getBoolean("Success") == true) {
				JSONArray products = json.getJSONArray("Products");
				for (int i = 0; i < products.length(); i++) {
					JSONObject p = products.getJSONObject(i);
					results.add(new Product(p.getInt("idProduct"), p
							.getInt("ProductCategoryId"), p
							.getString("ProductName"), p
							.getString("ProductBarcode")));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	private String getPage(String url) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.connect();

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return inputStreamToString(con.getInputStream());
			} else {
				return null;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String inputStreamToString(InputStream in) {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		try {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
}
