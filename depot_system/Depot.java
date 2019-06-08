package depot_system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import depot_system.Tanker.LiquidType;
import depot_system.WorkSchedule.InvalidDateRangeException;
import depot_system.WorkSchedule.ScheduleStatus;

public class Depot {
	private Hashtable<String, Vehicle> vehicles = new Hashtable<String, Vehicle>();
	private Hashtable<String, Driver> drivers = new Hashtable<String, Driver>();
	private String name = "name not set";
	private List<WorkSchedule> schedules;
	private TimeThread time;
	
	public Depot(String name, TimeThread time){
		this.name = name;
		schedules = new ArrayList<WorkSchedule>();
		schedules = Collections.synchronizedList(schedules);
		this.time = time;
	}
	
	/**
	 * Does not actually log anyone in. It's a utility function that simplifies checking if user exists and if password for that user is correct.
	 * All access checks are to be done in target application.
	 * This is so to allow different user access types (single-user, multiple users over internet etc.)
	 * @param user username to check for
	 * @param pass password to authenticate with
	 * @return true if user exists in this depot and password checks out, false otherwise
	 */
	
	public boolean logon(String user, String pass){
		Driver d = drivers.get(user);
		if(d == null) return false;
		return d.checkPassword(pass);
	}
	
	public String getName(){
		return name;
	}
	
	public void addDriver(String user, String pass){
		drivers.put(user, new Driver(user, pass));
	}
	
	public void addManager(String user, String pass){
		drivers.put(user, new Manager(user, pass));
	}
	
	public void addTruck(String make, String model, int weight, String regNo, int cargoCapacity){
		vehicles.put(regNo, new Truck(make, model, weight, regNo, cargoCapacity));
	}
	
	public void addTanker(String make, String model, int weight, String regNo, int liquidCapacity, LiquidType liquidType){
		vehicles.put(regNo, new Tanker(make, model, weight, regNo, liquidCapacity, liquidType));
	}
	
	public Driver getDriverByUsername(String user){
		return drivers.get(user);
	}
	
	public Set<String> getUsernames(){
		return drivers.keySet();
	}
	
	public Vehicle getVehicleByRegNo(String regNo){
		return vehicles.get(regNo);
	}
	
	public Set<String> getRegNos(){
		return vehicles.keySet();
	}
	
	public void addExistingVehicle(Vehicle v, String from, Driver d) throws InvalidDateRangeException, ScheduleClashException, Not48HousAdvanceException, LongerThan72HoursException{
		vehicles.put(v.regNo, v);
		addSchedule(from + " depot", time.getDate().plusHours(49), time.getDate().plusHours(59), d, v, "moving vehicle " + v.regNo + " from " + from + " to " + name);
	}
	
	public boolean transferVehicle(Vehicle v, Depot destination, Driver d) throws InvalidDateRangeException, ScheduleClashException, Not48HousAdvanceException, LongerThan72HoursException{
		if(hasNonarchivedSchedule(v)){
			return false;
		}
		addSchedule(name + " depot", time.getDate().plusHours(49), time.getDate().plusHours(59), d, v, "moving vehicle " + v.regNo + " from " + name + " to " + destination.name);
		destination.addExistingVehicle(v, this.name, d);
		//addSchedule(name + " depot", time.getDate().plusHours(49), time.getDate().plusHours(59), d, v, "moving vehicle " + v.regNo + " from " + name + " to " + destination.name);
		vehicles.remove(v.regNo);
		return true;
	}
	
	public void updateSchedules(LocalDateTime x){
		for(WorkSchedule s : schedules){
			if(!s.getStatus().equals(ScheduleStatus.ARCHIVED)) s.updateStatus(x);
		}
	}
	
	private boolean hasNonarchivedSchedule(Vehicle v){
		for(WorkSchedule s : schedules){
			if(!s.getStatus().equals(ScheduleStatus.ARCHIVED)) return true;
		}
		return false;
	}
	
