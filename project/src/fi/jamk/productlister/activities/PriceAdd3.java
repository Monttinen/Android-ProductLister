package fi.jamk.productlister.activities;

import fi.jamk.productlister.db.DBConnector;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import fi.jamk.productlister.R;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdd3 extends Activity implements View.OnClickListener {
		
	private DBConnector db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price3);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
		Button addPrice = (Button) findViewById(R.id.price_step_3_add);
		addPrice.setOnClickListener(this);
		
		db = new DBConnector();
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
			case R.id.price_step_3_add:
				addPrice();
				break;
		}
	}

	private void addPrice() {
		// TODO Check that a shop and product are selected.
		// TODO Check that one of the prices is inserted.
		
		// TODO move to product view ?
		startActivity(new Intent(this, MainActivity.class));
	}
}
