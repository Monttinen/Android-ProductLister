package fi.jamk.productlister;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.*;

public class DBConnector {

	private String server;

	/**
	 * *
	 * Constructor that sets server address.
	 */
	public DBConnector() {
		server = "http://128.199.60.131:8080/data/";
	}

	/**
	 * *
	 * Get ArrayList of Products from database based on keyword.
	 *
	 * @param keyword
	 * @return ArrayList<Product>
	 */
	public ArrayList<Product> searchProducts(String keyword) {
		ArrayList<Product> results = new ArrayList<Product>();
		if (keyword.length() < 1) {
			return results;
		}

		String responseString;

		// Get data string
		String url = server + "products?keyword=" + keyword;
		responseString = getPage(url);

		// parse json and return arraylist
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getString("Success").equals("1")) {
				JSONArray products = json.getJSONArray("Products");
				for (int i = 0; i < products.length(); i++) {
					JSONObject p = products.getJSONObject(i);

					// Some of these can be null!
					int id = p.getInt("idProduct");
					int catId = p.getInt("ProductCategoryId");
					String name = p.getString("ProductName");
					String bar = p.getString("ProductBarcode");

					results.add(new Product(id, catId, name, bar));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * *
	 * Get ArrayList of Categories from database
	 *
	 * @return ArrayList<Category>
	 */
	public ArrayList<Category> getCategories() {
		return getCategories(0);
	}

	/**
	 * *
	 * Get ArrayList of Categories from database based on CategoryParentId
	 *
	 * @param categoryParentId
	 * @return ArrayList<Category>
	 */
	public ArrayList<Category> getCategories(int categoryParentId) {
		ArrayList<Category> results = new ArrayList<Category>();
		String responseString;

		// Get data string
		String url;
		if (categoryParentId > 0) {
			url = server + "category?categoryId=" + categoryParentId;
		} else {
			url = server + "category";
		}
		responseString = getPage(url);

		// parse json and return arraylist
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getString("Success").equals("1")) {
				JSONArray products = json.getJSONArray("Categories");
				for (int i = 0; i < products.length(); i++) {
					JSONObject p = products.getJSONObject(i);

					// Some of these can be null!
					int id = p.getInt("idCategory");
					String name = p.getString("CategoryName");
					String pId = p.getString("CategoryParentId");
					String desc = p.getString("CategoryDescription");
					int parentId;
					try {
						parentId = Integer.parseInt(pId);
					} catch (NumberFormatException e) {
						parentId = 0;
					}
					results.add(new Category(id, name, parentId, desc));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	public JSONArray addProduct(Product p) {
		JSONArray result = new JSONArray();

		try {
			JSONObject json = new JSONObject();
			json.put("ProductName", p.getProductName());
			json.put("ProductCategoryId", p.getProductCategoryId());
			json.put("ProductBarcode", p.getProductBarcode());

			result = makeRequest(server+"addproduct", json);

		} catch (JSONException ex) {
			Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
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

	public JSONArray makeRequest(String path, JSONObject json) throws Exception {
		//instantiates httpclient to make request
		DefaultHttpClient httpclient = new DefaultHttpClient();

		//url with the post data
		HttpPost httpost = new HttpPost(path);

		//passes the results to a string builder/entity
		StringEntity se = new StringEntity(json.toString());

		//sets the post request as the resulting string
		httpost.setEntity(se);
		//sets a request header so the page receving the request
		//will know what to do with it
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");

		//Handles what is returned from the page 
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpost, responseHandler);

		JSONArray result = new JSONArray();
		result.put(new JSONObject(responseBody));
		return result;
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
