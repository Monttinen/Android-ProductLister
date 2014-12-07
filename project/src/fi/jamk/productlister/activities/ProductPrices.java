package fi.jamk.productlister.activities;


import fi.jamk.productlister.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ProductPrices extends Activity implements View.OnClickListener {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_prices);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		((Button) findViewById(R.id.product_add_addbutton)).setOnClickListener(this);

	}
	
	
	
	
	
	//TODO
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.product_add_addbutton:
				startActivity(new Intent(this, PriceAdd.class));
				break;

		}
	}

}