	public boolean isVehicleAvailable(Vehicle v, LocalDateTime start, LocalDateTime end){
		if(start.isAfter(end) || start.isEqual(end)) return false; //sanity check
		for(WorkSchedule s : schedules){
			if(!s.getStatus().equals(ScheduleStatus.ARCHIVED) || s.getVehicle().equals(v)){
				if(s.isDateInSchedule(start) || s.isDateInSchedule(end)) return false;
				if(s.isDateBeforeSchedule(start) && s.isDateAfterSchedule(end)) return false;
			}
		}
		return true;
	}
	
	public boolean isDriverAvailable(Driver d, LocalDateTime start, LocalDateTime end){
		if(start.isAfter(end) || start.isEqual(end)) return false; //sanity check
		for(WorkSchedule s : schedules){
			if(!s.getStatus().equals(ScheduleStatus.ARCHIVED) || s.getDriver().equals(d)){
				if(s.isDateInSchedule(start) || s.isDateInSchedule(end)) return false;
				if(s.isDateBeforeSchedule(start) && s.isDateAfterSchedule(end)) return false;
			}
		}
		return true;
	}
	/**
	 * A method for creating a new schedule.
	 * It has common sense checks (ex. if start date is before end date) and ones that conform to specification (ex. a job cannot be longer than 72 hours).
	 * @param client Name or other identifier of a client.
	 * @param startDate Start date of a job.
	 * @param endDate End date of a job.
	 * @param driver Driver object that will drive the vehicle.
	 * @param vehicle Vehicle that will do the job.
	 * @param description Any other information about the job.
	 * @return True if created successfully, false if dates are incorrect.
	 * @throws InvalidDateRangeException Should not be thrown, handled by return value. If thrown, it was thrown by constructor and this function contains an error.
	 * @throws ScheduleClashException Thrown if driver or vehicle are not available at requested dates.
	 * @throws Not48HousAdvanceException Thrown if job would be scheduled 48 hours in advance (note that this will be thrown if scheduled exactly at 48 hours from current simulated date).
	 * @throws LongerThan72HoursException Thrown if job would end up longer than 72 hours.
	 */
	public synchronized boolean addSchedule(String client, LocalDateTime startDate, LocalDateTime endDate, Driver driver, Vehicle vehicle, String description)
			throws InvalidDateRangeException, ScheduleClashException, Not48HousAdvanceException, LongerThan72HoursException{
		if(startDate.isAfter(endDate) || startDate.isEqual(endDate)) return false; //sanity check
		if(!isVehicleAvailable(vehicle, startDate, endDate) || !isDriverAvailable(driver, startDate, endDate)){
			throw new ScheduleClashException("Driver and/or vehicle not available!"); //is driver and vehicle available?
		}
		if(!time.date.plusHours(48).isBefore(startDate)){
			throw new Not48HousAdvanceException("Job isn't 48 hours in advance!"); //no jobs less than 48 hours in advance as per spec
		}
		if(!startDate.plusHours(72).isAfter(endDate)){
			throw new LongerThan72HoursException("Job is longer than 72 hours!"); //jobs no longer than 72 hours as per spec
		}
		schedules.add(new WorkSchedule(client, startDate, endDate, driver, vehicle, description));
		return true;
	}
	
	public List<WorkSchedule> getSchedules(){
		return schedules;
	}
	
	public class ScheduleClashException extends Exception {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -3581926044039094081L;

		public ScheduleClashException(String message) {
	        super(message);
	    }
	}
	
	public class Not48HousAdvanceException extends Exception {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -3581926044039094082L;

		public Not48HousAdvanceException(String message) {
	        super(message);
	    }
	}
	
	public class LongerThan72HoursException extends Exception {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -3581926044039094083L;

		public LongerThan72HoursException(String message) {
	        super(message);
	    }
	}
}
