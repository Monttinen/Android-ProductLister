package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import fi.jamk.productlister.R;
import fi.jamk.productlister.model.Shop;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdd2 extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

	private EditText shopName;
	private ListView listViewShops;

	private DBConnector db;
	private ArrayList<Shop> shops;
	private Shop selectedShop;

	private int selectedProductId;
	private String selectedProductName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price2);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button nextStep2 = (Button) findViewById(R.id.price_next_step_2);
		Button search = (Button) findViewById(R.id.price_step_2_search);
		TextView selectedProductText = (TextView) findViewById(R.id.price_step_2_selected_product_text);
		
		shopName = (EditText) findViewById(R.id.searchShop2);
		listViewShops = (ListView) findViewById(R.id.shopList);
		
		nextStep2.setOnClickListener(this);
		search.setOnClickListener(this);
		listViewShops.setOnItemClickListener(this);
		
		db = new DBConnector();
		shops = new ArrayList<Shop>();
		selectedShop = null;

		Intent intent = getIntent();
		selectedProductId = intent.getIntExtra("selectedProductId", 0);
		selectedProductName = intent.getStringExtra("selectedProductName");
		
		selectedProductText.setText(selectedProductName);
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
			case R.id.price_next_step_2:
				nextStep2();
				break;
			case R.id.price_step_2_search:
				searchShops();
				break;
		}
	}

	private void searchShops() {
		String keyword = shopName.getText().toString();
		SearchShopsTask task = new SearchShopsTask();
		task.execute(keyword);

		try {
			shops = task.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(PriceAdd2.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(PriceAdd2.class.getName()).log(Level.SEVERE, null, ex);
		}

		ArrayAdapter<Shop> newadapter = new ArrayAdapter<Shop>(PriceAdd2.this,
				android.R.layout.simple_list_item_1, shops);
		listViewShops.setAdapter(newadapter);
	}

	private void nextStep2() {
		if (selectedShop == null || selectedProductId < 1) {
			return;
		}
		Intent intent = new Intent(this, PriceAdd3.class);
		intent.putExtra("selectedProductId", selectedProductId);
		intent.putExtra("selectedProductName", selectedProductName);
		intent.putExtra("selectedShopId", selectedShop.getShopId());
		intent.putExtra("selectedShopName", selectedShop.getShopName());

		startActivity(intent);
	}

	private class SearchShopsTask extends AsyncTask<String, Void, ArrayList<Shop>> {

		@Override
		protected ArrayList<Shop> doInBackground(String... keyword) {
			try {
				return db.searchShops(keyword[0]);
			} catch (Exception ex) {
				Logger.getLogger(PriceAdd2.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(parent.getId() == R.id.shopList){
			selectedShop = (Shop) parent.getItemAtPosition(position);
			listViewShops.setItemChecked(position, true);
		}
	}
}
