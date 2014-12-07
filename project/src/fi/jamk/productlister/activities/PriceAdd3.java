package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import android.app.Activity;
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

		db = new DBConnector();

		Intent intent = getIntent();
		selectedProductId = intent.getIntExtra("selectedProductId", 0);
		selectedShopId = intent.getIntExtra("selectedShopId", 0);
		selectedProductName = intent.getStringExtra("selectedProductName");
		selectedShopName = intent.getStringExtra("selectedShopName");
		
		selectedProductTextView.setText(selectedProductName);
		selectedShopTextView.setText(selectedShopName);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.price_step_3_add:
				addPrice();
				break;
		}
	}

	private void addPrice() {
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
			AddProductTask task = new AddProductTask();
			task.execute(new Price(0, selectedShopId, selectedProductId, unitPrice, quantityPrice));

			JSONArray result = task.get();

			if (result.getJSONObject(0).getString("success").equals("0")) {
				Toast.makeText(getApplicationContext(), result.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Added price for: " + selectedProductName, Toast.LENGTH_SHORT).show();
				// TODO move to product prices or what? main activity for now.
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back button
				startActivity(intent);
			}
		} catch (Exception ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private class AddProductTask extends AsyncTask<Price, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Price... params) {
			JSONArray result = db.addPrice(params[0]);
			return result;
		}
	}
}
