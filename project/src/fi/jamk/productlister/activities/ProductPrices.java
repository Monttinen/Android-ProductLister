package fi.jamk.productlister.activities;

import fi.jamk.productlister.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fi.jamk.productlister.adapter.PriceAdapter;
import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Price;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductPrices extends Activity implements View.OnClickListener {

	private ProgressDialog progress;
	private ImageView productImage;
	private ListView priceList;
	private DBConnector db;

	private int selectedProductId;
	private String selectedProductName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_prices);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		((Button) findViewById(R.id.product_prices_add)).setOnClickListener(this);
		TextView selectedProductTextView = (TextView) findViewById(R.id.selected_product_text);
		productImage = (ImageView) findViewById(R.id.productImageView);
		priceList = (ListView) findViewById(R.id.product_prices_list);
		progress = new ProgressDialog(this);
		db = new DBConnector();
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		productImage.setMaxHeight((int)(size.x*0.5));
		
		Intent intent = getIntent();
		selectedProductId = intent.getIntExtra("selectedProductId", 0);
		selectedProductName = intent.getStringExtra("selectedProductName");

		selectedProductTextView.setText(selectedProductName);

		progress.setIndeterminate(true);
		progress.setMessage("Getting data..");
		progress.show();
		GetProductImage getImageTask = new GetProductImage();
		getImageTask.execute();
		GetProductPrices getPricesTask = new GetProductPrices();
		getPricesTask.execute();
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

		@Override
		protected void onPostExecute(Bitmap b) {
			productImage.setImageBitmap(b);
		}
	}

	private class GetProductPrices extends AsyncTask<Void, Void, ArrayList<Price>> {

		@Override
		protected ArrayList<Price> doInBackground(Void... params) {
			try {
				return db.getPrices(0, selectedProductId);
			} catch (Exception ex) {
				Logger.getLogger(ProductPrices.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ArrayList<Price>();
		}

		@Override
		protected void onPostExecute(ArrayList<Price> prices) {
			PriceAdapter newadapter = new PriceAdapter(ProductPrices.this,
					R.layout.listview_price, prices);
			priceList.setAdapter(newadapter);
			progress.hide();
		}
	}
}
