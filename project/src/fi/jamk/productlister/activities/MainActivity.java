package fi.jamk.productlister.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import fi.jamk.productlister.R;

/**
* The main activity for the application.
* Contains a menu for navigating to different activities of the application.
*/

public class MainActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((Button) findViewById(R.id.main_button_search)).setOnClickListener(this);
		((Button) findViewById(R.id.main_button_add_product)).setOnClickListener(this);
		((Button) findViewById(R.id.main_button_add_shop)).setOnClickListener(this);
		((Button) findViewById(R.id.main_button_add_price)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * The OnClickListeners for the menu buttons.
	 * @param v 
	 */
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.main_button_search:
				startActivity(new Intent(this, ProductSearch.class));
				break;

			case R.id.main_button_add_product:
				startActivity(new Intent(this, ProductAdd.class));
				break;

			case R.id.main_button_add_shop:
				startActivity(new Intent(this, ShopAdd.class));
				break;

			case R.id.main_button_add_price:
				startActivity(new Intent(this, PriceAdd.class));
				break;
		}
	}
}
