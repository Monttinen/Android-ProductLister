package fi.jamk.productlister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Monttinen
 */
public class ProductAdd extends Activity {
	private DBConnector db;
	private ArrayList<Category> categories;
	private ArrayList<Category> subcategories;
	
	private Spinner categorySpinner;
	private Spinner subCategorySpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		db = new DBConnector();
		categories = new ArrayList<Category>();
		subcategories = new ArrayList<Category>();
		
		categorySpinner = (Spinner) findViewById(R.id.product_add_category);
		subCategorySpinner = (Spinner) findViewById(R.id.product_add_subcategory);
		
		GetCategoriesTask gategoryTask = new GetCategoriesTask();
		gategoryTask.execute();
		
		try {
			categories = gategoryTask.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		ArrayAdapter<Category> newadapter = new ArrayAdapter<Category>(ProductAdd.this,
				android.R.layout.simple_spinner_dropdown_item, categories);
		
		categorySpinner.setAdapter(newadapter);
		
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO
			}
			
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;

	}
	
	private class GetCategoriesTask  extends AsyncTask<Integer, Void, ArrayList<Category>> {

		@Override
		protected ArrayList<Category> doInBackground(Integer... params) {
			if(params != null && params.length>0){
				return db.getCategories(params[0]);
			} else {
				return db.getCategories();
			}
		}
		
	}
}
