package depot_system;

public abstract class Vehicle {
	protected String make;
	protected String model;
	protected int weight;
	protected String regNo;
	
	public Vehicle(String make, String model, int weight, String regNo){
		this.make = make;
		this.model = model;
		this.weight = weight;
		this.regNo = regNo;
	}
	
	public Vehicle(Vehicle v){
		this.make = v.make;
		this.model = v.model;
		this.regNo = v.regNo;
		this.weight = v.weight;
	}
	
	public String getMake(){
		return make;
	}
	
	public String getModel(){
		return model;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public String getRegNo(){
		return regNo;
	}
	
	@Override
	public String toString(){
		return "regno: " + regNo + ", make: " + make + ", model: " + model + ", weight: " + weight;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
