package depot_system;

import java.time.LocalDateTime;

public class TimeThread implements Runnable {
	public LocalDateTime date = java.time.LocalDateTime.now();
	/**
	 * Delay in milliseconds between simulated hours.
	 */
	private int delay = 2000; //delay between new "hours"
	private DepotSystem sys;
	public boolean Stop = false;

	public TimeThread(DepotSystem sys) {
		this.sys = sys;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getDelay() {
		return delay;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(delay);
				if(Stop) Thread.currentThread().stop();
				date = date.plusHours(1);
				sys.updateSchedules(date);
				//System.out.println("Tick!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
