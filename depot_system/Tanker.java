package depot_system;

public class Tanker extends Vehicle {
	public enum LiquidType{
		UNDEFINED, WATER, OIL, CHEMICALS, PETROL, EDIBLE
	}
	
	private int liquidCapacity;
	private LiquidType liquidType;
	
	public Tanker(String make, String model, int weight, String regNo, int liquidCapacity, LiquidType liquidType){
		super(make, model, weight, regNo);
		this.liquidCapacity = liquidCapacity;
		this.liquidType = liquidType;
	}
	
	public Tanker(Tanker v){
		super(v);
		this.liquidCapacity = v.liquidCapacity;
		this.liquidType = v.liquidType;
	}
	
	public int getLiquidCapacity(){
		return liquidCapacity;
	}
	
	public LiquidType getLiquidType(){
		return liquidType;
	}
	
	public String toString(){
		return super.toString() + ", liquidcapacity: " + liquidCapacity + ", liquidtype: " + liquidType.toString();
	}

	public void setLiquidCapacity(int liquidCapacity) {
		this.liquidCapacity = liquidCapacity;
	}

	public void setLiquidType(LiquidType liquidType) {
		this.liquidType = liquidType;
	}
}
