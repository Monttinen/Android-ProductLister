package fi.jamk.productlister.activities;

import fi.jamk.productlister.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fi.jamk.productlister.db.DBConnector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductPrices extends Activity implements View.OnClickListener {
	
	private ImageView productImage;
	private DBConnector db;

	private int selectedProductId;
	private String selectedProductName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_prices);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		((Button) findViewById(R.id.product_prices_add)).setOnClickListener(this);
		TextView selectedProductTextView = (TextView) findViewById(R.id.productPricesSearched);
		productImage = (ImageView) findViewById(R.id.productImageView);
		
		db = new DBConnector();

		Intent intent = getIntent();
		selectedProductId = intent.getIntExtra("selectedProductId", 0);
		selectedProductName = intent.getStringExtra("selectedProductName");

		selectedProductTextView.setText(selectedProductName);
		
		GetProductImage getImageTask = new GetProductImage();
		getImageTask.execute();
		Bitmap image = null;
		try {
			image = getImageTask.get();
			productImage.setImageBitmap(image);
		} catch (InterruptedException ex) {
			Logger.getLogger(ProductPrices.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(ProductPrices.class.getName()).log(Level.SEVERE, null, ex);
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
			case R.id.product_prices_add:
				Intent intent = new Intent(this, PriceAdd2.class);
				intent.putExtra("selectedProductId", selectedProductId);
				intent.putExtra("selectedProductName", selectedProductName);
				startActivity(intent);
				break;
		}
	}

	private class GetProductImage extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... params) {
			return db.getProductImage(selectedProductId);
		}
	}
}
