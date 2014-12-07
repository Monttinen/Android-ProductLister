package fi.jamk.productlister.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fi.jamk.productlister.R;
import fi.jamk.productlister.db.DBConnector;
import fi.jamk.productlister.model.Price;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antti Minkkinen
 */
public class PriceAdapter extends ArrayAdapter<Price>{
	private Context context;
    private int layoutResourceId;   
    private ArrayList<Price> prices;
	
	private DBConnector db;
	
	public PriceAdapter(Context context, int textViewResourceId, ArrayList<Price> prices) {
		super(context, textViewResourceId, prices);
		this.layoutResourceId = textViewResourceId;
        this.context = context;
        this.prices = prices;
		
		db = new DBConnector();
	}
	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
		PriceHolder holder = null;
		
		if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new PriceHolder();
            holder.shop = (TextView)row.findViewById(R.id.txtShop);
            holder.uprice = (TextView)row.findViewById(R.id.txtUnitPrice);
            holder.qprice = (TextView)row.findViewById(R.id.txtQuantityPrice);
           
            row.setTag(holder);
        }
        else
        {
            holder = (PriceHolder)row.getTag();
        }
       
        Price price = prices.get(position);
		GetShop task = new GetShop();
		task.execute(price.getShopId());
		String shopName = "";
		try {
			shopName = task.get();
		} catch (InterruptedException ex) {
			Logger.getLogger(PriceAdapter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ExecutionException ex) {
			Logger.getLogger(PriceAdapter.class.getName()).log(Level.SEVERE, null, ex);
		}
		
        holder.shop.setText(shopName);
        holder.uprice.setText(price.getUnitPrice()+"e");
		holder.qprice.setText("["+price.getQuantityPrice()+"e/l or kg]");
		
       
        return row;
	}
	
	static class PriceHolder {
		TextView shop;
		TextView uprice;
		TextView qprice;
	}
	
	private class GetShop extends AsyncTask<Integer,Void,String> {
		@Override
		protected String doInBackground(Integer... params) {
			return db.getShop(params[0]).getShopName();
		}
	}
}
