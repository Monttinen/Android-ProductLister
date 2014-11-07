package fi.jamk.productlister;

public class Shop {
	private int idShop;
	private String ShopName;
	private String ShopAddress;
	private String ShopLocation;
	
	public Shop(int id, String name, String address, String location){
		this.idShop = id;
		this.ShopName = name;
		this.ShopAddress = address;
		this.ShopLocation = location;
	}
	
	public int getShopId(){
		return idShop;
	}
	
	public String getShopName(){
		return ShopName;
	}
	
	public String getShopAdddress(){
		return ShopAddress;
	}
	
	public String getShopLocation(){
		return ShopLocation;
	}
	
}
