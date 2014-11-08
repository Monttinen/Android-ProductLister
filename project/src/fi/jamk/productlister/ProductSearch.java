package fi.jamk.productlister;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProductSearch extends Activity {

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

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText textField = (EditText) findViewById(R.id.product_search_textfield);
				String keyword = textField.getText().toString();

				SearchProducts search = new SearchProducts();
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
				ArrayAdapter<Product> newadapter = new ArrayAdapter<Product>(ProductSearch.this,
						android.R.layout.simple_list_item_1, productlist);
				listViewProducts.setAdapter(newadapter);

			}
		});

	}

	private class SearchProducts extends AsyncTask<String, Void, ArrayList<Product>> {

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
