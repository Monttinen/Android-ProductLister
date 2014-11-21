package fi.jamk.productlister;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 *
 * @author Monttinen & Zamess
 */
public class ShopAdd extends Activity implements View.OnClickListener {

	
	
	private EditText txtShopName;
	private EditText txtShopAddress;
	private EditText txtShopLocation;
	
	String ShopName;
	String ShopAddress;
	String ShopLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		((Button) findViewById(R.id.shop_add_addbutton)).setOnClickListener(this);
		name = (EditText)findViewById(R.id.name);  
		
		


}

	
	private void addShop() {
		
		
		
		Shop potato = new shop()
		

	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.shop_add_addbutton:
				addShop();
				break;
			

		}

	}


}

