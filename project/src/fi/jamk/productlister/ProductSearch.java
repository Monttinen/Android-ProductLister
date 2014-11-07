package fi.jamk.productlister;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProductSearch extends Activity {
	private DBConnector db;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_search);
        db = new DBConnector();
        ArrayList<Product> test = db.searchProducts("olut");
        
        ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_1, test);
        ListView list = (ListView)findViewById(R.id.product_search_listview);
        
        list.setAdapter(adapter);
    }
}
