package error;

import java.util.ArrayList;

import data.InitialData;
import data.model.Airport;

/**
 * This class is used for checking if data is formatted correctly, specifically related to the Airport/Flight input.
 */
public class ErrorHandler {
	public static boolean isValidTagLength(String tag) {
		if (tag.length() != 3) return false;
		return true;
	}
	
	public static boolean isValidCoordinate(String coord) {
		try {
			int x = Integer.parseInt(coord);
			if (x <= 90 && x >= -90) return true;	
		} catch(Exception e) {}
		return false;
	}
	
	public static boolean isUniqueTag(Airport port) {
		if (InitialData.getAirports().contains(port)) {
			return false;
		}
		return true;
	}
	
	public static boolean isUniqueTagCSV(Airport port, ArrayList<Airport> container) {
		if (container.contains(port)) {
			return false;
		}
		return true;
	}
	
	
	public ErrorHandler() {

	}

}
