package depot_console;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Set;
import depot_system.*;
import depot_system.Depot.LongerThan72HoursException;
import depot_system.Depot.Not48HousAdvanceException;
import depot_system.Depot.ScheduleClashException;
import depot_system.Tanker.LiquidType;
import depot_system.WorkSchedule.InvalidDateRangeException;
import depot_system.WorkSchedule.ScheduleStatus;

public class ConsoleMenu {
	
	private DepotSystem ds = new DepotSystem();
	private SaneInput in = new SaneInput();
	private String currentUser = "";
	private String currentDepot = "";
	private String filepath = "M:\\eclipse-neon\\OODEV_Coursework\\src\\depot_console\\depotdata.csv";
	
	private void loginMenu() throws IOException, InvalidDateRangeException{
		String choice = "";
		boolean quit = false;
		
		while(!quit){
			System.out.println();
			System.out.println("Login Menu");
			System.out.println("1  - Show Depots");
			System.out.println("2  - Log In");
			System.out.println("3  - Admin Console"); // for testing and to simulate functions not assigned to anyone in spec (adding drivers and vehicles etc.)
			System.out.println("4  - Load Data from Disk");
			System.out.println("5  - Save Data to Disk");
			System.out.println("0  - Depot System Shutdown");
			System.out.print("Your Choice: ");
			choice = in.readString();
			try{
				switch(choice){
				case "1": printDepots(); break;
				case "2": login(); break;
				case "3": adminConsole(); break;
				case "4": loadFromCSV(); break;
				case "5": saveToCSV(); break;
				case "0": quit = true; break;
				default: System.out.println("Invalid choice."); break;
				}
			}
			finally{
				
			}
		}
		System.out.println("Shutting down.");
		ds.getTime().Stop = true;
	}
	
	public void printSchedules(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		if(!validUser()){
			System.out.println("INVALID USER!");
			return;
		}
		if(!isManager(ds.getDepotByName(currentDepot).getDriverByUsername(currentUser))){
			System.out.println("USER NOT AUTHORIZED TO RUN THIS COMMAND!");
			return;
		}
		System.out.println();
		System.out.println("Work Schedules at " + currentDepot);
		
		for(WorkSchedule s : ds.getDepotByName(currentDepot).getSchedules()){
			System.out.println(s.toString());
		}
	}
	
	private void makeSchedule() throws InvalidDateRangeException{
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		if(!validUser()){
			System.out.println("INVALID USER!");
			return;
		}
		if(!isManager(ds.getDepotByName(currentDepot).getDriverByUsername(currentUser))){
			System.out.println("USER NOT AUTHORIZED TO RUN THIS COMMAND!");
			return;
		}
		
		System.out.println();
		printUsers();
		System.out.print("Select driver: ");
		String driver = in.readString();
		if(!userExists(driver)){
			System.out.println("Invalid driver.");
			return;
		}
		printDetailsTrucks();
		printDetailsTankers();
		System.out.print("Select vehicle: ");
		String vehicle = in.readString();
		if(!vehicleExists(vehicle)){
			System.out.println("Invalid vehicle.");
			return;
		}
		System.out.println("Date format: YYYY-MM-ddThh:mm:ss");
		System.out.println("Current date: " + ds.getTime().getDate());
		System.out.print("Start date: ");
		LocalDateTime startDate = in.readDate();
		System.out.print("End date: ");
		LocalDateTime endDate = in.readDate();
		System.out.print("Client: ");
		String client = in.readString();
		System.out.print("Job description: ");
		String description = in.readString();
		boolean s = false;
		try {
			s = ds.getDepotByName(currentDepot).addSchedule(client, startDate, endDate, ds.getDepotByName(currentDepot).getDriverByUsername(driver), ds.getDepotByName(currentDepot).getVehicleByRegNo(vehicle), description);
		} catch (ScheduleClashException e) {
			System.out.println("Schedule clashes with another one!");
		}
		catch (Not48HousAdvanceException e) {
			System.out.println("Schedule is not at least 48 hours from now!");
		}
		catch (LongerThan72HoursException e) {
			System.out.println("Schedule is longer that 72 hours!");
		}
		if(s) System.out.println("Schedule added successfully.");
		else System.out.println("Adding schedule failed.");
	}
	
