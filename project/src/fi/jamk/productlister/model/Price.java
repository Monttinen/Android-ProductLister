package fi.jamk.productlister.model;

public class Price {
	private int idPrice;
	private int shopId;
	private int productId;
	
	// Negative value if not available, but atleast one needs to be defined.
	private double unitPrice;
	private double quantityPrice;

	/**
	 * Constructor for a price. Unit price and/or quantity price needs to be positive.
	 * @param idPrice
	 * @param idShop
	 * @param idProduct
	 * @param unitPrice
	 * @param quantityPrice
	 * @throws Exception if prices are wrong
	 */
	public Price(int idPrice, int idShop, int idProduct, double unitPrice, double quantityPrice) throws Exception {
		this.idPrice = idPrice;
		this.shopId = idShop;
		this.productId = idProduct;
		if(unitPrice<0 || quantityPrice < 0){
			throw new Exception("Unit price and quantity price cannot be both null!");
		}
		this.unitPrice = unitPrice;
		this.quantityPrice = quantityPrice;
	}

	public int getIdPrice() {
		return idPrice;
	}

	public int getShopId() {
		return shopId;
	}

	public int getProductId() {
		return productId;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public double getQuantityPrice() {
		return quantityPrice;
	}
	
	@Override
	public String toString(){
		return "Price of productId "+productId+" at shopId "+shopId+": "+"q:"+quantityPrice+" u:"+unitPrice;
	}
}
