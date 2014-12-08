package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Product;
import fi.jamk.productlister.model.Category;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Monttinen & James Pearce
 */
public class ProductAdd extends Activity implements View.OnClickListener,
		AdapterView.OnItemSelectedListener {

	private ArrayList<Category> categories;
	private ArrayList<Category> subCategories;
	
	private Spinner categorySpinner;
	private Spinner subCategorySpinner;
	private TextView name;
	private ProgressDialog progress;
	private ImageView picture;
	
	private int addedProductId = -1;
	private String mCurrentPhotoPath = "";
	private Bitmap productImage = null;
	
	private DBConnector db;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		db = new DBConnector();
		categories = new ArrayList<Category>();
		subCategories = new ArrayList<Category>();
		progress = new ProgressDialog(this);
		
		((Button) findViewById(R.id.take_picture)).setOnClickListener(this);
		((Button) findViewById(R.id.product_add_addbutton))
				.setOnClickListener(this);
		
		picture = (ImageView) findViewById(R.id.camera_preview);
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		picture.setMaxHeight((int)(size.x*0.5));
		
		categorySpinner = (Spinner) findViewById(R.id.product_add_category);
		subCategorySpinner = (Spinner) findViewById(R.id.product_add_subcategory);

		name = (TextView) findViewById(R.id.product_add_name_text);

		categorySpinner.setOnItemSelectedListener(this);
		
		// Get the categories for spinners
		GetCategoriesTask gategoryTask = new GetCategoriesTask();
		gategoryTask.execute();

		try {
			categories = gategoryTask.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		ArrayAdapter<Category> newadapter = new ArrayAdapter<Category>(
				ProductAdd.this, android.R.layout.simple_spinner_dropdown_item,
				categories);

		categorySpinner.setAdapter(newadapter);
		
		// Hides the sub category spinner if a category has no sub category.
		if (subCategories.size() < 1) {
			subCategorySpinner.setVisibility(View.INVISIBLE);
		} else {
			subCategorySpinner.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Used for navigating back from the action bar.
	 * Navigates back to main activity.
	 * @param item
	 * @return 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;
	}
	
	/**
	 * A method for starting the product image capturing.
	 */
	private void TakePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		File photoFile = null;
		try {
			photoFile = createImageFile();
		} catch (IOException ex) {
			// Error occurred while creating the File
			Toast.makeText(getApplicationContext(), "Creating tmp image failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
		}

		if (photoFile != null && takePictureIntent.resolveActivity(getPackageManager()) != null) {
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}
	
	/**
	 * Creates an empty file for storing the captured image.
	 * @return file for image
	 * @throws IOException 
	 */
	private File createImageFile() throws IOException {
		String imageFileName = "productimage";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		mCurrentPhotoPath = image.getAbsolutePath();

		return image;

	}
	
	/**
	 * Processes the image that was taken for a product.
	 * @param requestCode
	 * @param resultCode
	 * @param data 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			// saving data from the camera into a Bitmap
			Bitmap imageBitmap = decodeSampledBitmapFromFile(mCurrentPhotoPath, 500, 500);
			
			// Set the image to the ImageView
			picture.setImageBitmap(imageBitmap);

			Toast.makeText(getApplicationContext(), "Saved image: " + mCurrentPhotoPath, Toast.LENGTH_LONG).show();
			// making a JPEG from the scaled down bitmap
			File file = new File(mCurrentPhotoPath);
			productImage = imageBitmap;

			FileOutputStream fOut = null;

			try {
				fOut = new FileOutputStream(file);

				imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);

				fOut.flush();
				fOut.close();

			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Problem saving image",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * A listener for category spinners
	 * @param parent
	 * @param v
	 * @param position
	 * @param id 
	 */
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		switch (parent.getId()) {
			case R.id.product_add_category:
				Category c = (Category) ((Spinner) parent).getSelectedItem();
				subCategories = getSubCategories(c.getCategoryId());
				ArrayAdapter<Category> newadapter = new ArrayAdapter<Category>(
						ProductAdd.this,
						android.R.layout.simple_spinner_dropdown_item,
						subCategories);

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
	
	/**
	 * A listener for spinners. Not used really but needs to be implemented.
	 * @param parent 
	 */
	public void onNothingSelected(AdapterView<?> parent) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * A method for getting categories.
	 * @param categoryId
	 * @return ArrayList of categories
	 */
	private ArrayList<Category> getSubCategories(int categoryId) {
		ArrayList<Category> result = new ArrayList<Category>();
		GetCategoriesTask gategoryTask = new GetCategoriesTask();
		gategoryTask.execute(categoryId);
		try {
			result = gategoryTask.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return result;
	}
	
	/**
	 * AsyncTask for getting categories from database.
	 */
	private class GetCategoriesTask extends
			AsyncTask<Integer, Void, ArrayList<Category>> {

		@Override
		protected ArrayList<Category> doInBackground(Integer... params) {
			if (params != null && params.length > 0) {
				return db.getCategories(params[0]);
			} else {
				return db.getCategories();
			}
		}

	}
	
	/**
	 * A method for getting the currently selected category.
	 * @return 
	 */
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
	
	/**
	 * Starts the product adding.
	 */
	private void addProduct() {
		try {
			String input = name.getText().toString();
			String[] inputLines = input.split("\n");
			String productName = inputLines[0];

			if (productName.length() < 1 || productName.length() > 255) {
				Toast.makeText(getApplicationContext(),
						"Product name is not valid.", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			int productCategoryId = getSelectedCategory();
			progress.setIndeterminate(true);
			progress.setMessage("Adding the product..");
			progress.show();
		
			// Start the task for adding the product
			AddProductTask task = new AddProductTask();
			task.execute(new Product(0, productCategoryId, productName, ""));
			JSONArray result = task.get();
			if (result.getJSONObject(0).getString("success").equals("0")) {
				progress.hide();
				Toast.makeText(
						getApplicationContext(),
						"Error adding product: "
						+ result.getJSONObject(0).getString("message"),
						Toast.LENGTH_LONG).show();
			} else {
				// clear the name field after adding
				name.setText("");
				addedProductId = result.getJSONObject(0).getInt("productid");
				
				// Now add the image if defined.
				if (productImage != null) {
					try {
						// now send the image
						AddProductImageTask imagetask = new AddProductImageTask();
						imagetask.execute();
						JSONArray result2 = imagetask.get();

					} catch (Exception ex) {
						progress.hide();
						Toast.makeText(
								getApplicationContext(),
								"Error adding product image: "
								+ ex.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				}
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (JSONException ex) {
			Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}
	
	/**
	 * OnClickListener for buttons.
	 * @param v 
	 */
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
	
	/**
	 * AsyncTask for adding products to the database.
	 */
	private class AddProductTask extends AsyncTask<Product, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Product... params) {
			JSONArray result = db.addProduct(params[0]);
			return result;
		}
	}
	
	/**
	 * AsyncTask for adding product images to the database.
	 */
	private class AddProductImageTask extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			return db.addProductImage(productImage, addedProductId);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			progress.hide();
			try {
				if (result.getJSONObject(0).getString("success").equals("0")) {
					Toast.makeText(
							getApplicationContext(),
							"Error adding product image: "
									+ result.getJSONObject(0).getString("message"),
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Added product image.", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException ex) {
				Logger.getLogger(ProductAdd.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Calculates sample size for requested size.
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return sample size
	 */
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/**
	 * Scales down the image based on requested size
	 *
	 * @param filePath
	 * @param reqWidth
	 * @param reqHeight
	 * @return Bitmap
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
}
