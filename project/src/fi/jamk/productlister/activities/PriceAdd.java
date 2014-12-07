package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import fi.jamk.productlister.R;
import fi.jamk.productlister.model.Category;
import fi.jamk.productlister.model.Product;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdd extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

	private DBConnector db;
	private ArrayList<Category> categories;
	private ArrayList<Category> subCategories;
	private ArrayList<Product> products;

	private Spinner categorySpinner;
	private Spinner subCategorySpinner;
	private EditText name;
	private ListView listViewProducts;

	private Product selectedProduct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price1);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		categories = new ArrayList<Category>();
		subCategories = new ArrayList<Category>();
		products = new ArrayList<Product>();

		categorySpinner = (Spinner) findViewById(R.id.price_add_category);
		subCategorySpinner = (Spinner) findViewById(R.id.price_add_subcategory);
		Button nextStep = (Button) findViewById(R.id.price_next_step_1);
		// hide the button for now as it has no purpose
		nextStep.setVisibility(View.GONE);

		Button search = (Button) findViewById(R.id.price_step_1_search);
		listViewProducts = (ListView) findViewById(R.id.price_add_product_list);
		name = (EditText) findViewById(R.id.price_add_name_text);

		categorySpinner.setOnItemSelectedListener(this);
		listViewProducts.setOnItemClickListener(this);
		nextStep.setOnClickListener(this);
		search.setOnClickListener(this);

		listViewProducts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listViewProducts.setItemsCanFocus(true);

		db = new DBConnector();

		selectedProduct = null;

		GetCategoriesTask gategoryTask = new GetCategoriesTask();
		gategoryTask.execute();

		try {
			categories = gategoryTask.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}

		ArrayAdapter<Category> newadapter = new ArrayAdapter<Category>(PriceAdd.this,
				android.R.layout.simple_spinner_dropdown_item, categories);

		categorySpinner.setAdapter(newadapter);

		if (subCategories.size() < 1) {
			subCategorySpinner.setVisibility(View.INVISIBLE);
		} else {
			subCategorySpinner.setVisibility(View.VISIBLE);
		}
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
			case R.id.price_next_step_1:
				nextStep1();
				break;
			case R.id.price_step_1_search:
				clearFocus();
				searchProducts();
				break;
		}
	}

	private void searchProducts() {
		int categoryId = getSelectedCategory();
		String keyword = name.getText().toString();

		// clear selected product after each search
		selectedProduct = null;

		SearchProductsTask task = new SearchProductsTask();
		Object[] params = new Object[2];
		params[0] = keyword;
		params[1] = categoryId;
		task.execute(params);

		try {
			products = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayAdapter<Product> newadapter = new ArrayAdapter<Product>(PriceAdd.this,
				android.R.layout.simple_list_item_1, products);
		listViewProducts.setAdapter(newadapter);
	}

	private void nextStep1() {
		// Check that a product has been selected.
		if (selectedProduct == null) {
			return;
		}
		Intent intent = new Intent(this, PriceAdd2.class);
		intent.putExtra("selectedProductId", selectedProduct.getProductId());
		intent.putExtra("selectedProductName", selectedProduct.getProductName());
		startActivity(intent);
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		switch (parent.getId()) {
			case R.id.price_add_category:
				Category c = (Category) ((Spinner) parent).getSelectedItem();
				subCategories = getSubCategories(c.getCategoryId());
				ArrayAdapter<Category> newadapter = new ArrayAdapter<Category>(PriceAdd.this,
						android.R.layout.simple_spinner_dropdown_item, subCategories);

				subCategorySpinner.setAdapter(newadapter);
				if (subCategories.size() < 1) {
					subCategorySpinner.setVisibility(View.INVISIBLE);
				} else {
					subCategorySpinner.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.price_add_subcategory:
				break;

		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private ArrayList<Category> getSubCategories(int categoryId) {
		ArrayList<Category> result = new ArrayList<Category>();
		GetCategoriesTask gategoryTask = new GetCategoriesTask();
		gategoryTask.execute(categoryId);
		try {
			result = gategoryTask.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}

	private class GetCategoriesTask extends AsyncTask<Integer, Void, ArrayList<Category>> {

		@Override
		protected ArrayList<Category> doInBackground(Integer... params) {
			if (params != null && params.length > 0) {
				return db.getCategories(params[0]);
			} else {
				return db.getCategories();
			}
		}

	}

	private int getSelectedCategory() {
		int selectedCategory = 0;
		Category c;
		if (subCategories.size() > 0) {
			c = (Category) subCategorySpinner.getSelectedItem();
		} else {
			c = (Category) categorySpinner.getSelectedItem();
		}

		selectedCategory = c.getCategoryId();
		return selectedCategory;
	}

	private class SearchProductsTask extends AsyncTask<Object, Void, ArrayList<Product>> {

		@Override
		protected void onPreExecute() {
			// TODO some other progress dialog?
			Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected ArrayList<Product> doInBackground(Object... params) {
			String keyword = (String) params[0];
			int categoryId = (Integer) params[1];
			return db.searchProducts(keyword, categoryId);
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.price_add_product_list) {
			selectedProduct = (Product) parent.getItemAtPosition(position);
			listViewProducts.setItemChecked(position, true);
			nextStep1();
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
