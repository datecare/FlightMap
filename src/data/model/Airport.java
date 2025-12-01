package data.model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.Queue;

import helper.SimulationClock;

/**
 * This class models an airport, which is identified by its unique three letter tag,
 * and defined by its name, and integer Cartesian coordinates in [-90,90].
 * 
 * <p> The Airport holds a queue of Flights, so flights can't happen all at once, but
 * wait until all previous flights have dispatched.
 * <p> The Airport is displayed as a gray rectangle.
 */
public class Airport extends Model {

	private String name;
	private String tag;
	boolean active = true;
	boolean flash = false;
	private Queue<Flight> waitingZone = new ArrayDeque();
	private int lastDepartedFlightTime;
	private int passedTime = 10;
	
	public Airport(double scale, int x, int y, int width, String name, String tag) {
		super(x, y, width, scale);
		this.name = name;
		this.tag = tag;
		}

	public void toggleActive() {
		active = !active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}
	
	public boolean isFlashing() {
		return flash;
	}
	
	public void toggleFlash() {
		flash = !flash;
	}
	
	public boolean canInstantFly() {
		if (passedTime >= 10 && waitingZone.size() == 0) {
			return true;
		}
		return false;
	}
	
	public boolean canWaitingFly() {
		if (passedTime >= 10) {
			return true;
		}
		return false;
	}
	
	public void setLastDepartedFlightTime(int lastDepartedFlightTime) {
		this.lastDepartedFlightTime = lastDepartedFlightTime;
	}
	
	public int getLastDepartedFlightTime() {
		return lastDepartedFlightTime;
	}
	
	public int getPassedTime() {
		return passedTime;
	}
	
	public void setPassedTime(int passedTime) {
		this.passedTime = passedTime;
	}

	public void addFlightToQueue(Flight f) {
		waitingZone.add(f);
	}
	
	public Flight getNextFlight() {
		return waitingZone.poll();
	}
	
	public int numberOfWaitingFlight() {
		return waitingZone.size();
	}

	@Override
	public String toString() {
		return name + " [" + tag + "]: " + " (" + vx + ", " + vy + ")"; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Airport)) return false;
		Airport other = (Airport) obj;
		if (other.tag.equals(this.tag))
			return true;
		return false;
	}

	@Override
	public void paint(Graphics g, SimulationClock timer) {
		int scaleApr = (int)scale;
        Color color = g.getColor();
        
        if (!flash) g.setColor(Color.gray);
        else g.setColor(Color.red);
        g.fillRect(x - 2*scaleApr, y - 2*scaleApr, 4*scaleApr, 4*scaleApr); // -3 to center the circle on the coordinate

        g.setColor(Color.BLACK);
        g.drawString(getTag(), x + (int)4*scaleApr, (int)y + 2*scaleApr);
        g.setColor(color); 
	}
}