	private void printUsers(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		Set<String> users = ds.getDepotByName(currentDepot).getUsernames();
		
		System.out.println();
		System.out.println("User List:");
		for(String n : users){
			System.out.println(n);
		}
	}
	
	private void printVehicles(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		Set<String> vehicles = ds.getDepotByName(currentDepot).getRegNos();
		
		System.out.println();
		System.out.println("Vehicle List:");
		for(String n : vehicles){
			System.out.println(n + ", " + ds.getDepotByName(currentDepot).getVehicleByRegNo(n).getClass().getName());
		}
	}
	
	private void printDetailsTrucks(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		Set<String> trucks = ds.getDepotByName(currentDepot).getRegNos();
		
		System.out.println();
		System.out.println("Truck List:");
		for(String n : trucks){
			Vehicle x = ds.getDepotByName(currentDepot).getVehicleByRegNo(n);
			if(!isTruck(x)) continue;
			System.out.println(x.toString());
		}
	}
	
	private void printDetailsTankers(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		Set<String> tankers = ds.getDepotByName(currentDepot).getRegNos();
		
		System.out.println();
		System.out.println("Tanker List:");
		for(String n : tankers){
			Vehicle x = ds.getDepotByName(currentDepot).getVehicleByRegNo(n);
			if(!isTanker(x)) continue;
			System.out.println(x.toString());
		}
	}
	
	private void printDepots(){
		Set<String> depotNames = ds.getDepotNames();
		
		System.out.println();
		System.out.println("Depot List:");
		for(String n : depotNames){
			System.out.println(n);
		}
	}
	
	private void adminConsole(){
		String choice = "";
		boolean quit = false;
		
		while(!quit){
			System.out.println();
			System.out.println("Admin Console:");
			System.out.println("1  - Add Depot");
			System.out.println("2  - Change Depot");
			System.out.println("3  - Check Validity of Current Depot");
			System.out.println("4  - Print Depot List");
			System.out.println("5  - Print Username List");
			System.out.println("6  - Print Vehicle List");
			System.out.println("7  - Add Driver");
			System.out.println("8  - Add Manager");
			System.out.println("9  - Add Truck");
			System.out.println("10 - Add Tanker");
			System.out.println("11 - Print Truck Details");
			System.out.println("12 - Print Tanker Details");
			System.out.println("13 - Change User");
			System.out.println("14 - Check Validity of Current User");
			System.out.println("15 - Check If User Is Manager");
			System.out.println("16 - Set delay between hours");
			System.out.println("0  - Back to Login Menu");
			choice = in.readString();
			
			switch(choice){
			case "1": addDepot(); break;
			case "2": changeDepot(); break;
			case "3": System.out.println(validDepot()); break;
			case "4": printDepots(); break;
			case "5": printUsers(); break;
			case "6": printVehicles(); break;
			case "7": addDriver(); break;
			case "8": addManager(); break;
			case "9": addTruck(); break;
			case "10": addTanker(); break;
			case "11": printDetailsTrucks(); break;
			case "12": printDetailsTankers(); break;
			case "13": changeUser(); break;
			case "14": System.out.println(validUser()); break;
			case "15": System.out.println(Manager.class.isInstance(currentUser)); break;
			case "16": setTimeDelay(); break;
			case "0": quit = true; break;
			default: System.out.println("Invalid choice."); break;
			}
		}
	}
	
	private void setTimeDelay(){
		System.out.println();
		System.out.println("Current delay is " + ds.getTime().getDelay() + "ms");
		System.out.print("New delay (0 to cancel): ");
		int d = in.readUnsignedInteger();
		if(d == 0) return;
		ds.getTime().setDelay(d);
		return;
	}
	
	private void login() throws InvalidDateRangeException{
		String depot = "";
		String username = "";
		String password = "";
		
		printDepots();
		System.out.println();
		
		System.out.print("Depot name: ");
		depot = in.readString();
		if(!depotExists(depot)){
			System.out.println("No such depot.");
			return;
		}
		this.currentDepot = depot;
		
		System.out.print("Username: ");
		username = in.readString();
		if(!userExists(username)){
			System.out.println("No such user.");
			return;
		}
		
		System.out.print("Password: ");
		password = in.readString();
		if(!ds.getDepotByName(currentDepot).logon(username, password)){
			System.out.println("Wrong password.");
			return;
		}
		this.currentUser = username;
		
		if(isManager(ds.getDepotByName(currentDepot).getDriverByUsername(currentUser))){
			managerMenu();
		}
		else{
			driverMenu();
		}
		return;
	}
	
