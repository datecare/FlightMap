package logic;

import java.awt.Label;
import java.util.ArrayList;
import java.util.Iterator;

import data.InitialData;
import data.model.Airport;
import data.model.Flight;
import data.model.Model;
import gui.MapScene;
import helper.SimulationClock;

public class Simulation extends Thread {
	
	// Simulation ticks every SIM_TICK milliseconds.
	private final int SIM_TICK = 200;
	
	MapScene map;
	ArrayList<Flight> simFlights = new ArrayList<>();
	public SimulationClock timer;
	private Label timeLabel;
	
	/**
	 * @param map The scene the map is tied to
	 * @param timeLabel The timer label that the simulation updates
	 * @param initialPause The state the simulation starts in: paused or not
	 */
	public Simulation(MapScene map, Label timeLabel, boolean initialPause) {
		this.timeLabel = timeLabel;
		this.map = map;
		simFlights = (ArrayList<Flight>) InitialData.getFlights().clone();
		timer = new SimulationClock(SIM_TICK);
		timer.addClient(this);
		timer.start();
		if (!initialPause)	timer.go();
		else timeLabel.setText(timer.toString());
		this.start();
	}
	
	/**
	 * Terminates the timer and simulation threads when they are no longer needed
	 */
	public synchronized void finish() {
		if (timer != null) {
			timer.interrupt();
		}
		interrupt();
		while (map.simulation != null) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
	
	synchronized void clearSimulation() {
        map.simulation = null;
        notify(); 
    }
	
	public boolean inInterval(Flight f) {
		return timer.isIntervalActive(f.getHour(), f.getMinute(), f.getDuration());
	}
	
	/**
	 * Handles the actual simulation: goes through the logic of which models are supposed to be
	 * drawn and calls the Scene's paintTick method for the actual painting.
	 */
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {	
  				timeLabel.setText(timer.toString());
 				timeLabel.revalidate();
 				
 				/*
 				 * Deals with each airport's Flight queuing,
 				 * updates the passed time and dispatches the next flight
 				 * from the queue if it is ready.
 				*/
				for (Airport a : InitialData.getAirports()) {
					a.setPassedTime(a.getPassedTime() + 2);
					if (a.numberOfWaitingFlight() != 0 && a.canWaitingFly()) {
						Flight f = a.getNextFlight();
						map.addModel(f);
						a.setLastDepartedFlightTime(f.getStartTime());
						a.setPassedTime(0);
					}
				}
				
				
				/* 
				 * Goes through each flight, checking if it is ready to
				 * display or not, if it is either dispatches it instantly
				 * or adds it to the airport's queue to wait until it can
				 */
				Iterator<Flight> flightIterator = simFlights.iterator();
				while (flightIterator.hasNext()) {
					Flight f = flightIterator.next();
					if (inInterval(f)) {
						Airport start = f.getStartAirport();
						
						// If no flights in queue, dispatch
						if (start.canInstantFly()) {
							map.addModel(f);
							start.setLastDepartedFlightTime(f.getStartTime());
							start.setPassedTime(0);
						}
						
						// Otherwise reschedule the flight and add it to the queue
						else {
							int newStartTime = start.getLastDepartedFlightTime()
									+ (start.numberOfWaitingFlight() + 1) * 10; 
							int newStartHour = newStartTime / 60;
							int newStartMinute = newStartTime % 60;
							Flight delayedFlight = new Flight(1,
									f.getStartAirport(), f.getEndAirport(), 
									newStartHour, newStartMinute, f.getDuration());
							start.addFlightToQueue(delayedFlight);
						}
						flightIterator.remove();
					}
				}
				
				// Removes flights that have passed
				Iterator<Model> modelsIterator = map.getDisplayed().iterator();
				while (modelsIterator.hasNext()) {
					Model m = modelsIterator.next();
					if (m instanceof Flight) {
						Flight f = (Flight) m;
						if (!inInterval(f)) {
							modelsIterator.remove();
						}
					}
				}
				
				map.repaint();
				
				// Wait for the Simulation Timer to notify
			    synchronized (this) {
				    this.wait();
				}
			}
		} catch (InterruptedException e) {}
		
		synchronized (this) {
			clearSimulation();
		}
	}

}
