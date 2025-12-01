package data;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import data.model.Airport;
import data.model.Flight;

/**
 * This class is the data set used to hold the airport and flight data, giving an
 * interface for the storage of data from various sources.
 */
public class InitialData {

	private static ArrayList<Airport> airports = new ArrayList<>();
	private static ArrayList<Flight> flights = new ArrayList<>();
	
	public static void addAirport(Airport airport) {
		if (!airports.contains(airport)) {
			airports.add(airport);
		}
	}
	
	public static void addFlight(Flight flight) {
		if (!flights.contains(flight)) {
			flights.add(flight);
		}
	}
	
	public static ArrayList<Airport> getAirports() {
		return airports;
	}

	public static ArrayList<Flight> getFlights() {
		return flights;
	}
	
	/**
	 * Checks if the airport exists in the Data by the unique tag 
	 * @param tag Tag by which the airport is searched in the data set
	 * @return the Airport object if it exists, otherwise null
	 */
	public static Airport findByTag(String tag) {
		try {
		Airport found = InitialData.getAirports().stream()
			    .filter(airport -> airport.getTag().equals(tag))  
			    .findFirst()  
			    .get();  
		return found;
		} catch (NoSuchElementException e) {
			return null;
		}
		
	}

	public InitialData() {}

}
