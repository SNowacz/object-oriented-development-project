package depot_system;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Set;

/**
 * This class is a main object of the depot system.
 * After instantiating this class system will be initialised and and accessible through that object.
 * @author cmpsnowa
 *
 */

public class DepotSystem { // I changed System to DepotSystem to not confuse with Java's built-in System class (like with System.out.println() )
	private Hashtable<String, Depot> depots = new Hashtable<String, Depot>();
	private TimeThread time = new TimeThread(this);
	
	public DepotSystem(){
		Thread bg = new Thread(time);
		bg.start();
	}
	
	public void addDepot(String name){
		depots.put(name, new Depot(name, time));
	}
	
	public TimeThread getTime() {
		return time;
	}

	public Depot getDepotByName(String name){
		return depots.get(name);
	}
	
	public Set<String> getDepotNames(){
		return depots.keySet();
	}
	/**
	 * Do not call manually.
	 * It exists so the thread can update schedules. 
	 * @param x
	 */
	public void updateSchedules(LocalDateTime x){
		for(String d : depots.keySet()){
			depots.get(d).updateSchedules(x);
		}
	}
}
