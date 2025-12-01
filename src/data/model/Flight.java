package data.model;

import java.awt.Color;
import java.awt.Graphics;

import helper.SimulationClock;

/**
 * This class models a flight, being defined by a starting airport, destination airport,
 * start hour, start minute and the duration of the flight.
 * 
 * <p> The actual flight is painted as a blue dot.
 */
public class Flight extends Model {
	
	private Airport startAirport;
	private Airport endAirport;
	private int hour, minute;
	private int duration;

	public Flight(int width, Airport startAirport, Airport endAirport, int startHour, int startMinute, int duration) {
		super(startAirport.getX(), startAirport.getY(), width, startAirport.scale);
		this.startAirport = startAirport;
		this.endAirport = endAirport;
		this.hour = startHour;
		this.minute = startMinute;
		this.duration = duration;
	}
	
	public Airport getStartAirport() {
		return startAirport;
	}

	public Airport getEndAirport() {
		return endAirport;
	}
	
	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getStartTime() {
		return hour * 60 + minute;
	}
	
	@Override
	public String toString() {
		return "Flight: " + 
	            startAirport.getTag() + " -->" + endAirport.getTag() + 
	            "  |  Start: " + getHour() + ":" + getMinute() + 
	            "  |  Duration: " + duration;
	}
	
	@Override
	public void paint(Graphics g, SimulationClock timer) {
		int scaleApr = (int)scale;
		
		// Updates the flight's position based on the simulation clock
		if (timer.isIntervalActive(hour, minute,
				duration))
		{
			int startTime = hour * 60 + minute;
			int totalTime = duration;
			int currentTime = timer.getHour() * 60 + 
					timer.getMinute() - startTime;
			x = startAirport.x + (int)((endAirport.x - startAirport.x) *
					(double)(currentTime)/totalTime);
			y = startAirport.y + (int)((endAirport.y - startAirport.y) *
					(double)(currentTime)/totalTime);
		}
        
        Color color = g.getColor();
        
        g.setColor(Color.blue);
        g.fillOval(x - 2*scaleApr, y - 2*scaleApr, 4*scaleApr, 4*scaleApr); 

        g.setColor(color);
	}

}
