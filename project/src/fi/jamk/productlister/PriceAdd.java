/*
 */
package fi.jamk.productlister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdd extends Activity{
	
	private DBConnector db;
	private List<Price> priceList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		db = new DBConnector();
		
		// just for testing:
		GetProductPricesTask getPrices = new GetProductPricesTask();
		getPrices.execute(new Product(1, 2, "DefaultOlut", ""));
		
		try {
			priceList = getPrices.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(PriceAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(PriceAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private class GetProductPricesTask extends AsyncTask<Product, Void, ArrayList<Price>>{
		@Override
		protected ArrayList<Price> doInBackground(Product... params) {
			ArrayList<Price> result = null;
			try {
				result =  db.getPrices(0, params[0].getProductId());
			} catch (Exception ex) {
				Logger.getLogger(PriceAdd.class.getName()).log(Level.SEVERE, null, ex);
			}
			return result;
		}
	}
}