package depot_system;

public class Truck extends Vehicle {
	private int cargoCapacity;
	
	public Truck(String make, String model, int weight, String regNo, int cargoCapacity){
		super(make, model, weight, regNo);
		this.cargoCapacity = cargoCapacity;
	}
	
	public Truck(Truck v){
		super(v);
		this.cargoCapacity = v.cargoCapacity;
	}
	
	public int getCargoCapacity(){
		return cargoCapacity;
	}
	
	public String toString(){
		return super.toString() + ", cargocapacity: " + cargoCapacity;
	}

	public void setCargoCapacity(int cargoCapacity) {
		this.cargoCapacity = cargoCapacity;
	}
}
