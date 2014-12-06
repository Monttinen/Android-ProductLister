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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import fi.jamk.productlister.R;
import fi.jamk.productlister.model.Category;
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
public class PriceAdd extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	private DBConnector db;
	private ArrayList<Category> categories;
	private ArrayList<Category> subCategories;
	private Spinner categorySpinner;
	private Spinner subCategorySpinner;
	private TextView name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price1);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		categories = new ArrayList<Category>();
		subCategories = new ArrayList<Category>();
		
		categorySpinner = (Spinner) findViewById(R.id.price_add_category);
		subCategorySpinner = (Spinner) findViewById(R.id.price_add_subcategory);
		Button nextStep = (Button) findViewById(R.id.price_next_step_1);
		name = (TextView) findViewById(R.id.price_add_name_text);

		categorySpinner.setOnItemSelectedListener(this);
		nextStep.setOnClickListener(this);

		db = new DBConnector();
		// TODO 1. fill category spinners
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
		// TODO 2. implement search
		// TODO 3. implement filling product list
		// TODO 4. implement product selection and navigation to next step
		// just for testing:
		
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
		}
	}

	private void nextStep1() {
		// TODO Check that a product has been selected.
		startActivity(new Intent(this, PriceAdd2.class));
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
}
