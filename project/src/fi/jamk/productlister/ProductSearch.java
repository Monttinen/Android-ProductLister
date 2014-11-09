package fi.jamk.productlister;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProductSearch extends Activity implements OnClickListener {

	private DBConnector db;
	private ArrayList<Product> productlist;
	ListView listViewProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_search);
		db = new DBConnector();
		productlist = new ArrayList<Product>();
		listViewProducts = (ListView) findViewById(R.id.product_search_listview);

		Button searchButton = (Button) findViewById(R.id.product_search_search);
		searchButton.setOnClickListener(this);

		Button addProductButton = (Button) findViewById(R.id.product_search_add_product);
		addProductButton.setOnClickListener(this);

		Button addShopButton = (Button) findViewById(R.id.product_search_add_shop);
		addShopButton.setOnClickListener(this);

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
				searchClicked();
				break;
			case R.id.product_search_add_product:
				startActivity(new Intent(this, ProductAdd.class));
				break;
			case R.id.product_search_add_shop:
				// TODO
				break;
		}
	}

	private void searchClicked() {

		EditText textField = (EditText) findViewById(R.id.product_search_textfield);
		String keyword = textField.getText().toString();

		SearchProductsTask search = new SearchProductsTask();
		search.execute(keyword);

		try {
			productlist = search.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//TODO custom list item with more info?
		ArrayAdapter<Product> newadapter = new ArrayAdapter<Product>(ProductSearch.this,
				android.R.layout.simple_list_item_1, productlist);
		listViewProducts.setAdapter(newadapter);

	}

	private class SearchProductsTask extends AsyncTask<String, Void, ArrayList<Product>> {

		@Override
		protected void onPreExecute() {
			// TODO some other progress dialog?
			Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected ArrayList<Product> doInBackground(String... keyword) {
			return db.searchProducts(keyword[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<Product> list) {
			// TODO stop displaying other progress dialog
			Toast.makeText(getApplicationContext(), "Found " + list.size() + " products.", Toast.LENGTH_SHORT).show();
		}
	}
}
