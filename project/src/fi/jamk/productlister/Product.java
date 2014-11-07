package fi.jamk.productlister;

public class Product {
	private int idProduct;
	private int ProductCategoryId;
	private String ProductName;
	private String ProductBarcode;
	
	public Product(int id, int cateogryId, String name, String barcode){
		this.idProduct = id;
		this.ProductBarcode = barcode;
		this.ProductName = name;
		this.ProductCategoryId = cateogryId;
	}
	
	public int getProductId(){
		return idProduct;
	}
	
	public int getProductCategoryId(){
		return ProductCategoryId;
	}
	
	public String getProductName(){
		return ProductName;
	}
	
	public String getProductBarcode(){
		return ProductBarcode;
	}
}
