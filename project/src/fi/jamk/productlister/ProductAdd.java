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
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		return true;

}
}
