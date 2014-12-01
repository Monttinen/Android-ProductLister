package fi.jamk.productlister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
