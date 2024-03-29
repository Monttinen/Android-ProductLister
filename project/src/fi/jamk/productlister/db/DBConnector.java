package fi.jamk.productlister.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import fi.jamk.productlister.model.Category;
import fi.jamk.productlister.model.Product;
import fi.jamk.productlister.model.Price;
import fi.jamk.productlister.model.Shop;
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

/**
 * A connector class that uses JSON queries to communicate with the backend system.
 */
public class DBConnector {
	
	// URL holders for server addresses
	private String server;
	private String imageServer;

	/**
	 * Constructor that sets server address.
	 */
	public DBConnector() {
		server = "http://128.199.60.131:8080/data/";
		imageServer = "http://128.199.60.131/img/";
	}

	/**
	 * *
	 * Get ArrayList of Products from database based on keyword. categoryId is
	 * ignored if < 1
	 *
	 * @param keyword
	 * @param categoryId
	 * @return ArrayList<Product>
	 */
	public ArrayList<Product> searchProducts(String keyword, int categoryId) {
		ArrayList<Product> results = new ArrayList<Product>();
		if (keyword.length() < 1) {
			return results;
		}
		if (categoryId < 1) {
			categoryId = 0;
		}
		String responseString;

		// Get data string
		String url = server + "products?keyword=" + keyword + "&categoryId=" + categoryId;
		responseString = getPage(url);

		// parse json and return arraylist
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getString("success").equals("1")) {
				JSONArray products = json.getJSONArray("products");
				for (int i = 0; i < products.length(); i++) {
					JSONObject p = products.getJSONObject(i);

					// Some of these can be null!
					int id = p.getInt("productId");
					int catId = p.getInt("productCategoryId");
					String name = p.getString("productName");
					String bar = p.getString("productBarcode");

					results.add(new Product(id, catId, name, bar));
				}
			}
		} catch (JSONException e) {
			Log.e("DBConnector", "Error parsing JSON", e);
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
			if (json.getString("success").equals("1")) {
				JSONArray products = json.getJSONArray("categories");
				for (int i = 0; i < products.length(); i++) {
					JSONObject p = products.getJSONObject(i);

					// Some of these can be null!
					int id = p.getInt("categoryId");
					String name = p.getString("categoryName");
					String pId = p.getString("categoryParentId");
					String desc = p.getString("categoryDescription");
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
			Log.e("DBConnector", "Error parsing JSON", e);
		}
		return results;
	}
	
	/**
	 * Gets a list of prices based on parameters.
	 * Both can be defined but at least one of them is needed.
	 * @param shopId
	 * @param productId
	 * @return price list
	 * @throws Exception 
	 */
	public ArrayList<Price> getPrices(int shopId, int productId) throws Exception {
		ArrayList<Price> results = new ArrayList<Price>();
		String responseString;

		if (shopId <= 0 && productId <= 0) {
			throw new Exception("shopId and productId cannot both be empty");
		}

		responseString = getPage(server + "prices?shopId=" + shopId + "&productId=" + productId);

		// parse json and return arraylist
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getString("success").equals("1")) {
				JSONArray prices = json.getJSONArray("prices");
				for (int i = 0; i < prices.length(); i++) {
					JSONObject p = prices.getJSONObject(i);

					// Some of these can be < 0.0 !
					int id = p.getInt("idPrice");
					int shopIdOut = p.getInt("shopId");
					int productIdOut = p.getInt("productId");
					double unitPrice = p.getDouble("unitPrice");
					double quantityPrice = p.getDouble("quantityPrice");

					results.add(new Price(id, shopIdOut, productIdOut, unitPrice, quantityPrice));
				}
			}
		} catch (JSONException e) {
			Log.e("DBConnector", "Error parsing JSON", e);
		}
		return results;
	}
	
	/**
	 * Searches for shops based on keyword.
	 * @param keyword
	 * @return a list of shops
	 * @throws Exception 
	 */
	public ArrayList<Shop> searchShops(String keyword) throws Exception {
		ArrayList<Shop> results = new ArrayList<Shop>();
		String responseString;

		if (keyword.length() < 1) {
			throw new Exception("Need a keyword.");
		}

		responseString = getPage(server + "searchshops?keyword=" + keyword);

		// parse json and return arraylist
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getString("success").equals("1")) {
				JSONArray shops = json.getJSONArray("shops");
				for (int i = 0; i < shops.length(); i++) {
					JSONObject s = shops.getJSONObject(i);

					// Some of these can be < 0.0 !
					int id = s.getInt("shopId");
					String name = s.getString("shopName");
					String address = s.getString("shopAddress");
					String location = s.getString("shopLocation");

					results.add(new Shop(id, name, address, location));
				}
			}
		} catch (JSONException e) {
			Log.e("DBConnector", "Error parsing JSON", e);
		}
		return results;
	}
	
	/**
	 * Gets a shop based on shopId
	 * @param shopId
	 * @return Shop
	 */
	public Shop getShop(int shopId) {
		Shop result = null;
		String responseString;
		responseString = getPage(server + "shop?shopId=" + shopId);

		JSONObject json;
		try {
			json = new JSONObject(responseString);
			if (json.getString("success").equals("1")) {
				JSONObject s = json.getJSONObject("shop");

				// Some of these can be < 0.0 !
				int id = s.getInt("shopId");
				String name = s.getString("shopName");
				String address = s.getString("shopAddress");
				String location = s.getString("shopLocation");

				result = new Shop(id, name, address, location);
			}
		} catch (JSONException e) {
			Log.e("DBConnector", "Error parsing JSON", e);
		}
		return result;
	}
	
	/**
	 * Attempts to add a product to database
	 * @param p
	 * @return JSONArray with added product productId or a message if adding failed
	 */
	public JSONArray addProduct(Product p) {
		JSONArray result = new JSONArray();

		try {
			JSONObject json = new JSONObject();
			json.put("productName", p.getProductName());
			json.put("productCategoryId", p.getProductCategoryId());
			json.put("productBarcode", p.getProductBarcode());

			result = makeRequest(server + "addproduct", json);

		} catch (JSONException ex) {
			Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Attempts to add a shop to the database.
	 * @param s
	 * @return JSONArray with the added shop shopId or a message if adding failed
	 */
	public JSONArray addShop(Shop s) {
		JSONArray result = new JSONArray();

		try {
			JSONObject json = new JSONObject();
			json.put("shopName", s.getShopName());
			json.put("shopAddress", s.getShopAdddress());
			json.put("shopLocation", s.getShopLocation());

			result = makeRequest(server + "addshop", json);

		} catch (JSONException ex) {
			Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Attempts to add a price to the database.
	 * @param p
	 * @return JSONArray with the added price priceId or a message if adding failed
	 */
	public JSONArray addPrice(Price p) {
		JSONArray result = new JSONArray();

		try {
			JSONObject json = new JSONObject();
			json.put("shopId", p.getShopId());
			json.put("productId", p.getProductId());
			json.put("unitPrice", p.getUnitPrice());
			json.put("quantityPrice", p.getQuantityPrice());

			result = makeRequest(server + "addprice", json);

		} catch (JSONException ex) {
			Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * A method for getting a page with HTTP GET
	 * @param url
	 * @return the page as a String
	 */
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
			Log.e("DBConnector", "Malformed URL: ", e);
		} catch (IOException e) {
			Log.e("DBConnector", "IO Exception: ", e);
		}
		return null;
	}

	/**
	 * Makes a request to the backend.
	 * @param path URL to send the request to
	 * @param json a JSON object for the request
	 * @return JSONArray that contains the response
	 * @throws Exception 
	 */
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
	
	/**
	 * Gets a string from an input stream
	 * @param in input stream
	 * @return string
	 */
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
			Log.e("DBConnector", "IO Exception: ", e);
		}

		return stringBuilder.toString();
	}
	
	/**
	 * Attempts to add a image for a product.
	 * @param image bitmap of the image being added
	 * @param productId the productId of the product
	 * @return JSONArray containing the productId of the product or a message 
	 * if adding image failed
	 */
	public JSONArray addProductImage(Bitmap image, int productId) {
		JSONArray result = new JSONArray();
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 85, stream);
			byte[] imageBytes = stream.toByteArray();
			String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

			JSONObject json = new JSONObject();
			json.put("productId", productId);
			json.put("productImage", imageString);

			result = makeRequest(server + "addproductimage", json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Gets the product image as a bitmap from the server
	 * @param productId the product's productId
	 * @return bitmap 
	 */
	public Bitmap getProductImage(int productId) {
		try {
			URL url = new URL(imageServer + productId + ".jpg");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
