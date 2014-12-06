package fi.jamk.productlister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdd2 extends Activity implements View.OnClickListener {

	private DBConnector db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_price2);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
		Button nextStep2 = (Button) findViewById(R.id.price_next_step_2);
		nextStep2.setOnClickListener(this);
		
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
			case R.id.price_next_step_2:
				nextStep2();
				break;
		}
	}

	private void nextStep2() {
		// TODO Check that a shop has been selected.
		startActivity(new Intent(this, PriceAdd3.class));
	}
}
