package chzone.template_engine;

public class Product {
	@Override
	public String toString() {
		return "Product [name=" + name + ", price=" + price + "]";
	}
	private final String name;
	private  double price;

	public Product(String name, double price) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return this.name;
	}
	public double getPrice(){
		return this.price;
	}
	public static void main(String[] args) {
		
	}
	
}