	private void driverMenu(){
		String choice = "";
		boolean quit = false;
		
		while(!quit){
			System.out.println();
			System.out.println("Current Depot: " + currentDepot);
			System.out.println("Logged in as: " + currentUser);
			System.out.println("Driver Menu:");
			System.out.println("1  - Check Schedule");
			System.out.println("2  - Change Password");
			System.out.println("0  - Previous Menu");
			choice = in.readString();
			
			switch(choice){
			case "1": showDriverSchedule(); break;
			case "2": changePassword(); break;
			case "0": quit = true; break;
			default: System.out.println("Invalid choice."); break;
			}
		}
	}
	
	public void showDriverSchedule(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		if(!validUser()){
			System.out.println("INVALID USER!");
			return;
		}
		System.out.println();
		System.out.println("Work Schedules for " + currentUser);
		
		for(WorkSchedule s : ds.getDepotByName(currentDepot).getSchedules()){
			if(!s.getStatus().equals(ScheduleStatus.ARCHIVED) && s.getDriver().getUsername().equals(currentUser))System.out.println(s.toString());
		}
	}
	
	private void managerMenu() throws InvalidDateRangeException{
		String choice = "";
		boolean quit = false;
		
		while(!quit){
			System.out.println();
			System.out.println("Current Depot: " + currentDepot);
			System.out.println("Logged in as: " + currentUser);
			System.out.println("Manager Menu:");
			System.out.println("1  - Driver Menu");
			System.out.println("2  - Transfer Vehicle");
			System.out.println("3  - Show Vehicle Details");
			System.out.println("4  - Show Schedules");
			System.out.println("5  - Create Schedule");
			System.out.println("6  - Show Date");
			System.out.println("0  - Previous Menu");
			choice = in.readString();
			
			switch(choice){
			case "1": driverMenu(); break;
			case "2": transferVehicle(); break;
			case "3": printDetailsTrucks(); printDetailsTankers(); break;
			case "4": printSchedules(); break;
			case "5": makeSchedule(); break;
			case "6": printDate(); break;
			case "0": quit = true; break;
			default: System.out.println("Invalid choice."); break;
			}
		}
	}
	
	private void printDate(){
		System.out.println(ds.getTime().getDate());
	}
	
	private void transferVehicle() throws InvalidDateRangeException{
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		if(!validUser()){
			System.out.println("INVALID USER!");
			return;
		}
		if(!isManager(ds.getDepotByName(currentDepot).getDriverByUsername(currentUser))){
			System.out.println("USER NOT AUTHORIZED TO RUN THIS COMMAND!");
			return;
		}
		
		System.out.println();
		String vehicle = "";
		System.out.print("Registration number of vehicle to move: ");
		vehicle = in.readString();
		if(!vehicleExists(vehicle)){
			System.out.println("Invalid vehicle.");
			return;
		}
		
		String driver = "";
		System.out.print("Driver who will move the vehicle: ");
		driver = in.readString();
		if(!userExists(driver)){
			System.out.println("Invalid driver.");
			return;
		}
		
		printDepots();
		String depot = "";
		System.out.print("Destination depot: ");
		depot = in.readString();
		try{
			if(!depotExists(depot)){
				System.out.println("Invalid depot.");
				return;
			}
			if(depot.equals(currentDepot)){
				System.out.println("Cannot transfer back to the depot vehicle is currently in.");
				return;
			}
			if(ds.getDepotByName(currentDepot).transferVehicle(ds.getDepotByName(currentDepot).getVehicleByRegNo(vehicle), ds.getDepotByName(depot), ds.getDepotByName(currentDepot).getDriverByUsername(driver))){
				System.out.println("Vehicle transferred.");
			}
			else{
				System.out.println("Transfer error. Check the vehicle's schedule.");
			}
		} catch (ScheduleClashException e) {
			System.out.println("Schedule clashes with another one!");
		}
		catch (Not48HousAdvanceException e) {
			System.out.println("Schedule is not at least 48 hours from now!");
		}
		catch (LongerThan72HoursException e) {
			System.out.println("Schedule is longer that 72 hours!");
		}
	}
	
