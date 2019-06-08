package depot_system;

import java.time.LocalDateTime;

/**
 * Class representing single work schedule.
 * Uses its own enum type for status, make sure you import it.
 * @author cmpsnowa
 *
 */

public class WorkSchedule {
	public enum ScheduleStatus{
		PENDING, ACTIVE, ARCHIVED
	}
	
	public class InvalidDateRangeException extends Exception {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -3581926044039094080L;

		public InvalidDateRangeException(String message) {
	        super(message);
	    }
	}
	
	private String client;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private ScheduleStatus status;
	private Driver driver;
	private Vehicle vehicle;
	private String description;
	
	public WorkSchedule(String client, LocalDateTime startDate, LocalDateTime endDate, Driver driver, Vehicle vehicle, String description) throws InvalidDateRangeException {
		if(startDate.isAfter(endDate) || startDate.isEqual(endDate)) throw new InvalidDateRangeException("Job cannot start when or after it ends!"); //sanity check
		this.client = client;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = ScheduleStatus.PENDING;
		this.driver = driver;
		this.vehicle = vehicle;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Driver getDriver() {
		return driver;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public ScheduleStatus getStatus() {
		return status;
	}

	public boolean isDateInSchedule(LocalDateTime x){
		if((x.isEqual(startDate) || x.isAfter(startDate)) && (x.isEqual(endDate) || x.isBefore(endDate))) return true;
		return false;
	}
	
	public boolean isDateBeforeSchedule(LocalDateTime x){
		if(x.isBefore(startDate)) return true;
		return false;
	}
	
	public boolean isDateAfterSchedule(LocalDateTime x){
		if(x.isAfter(endDate)) return true;
		return false;
	}
	
	public synchronized void updateStatus(LocalDateTime currentDate){
		if(currentDate.isBefore(startDate)) status = ScheduleStatus.PENDING;
		if(currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) status = ScheduleStatus.ACTIVE;
		if(currentDate.isAfter(endDate)) status = ScheduleStatus.ARCHIVED;
		//System.out.println("DEBUG: Schedule for " + this.client + " is " + this.status);
	}
	
	@Override
	public String toString(){
		return "client: " + client + " start: " + startDate + " end: " + endDate + "  status: " + status + " driver: "
			+ driver.getUsername() + " vehicle: " + vehicle.getRegNo() + " description: " + description;
	}
}
