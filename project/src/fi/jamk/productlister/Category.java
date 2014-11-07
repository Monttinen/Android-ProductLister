package fi.jamk.productlister;

public class Category {
	private int idCategory;
	private String CategoryName;
	private int CategoryParentId;
	private String CategoryDescription;


	public Category(int id, String name, int parentId, String description){
		this.idCategory = id;
		this.CategoryName = name;
		this.CategoryParentId = parentId;
		this.CategoryDescription = description;
	}
	
	public String getCategoryName(){
		return CategoryName;
	}
	
	public String getCategoryDescription(){
		return CategoryDescription;
	}
	
	public int getCategoryParentId(){
		return CategoryParentId;
	}
	
	public int getCategoryId(){
		return idCategory;
	}
}
