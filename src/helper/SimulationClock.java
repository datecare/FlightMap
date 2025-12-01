package helper;

import java.util.ArrayList;

/**
 * This class functions as a Simulation Clock, notifying the logic and the
 * painting to update on its ticks.
 */
public class SimulationClock extends Thread {

	private int tickInterval;
	private int h, m;
	private boolean work;
	
	// Runnable clients that are notified when ticks happen
	private ArrayList<Runnable> clients = new ArrayList<>();
	
	/**
	 * @param tickInterval The interval between the clock's ticks in ms
	 */
	public SimulationClock(int tickInterval) {
		this.tickInterval = tickInterval;
	}
	
	public int getHour() {
		return h;
	}
	
	public int getMinute() {
		return m;
	}
	
	public void addClient(Runnable r) {
		clients.add(r);
	}
	
	
	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				synchronized (this) {
					while(!work) {
						wait();
					}
				}

				sleep(tickInterval);
				if (!work) continue;
				for (Runnable r : clients) {
					synchronized (r) {
						r.notify();
					}
				}
				
				m += 2;
				if (m % 60 == 0) {
					h++;
					m = 0;
				}
			}
		} catch (InterruptedException e) {}
	}
	
	public synchronized void go() {
		work = true;
		this.notify();
	}
	
	public synchronized void pause() {
		work = false;
	}
	
	public synchronized void reset() {
		m = h = 0;
	}
	
	/**
	 * checks if the current tick is in an interval defined by the start time and duration
	 */
	public boolean isIntervalActive(int startHour, int startMinute, int duration) {
	    // Calculate end time
	    int totalStartMinutes = startHour * 60 + startMinute;
	    int totalEndMinutes = totalStartMinutes + duration;
	    
	    int currentTotalMinutes = h * 60 + m;
	   
	    return currentTotalMinutes >= totalStartMinutes && currentTotalMinutes <= totalEndMinutes;
	}
	
	@Override
	public String toString() {
		return String.format("%dh:%02dm", h, m);
	}

}
