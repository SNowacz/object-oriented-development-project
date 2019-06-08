package depot_system;

public class Driver {
	protected String username;
	protected String password;
	/**
	 * Checks if if provided password is the one set for this driver.
	 * Note that there is no getPassword() method for security reasons.
	 * @param p Password to check against stored one.
	 * @return True if password checks out, false otherwise.
	 */
	public boolean checkPassword(String p){
		return p.equals(password);
	}
	
	public String getUsername(){
		return username;
	}
	
	public Driver(String username, String password){
		this.username = username;
		setPassword(password);
	}
	
	public synchronized void setPassword(String pass){
		this.password = pass;
	}
	
}
