package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Product;
import fi.jamk.productlister.model.Category;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import fi.jamk.productlister.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Monttinen & Zamess
 */
public class ProductAdd extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	private DBConnector db;
	private ArrayList<Category> categories;
	private ArrayList<Category> subCategories;
	private Spinner categorySpinner;
	private Spinner subCategorySpinner;
	private TextView name;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		db = new DBConnector();
		categories = new ArrayList<Category>();
		subCategories = new ArrayList<Category>();

		((Button) findViewById(R.id.take_picture)).setOnClickListener(this);
		((Button) findViewById(R.id.product_add_addbutton)).setOnClickListener(this);

		categorySpinner = (Spinner) findViewById(R.id.product_add_category);
		subCategorySpinner = (Spinner) findViewById(R.id.product_add_subcategory);

		name = (TextView) findViewById(R.id.product_add_name_text);

		categorySpinner.setOnItemSelectedListener(this);

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

	private void TakePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			ImageView picture = (ImageView) findViewById(R.id.camera_preview);
			picture.setImageBitmap(imageBitmap);
		}
	}

	
	String mCurrentPhotoPath;

	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	
	

//TODO
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	          
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	
	
	
	
	
	
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		switch (parent.getId()) {
			case R.id.product_add_category:
				Category c = (Category) ((Spinner) parent).getSelectedItem();
				subCategories = getSubCategories(c.getCategoryId());
				ArrayAdapter<Category> newadapter = new ArrayAdapter<Category>(ProductAdd.this,
						android.R.layout.simple_spinner_dropdown_item, subCategories);

				subCategorySpinner.setAdapter(newadapter);
				if (subCategories.size() < 1) {
					subCategorySpinner.setVisibility(View.INVISIBLE);
				} else {
					subCategorySpinner.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.product_add_subcategory:
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

	private void addProduct() {
		try {
			String input = name.getText().toString();
			String[] inputLines = input.split("\n");
			String productName = inputLines[0];
			
			if (productName.length() < 1 || productName.length() > 255) {
				Toast.makeText(getApplicationContext(), "Product name is not valid.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			int productCategoryId = getSelectedCategory();
			
			AddProductTask task = new AddProductTask();
			task.execute(new Product(0, productCategoryId, productName, ""));
			JSONArray result = task.get();
			if(result.getJSONObject(0).getString("success").equals("0")){
				Toast.makeText(getApplicationContext(), "Error adding product: "+result.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Added product: "+productName, Toast.LENGTH_SHORT).show();
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JSONException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.take_picture:
				TakePicture();
				break;
			case R.id.product_add_addbutton:
				addProduct();
				break;

		}
	}

	private class AddProductTask extends AsyncTask<Product, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Product... params) {
			JSONArray result = db.addProduct(params[0]);
			return result;
		}
	}
}
