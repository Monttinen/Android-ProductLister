package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fi.jamk.productlister.R;
import fi.jamk.productlister.model.Price;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdd3 extends Activity implements View.OnClickListener {

	private int selectedProductId;
	private int selectedShopId;
	private String selectedProductName;
	private String selectedShopName;

	private EditText unitPriceTextField;
	private EditText quantityPriceTextField;
	private TextView selectedProductTextView;
	private TextView selectedShopTextView;
	private ProgressDialog progress;

	private DBConnector db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price3);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button addPrice = (Button) findViewById(R.id.price_step_3_add);
		addPrice.setOnClickListener(this);
		unitPriceTextField = (EditText) findViewById(R.id.product_add_unit_price);
		quantityPriceTextField = (EditText) findViewById(R.id.product_add_quantity_price);
		selectedProductTextView = (TextView) findViewById(R.id.product_add_price_view);
		selectedShopTextView = (TextView) findViewById(R.id.product_add_price_shop_view);

		progress = new ProgressDialog(this);

		db = new DBConnector();

		Intent intent = getIntent();
		selectedProductId = intent.getIntExtra("selectedProductId", 0);
		selectedShopId = intent.getIntExtra("selectedShopId", 0);
		selectedProductName = intent.getStringExtra("selectedProductName");
		selectedShopName = intent.getStringExtra("selectedShopName");

		selectedProductTextView.setText(selectedProductName);
		selectedShopTextView.setText(selectedShopName);
	}
	
	/**
	 * Used for navigating back from the action bar.
	 * Navigates back to main activity.
	 * @param item
	 * @return 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;
	}
	
	/**
	 * Listener for buttons.
	 * @param v 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.price_step_3_add:
				addPrice();
				break;
		}
	}
	
	/**
	 * Starts the price adding.
	 */
	private void addPrice() {
		progress.setIndeterminate(true);
		progress.setMessage("Adding product");
		progress.show();

		double unitPrice;
		double quantityPrice;
		try {
			unitPrice = Double.parseDouble(unitPriceTextField.getText().toString());
		} catch (NumberFormatException e) {
			unitPrice = 0.0;
		}
		try {
			quantityPrice = Double.parseDouble(quantityPriceTextField.getText().toString());
		} catch (NumberFormatException e) {
			quantityPrice = 0.0;
		}

		if (selectedProductId < 1 || selectedShopId < 1 || !(unitPrice > 0.0 || quantityPrice > 0.0)) {
			return;
		}

		try {
			AddPriceTask task = new AddPriceTask();
			task.execute(new Price(0, selectedShopId, selectedProductId, unitPrice, quantityPrice));
			Intent intent = new Intent(this, ProductPrices.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back button
			intent.putExtra("selectedProductId", selectedProductId);
			intent.putExtra("selectedProductName", selectedProductName);
			startActivity(intent);
		} catch (Exception ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * AsyncTask for adding a price to the database.
	 */
	private class AddPriceTask extends AsyncTask<Price, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Price... params) {
			JSONArray result = db.addPrice(params[0]);
			return result;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			try {
				if (result.getJSONObject(0).getString("success").equals("0")) {
					progress.hide();
					Toast.makeText(getApplicationContext(), result.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
				} else {
					progress.hide();

				}
			} catch (JSONException ex) {
				Logger.getLogger(PriceAdd3.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
