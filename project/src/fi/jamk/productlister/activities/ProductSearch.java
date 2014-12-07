package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Product;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import static android.content.Context.INPUT_METHOD_SERVICE;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import fi.jamk.productlister.R;

public class ProductSearch extends Activity implements OnClickListener, AdapterView.OnItemClickListener {

	private DBConnector db;
	private ArrayList<Product> productlist;
	private Product selectedProduct;
	private ListView listViewProducts;
	private ProgressDialog progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_search);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		db = new DBConnector();
		productlist = new ArrayList<Product>();
		selectedProduct = null;

		listViewProducts = (ListView) findViewById(R.id.product_search_listview);
		listViewProducts.setOnItemClickListener(this);

		Button searchButton = (Button) findViewById(R.id.product_search_search);
		searchButton.setOnClickListener(this);

		Button addProductButton = (Button) findViewById(R.id.product_search_add_product);
		addProductButton.setOnClickListener(this);

		Button addShopButton = (Button) findViewById(R.id.product_search_add_shop);
		addShopButton.setOnClickListener(this);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		progress = new ProgressDialog(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.product_search_search:
				clearFocus();
				searchClicked();
				break;
			case R.id.product_search_add_product:
				startActivity(new Intent(this, ProductAdd.class));
				break;
			case R.id.product_search_add_shop:
				startActivity(new Intent(this, ShopAdd.class));
				break;
		}
	}

	private void searchClicked() {

		EditText textField = (EditText) findViewById(R.id.product_search_textfield);
		String keyword = textField.getText().toString();
		if (keyword.length() < 1) {
			Toast.makeText(getApplicationContext(), "Give a proper keyword.", Toast.LENGTH_SHORT).show();
			return;
		}
		progress.setIndeterminate(true);
		progress.setMessage("Searching..");
		progress.show();
		SearchProductsTask search = new SearchProductsTask();
		search.execute(keyword);

	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.product_search_listview) {
			selectedProduct = (Product) parent.getItemAtPosition(position);
			listViewProducts.setItemChecked(position, true);

			Intent intent = new Intent(this, ProductPrices.class);
			intent.putExtra("selectedProductId", selectedProduct.getProductId());
			intent.putExtra("selectedProductName", selectedProduct.getProductName());

			startActivity(intent);
		}
	}

	private class SearchProductsTask extends AsyncTask<String, Void, ArrayList<Product>> {

		@Override
		protected ArrayList<Product> doInBackground(String... keyword) {
			return db.searchProducts(keyword[0], 0);
		}

		@Override
		protected void onPostExecute(ArrayList<Product> list) {
			Toast.makeText(getApplicationContext(), "Found " + list.size() + " products.", Toast.LENGTH_SHORT).show();
			// TODO stop displaying other progress dialog
			productlist = list;

			//TODO custom list item with more info?
			ArrayAdapter<Product> newadapter = new ArrayAdapter<Product>(ProductSearch.this,
					android.R.layout.simple_list_item_1, productlist);
			listViewProducts.setAdapter(newadapter);
			progress.hide();
		}
	}

	private void clearFocus() {
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