	private void changePassword(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		if(!validUser()){
			System.out.println("INVALID USER!");
			return;
		}
		
		System.out.println();
		String pass = "";
		System.out.print("Password (cannot be empty): ");
		pass = in.readString();
		ds.getDepotByName(currentDepot).getDriverByUsername(currentUser).setPassword(pass);
		System.out.println("Password changed.");
	}
	
	private boolean validUser(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return false;
		}
		Set<String> usernames = ds.getDepotByName(currentDepot).getUsernames();
		
		for(String n : usernames){
			if(n.equals(currentUser)){
				return true;
			}
		}
		return false;
	}
	
	private void changeDepot(){
		String name = "";
		
		System.out.println();
		System.out.println("Current depot: " + currentDepot);
		System.out.print("Depot name to switch to: ");
		name = in.readString();
		currentDepot = name;
		System.out.println("Chosen depot: " + currentDepot);
	}
	
	private void changeUser(){
		String name = "";
		
		System.out.println();
		System.out.println("Current user: " + currentUser);
		System.out.print("Username to switch to: ");
		name = in.readString();
		currentUser = name;
		System.out.println("Chosen user: " + currentUser);
	}
	
	private boolean validDepot(){
		Set<String> depotNames = ds.getDepotNames();
		
		for(String n : depotNames){
			if(n.equals(currentDepot)){
				return true;
			}
		}
		return false;
	}
	
	private void addDepot(){
		String name = "";
		
		System.out.println();
		System.out.print("Name the depot (cannot be empty): ");
		name = in.readString();
		ds.addDepot(name);
		System.out.println("Depot " + name + " added.");
	}
	
	private boolean depotExists(String n){
		return null != ds.getDepotByName(n);
	}
	
	private boolean userExists(String n){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return false;
		}
		return null != ds.getDepotByName(currentDepot).getDriverByUsername(n);
	}
	
	private boolean vehicleExists(String n){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return false;
		}
		return null != ds.getDepotByName(currentDepot).getVehicleByRegNo(n);
	}
	
	private boolean isTruck(Vehicle obj){
		return Truck.class.isInstance(obj);
	}
	
	private boolean isTanker(Vehicle obj){
		return Tanker.class.isInstance(obj);
	}
	
	private boolean isManager(Driver obj){
		return Manager.class.isInstance(obj);
	}
	
	private void addDriver(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		String username = "";
		String password = "";
		
		System.out.println();
		System.out.print("Username (cannot be empty): ");
		username = in.readString();
		if(userExists(username)){
			System.out.println("Object " + username + " exists already.");
			return;
		}
		System.out.print("Password (cannot be empty): ");
			password = in.readString();
		
		ds.getDepotByName(currentDepot).addDriver(username, password);
		System.out.println("Driver " + username + " added.");
	}
	
	private void addManager(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		String username = "";
		String password = "";
		
		System.out.println();
		System.out.print("Username (cannot be empty): ");
			username = in.readString();
		if(userExists(username)){
			System.out.println("Object " + username + " exists already.");
			return;
		}
		System.out.print("Password (cannot be empty): ");
			password = in.readString();
		
		ds.getDepotByName(currentDepot).addManager(username, password);
		System.out.println("Manager " + username + " added.");
	}
	
	private void addTruck(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		String make = "";
		String model = "";
		int weight = 0;
		String regNo = "";
		int cargoCapacity = 0;
		
		System.out.println();
		System.out.print("Registration number (cannot be empty): ");
		regNo = in.readString();
		if(userExists(regNo)){
			System.out.println("Object " + regNo + " exists already.");
			return;
		}
		System.out.print("Make (cannot be empty): ");
		make = in.readString();
		System.out.print("Model (cannot be empty): ");
		model = in.readString();
		System.out.print("Weight (must be positive): ");
			weight = in.readUnsignedInteger();
		System.out.print("Cargo capacity (must be positive): ");
			cargoCapacity = in.readUnsignedInteger();
		
		ds.getDepotByName(currentDepot).addTruck(make, model, weight, regNo, cargoCapacity);
		System.out.println("Truck " + regNo + " added.");
	}
	
	private void addTanker(){
		if(!validDepot()){
			System.out.println("INVALID DEPOT!");
			return;
		}
		
		String make = "";
		String model = "";
		int weight = 0;
		String regNo = "";
		int liquidCapacity = 0;
		LiquidType liquidType = LiquidType.UNDEFINED;
		
		System.out.println();
		System.out.print("Registration number (cannot be empty): ");
		regNo = in.readString();
		if(userExists(regNo)){
			System.out.println("Object " + regNo + " exists already.");
			return;
		}
		System.out.print("Make (cannot be empty): ");
		make = in.readString();
		System.out.print("Model (cannot be empty): ");
		model = in.readString();
		System.out.print("Weight (must be positive): ");
		weight = in.readUnsignedInteger();
		System.out.print("Liquid capacity (must be positive): ");
		liquidCapacity = in.readUnsignedInteger();
		System.out.println("Possible Liquid Type values:");
		System.out.println(java.util.Arrays.asList(LiquidType.values()));
		System.out.print("Liquid type (cannot be empty): ");
		liquidType = in.readLiquidType();
		
		ds.getDepotByName(currentDepot).addTanker(make, model, weight, regNo, liquidCapacity, liquidType);
		System.out.println("Tanker " + regNo + " added.");
	}
	
	private void saveToCSV() throws IOException{
		PrintWriter printWriter = null;
		try {
			FileWriter fileWriter = new FileWriter(filepath);
			printWriter = new PrintWriter(fileWriter);
			
			for(String x : ds.getDepotNames()){
				Depot d = ds.getDepotByName(x);
				printWriter.println(d.getClass().getName() + "," + d.getName());
				
				for(String y : d.getUsernames()){
					Driver dr = d.getDriverByUsername(y);
					printWriter.println(dr.getClass().getName() + "," + d.getName() + "," + dr.getUsername());
				}
				
				for(String y : d.getRegNos()){
					Vehicle v = d.getVehicleByRegNo(y);
					if(isTruck(v)){
						printWriter.println(v.getClass().getName() + "," + d.getName() + "," + v.getMake() + "," + v.getModel() + "," + v.getWeight() + "," + v.getRegNo() + "," + Truck.class.cast(v).getCargoCapacity());
					}
					if(isTanker(v)){
						printWriter.println(v.getClass().getName() + "," + d.getName() + "," + v.getMake() + "," + v.getModel() + "," + v.getWeight() + "," + v.getRegNo() + "," + Tanker.class.cast(v).getLiquidCapacity() + "," + Tanker.class.cast(v).getLiquidType());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("ERROR SAVING TO FILE");
		}
	    finally{
	    	if(printWriter != null) printWriter.close();
	    }
	}
	
	private void loadFromCSV() throws IOException{
		BufferedReader br = null;
		try {
			String line = "";
			br = new BufferedReader(new FileReader(filepath));
			
				while ((line = br.readLine()) != null) {

			        // use comma as separator
			        String[] parsed = line.split(",");

			        switch(parsed[0]){
			        case "depot_system.Depot": ds.addDepot(parsed[1]); break;
			        case "depot_system.Truck": ds.getDepotByName(parsed[1]).addTruck(parsed[2], parsed[3], Integer.parseInt(parsed[4]), parsed[5], Integer.parseInt(parsed[6])); break;
			        case "depot_system.Tanker": ds.getDepotByName(parsed[1]).addTanker(parsed[2], parsed[3], Integer.parseInt(parsed[4]), parsed[5], Integer.parseInt(parsed[6]), LiquidType.valueOf(parsed[7])); break;
			        case "depot_system.Driver": ds.getDepotByName(parsed[1]).addDriver(parsed[2], "p"); break;
			        case "depot_system.Manager": ds.getDepotByName(parsed[1]).addManager(parsed[2], "p"); break;
			        default: break;
			        }

			    }
		} catch (NumberFormatException e) {
			System.out.println("ERROR READING FROM FILE");
		}
		finally{
			if(br != null) br.close();
		}
	}
	
	public static void main(String[] args) throws IOException, InvalidDateRangeException {
		ConsoleMenu menuSystem = new ConsoleMenu(); // an instance of this class to initiate class-wide variable ds to allow code to be broken into smaller functions
		menuSystem.loginMenu();
	}

}
