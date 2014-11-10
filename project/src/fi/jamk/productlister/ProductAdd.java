package fi.jamk.productlister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Monttinen & Zamess
 */
public class ProductAdd extends Activity implements View.OnClickListener{
	private DBConnector db;
	private ArrayList<Category> categories;
	private Spinner categorySpinner;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		db = new DBConnector();
		categories = new ArrayList<Category>();
		((Button)findViewById(R.id.take_picture)).setOnClickListener(this);

		
		categorySpinner = (Spinner) findViewById(R.id.product_add_category);
		
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
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;

	}
	
	
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
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


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.take_picture:
			dispatchTakePictureIntent();
			break;

		
		}
	}
}
