package fi.jamk.productlister;

import java.util.ArrayList;

public class DBConnector {
	
	public DBConnector(){
		
	}
	
	public ArrayList<Product> searchProducts(String keyword){
		ArrayList<Product> results = new ArrayList<Product>();
		if(keyword.length()<3){
			return results;
		}
		
		
		// dummy data:
		results.add(new Product(1, 1, "testiolut", "bar1"));
		results.add(new Product(2, 1, "testiolut2", "bar2"));
		results.add(new Product(3, 1, "testiolut3", "bar3"));
		results.add(new Product(4, 1, "testiolut4", "bar4"));
		
		return results;
	}
}
