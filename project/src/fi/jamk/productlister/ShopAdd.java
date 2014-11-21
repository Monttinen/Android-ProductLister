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
		txtShopName = (EditText) findViewById(R.id.editTextName);  
		txtShopAddress = (EditText) findViewById(R.id.editTextAddress);  
		txtShopLocation = (EditText) findViewById(R.id.editTextLocation);  


}

	
	private void addShop() {
		
		
	
	

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

