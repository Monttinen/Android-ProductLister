package fi.jamk.productlister;


import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 *
 * @author Monttinen & Zamess
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

	
	private void addShop() {
		/*
		try {
			ShopName = txtShopName.getText().toString(); //setting Strings from EditTexts
			ShopAddress = txtShopAddress.getText().toString();
			ShopLocation = txtShopLocation.getText().toString();
			//String[] inputLines = input.split("\n");
			//String productName = inputLines[0];
			
			
			//TODO better validity check
			if (ShopName.length() < 1 || ShopName.length() > 255) {
				Toast.makeText(getApplicationContext(), "Shop name is not valid.", Toast.LENGTH_SHORT).show();
				return;
			}
			//TODO better validity check
			if (ShopName.length() < 1 || ShopName.length() > 255) {
				Toast.makeText(getApplicationContext(), "Shop address is not valid.", Toast.LENGTH_SHORT).show();
				return;
			}
			//TODO better validity check
			if (ShopName.length() < 1 || ShopName.length() > 255) {
				Toast.makeText(getApplicationContext(), "Shop location is not valid.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			AddShopTask task = new AddShopTask();
			task.execute(new Shop(0, ShopName, ShopAddress, ShopLocation));
			
			JSONArray result = task.get();
			if(result.getJSONObject(0).getString("success").equals("0")){
				Toast.makeText(getApplicationContext(), "Error adding shop: "+result.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Added shop: "+ShopName, Toast.LENGTH_SHORT).show();
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JSONException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
	
	*/

	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.shop_add_addbutton:
				addShop();
				break;
			

		}

	}

	public class AddShopTask extends AsyncTask<Shop, Void, JSONArray>{
		@Override
		protected JSONArray doInBackground(Shop... params) {
			JSONArray result = db.addShop(params[0]);
			return result;
		}
	}
}

