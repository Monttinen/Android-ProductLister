package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Shop;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import static android.content.Context.INPUT_METHOD_SERVICE;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fi.jamk.productlister.R;

/**
 *
 * @author Monttinen & James Pearce
 */
public class ShopAdd extends Activity implements View.OnClickListener {

	private DBConnector db;

	private EditText txtShopName;
	private EditText txtShopAddress;
	private EditText txtShopLocation;

	String ShopName;
	String ShopAddress;
	String ShopLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		db = new DBConnector();

		((Button) findViewById(R.id.shop_add_addbutton)).setOnClickListener(this);
		txtShopName = (EditText) findViewById(R.id.editTextName);
		txtShopAddress = (EditText) findViewById(R.id.editTextAddress);
		txtShopLocation = (EditText) findViewById(R.id.editTextLocation);

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
	 * A method called when pressing the add button.
	 * Validates input fields and starts the AddShopTask
	 */
	private void addShop() {
		//setting Strings from EditTexts
		ShopName = txtShopName.getText().toString(); 
		ShopAddress = txtShopAddress.getText().toString();
		ShopLocation = txtShopLocation.getText().toString();

		if (ShopName.length() < 1 || ShopName.length() > 255) {
			Toast.makeText(getApplicationContext(), "Shop name is not valid.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ShopName.length() < 1 || ShopName.length() > 255) {
			Toast.makeText(getApplicationContext(), "Shop address is not valid.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ShopName.length() < 1 || ShopName.length() > 255) {
			Toast.makeText(getApplicationContext(), "Shop location is not valid.", Toast.LENGTH_SHORT).show();
			return;
		}

		AddShopTask task = new AddShopTask();
		task.execute(new Shop(0, ShopName, ShopAddress, ShopLocation));
		txtShopAddress.setText("");
		txtShopLocation.setText("");
		txtShopName.setText("");
	}
	
	/**
	 * OnClickListener for the add button.
	 * @param v 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.shop_add_addbutton:
				clearFocus();
				addShop();
				break;

		}

	}
	
	/**
	 * AsyncTask for adding a shop.
	 */
	public class AddShopTask extends AsyncTask<Shop, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Shop... params) {
			JSONArray result = db.addShop(params[0]);
			return result;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			try {
				if (result.getJSONObject(0).getString("success").equals("0")) {
					Toast.makeText(getApplicationContext(), "Error adding shop: " + result.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Added shop: " + ShopName, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException ex) {
				Logger.getLogger(ShopAdd.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * A method for clearing the focus. For example used for removing the
	 * virtual keyboard when a button has been pressed.
	 */
	private void clearFocus() {
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			Log.e("ShopAdd", "Could not clear focus.", e);
		}
	}
}
