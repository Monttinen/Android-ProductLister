package fi.jamk.productlister;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

/**
 *
 * @author Monttinen & Zamess
 */
public class ShopAdd extends Activity implements View.OnClickListener {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		((Button) findViewById(R.id.shop_add_addbutton)).setOnClickListener(this);

		
		


}


	@Override
	public void onClick(View v) {
	//case R.id.shop_add_addbutton:
		//addShop();
	//	break;
		
	}



}
