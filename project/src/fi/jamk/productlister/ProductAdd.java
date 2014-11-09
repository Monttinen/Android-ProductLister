package fi.jamk.productlister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 *
 * @author Monttinen
 */
public class ProductAdd extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		return true;

}
}
