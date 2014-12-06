package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Product;
import fi.jamk.productlister.model.Price;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import fi.jamk.productlister.R;
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
public class PriceAdd extends Activity implements View.OnClickListener {

	private DBConnector db;
	private List<Price> priceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price1);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button nextStep = (Button) findViewById(R.id.price_next_step_1);
		nextStep.setOnClickListener(this);

		db = new DBConnector();
		// TODO 1. fill category spinners

		// TODO 2. implement search
		// TODO 3. implement filling product list
		// TODO 4. implement product selection and navigation to next step
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;
	}

	private class GetProductPricesTask extends AsyncTask<Product, Void, ArrayList<Price>> {

		@Override
		protected ArrayList<Price> doInBackground(Product... params) {
			ArrayList<Price> result = null;
			try {
				result = db.getPrices(0, params[0].getProductId());
			} catch (Exception ex) {
				Logger.getLogger(PriceAdd.class.getName()).log(Level.SEVERE, null, ex);
			}
			return result;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.price_next_step_1:
				nextStep1();
				break;
		}
	}

	private void nextStep1() {
		// TODO Check that a product has been selected.
		startActivity(new Intent(this, PriceAdd2.class));
	}

}
