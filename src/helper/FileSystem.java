package helper;

import java.awt.Dialog;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Frame;

import data.InitialData;
import data.model.Airport;
import data.model.Flight;
import error.ErrorHandler;
import gui.MainWindow;

/**
 * This class handles saving to and loading from a CSV file the Airport and Flight data 
 */
public class FileSystem {
    private static void showErrorDialog(String message) {
        Dialog dialog = new Dialog((Frame) null, "Error", true);
        dialog.setLayout(new java.awt.FlowLayout());
        dialog.add(new Label(message));
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) { dialog.dispose(); }
        });
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static boolean saveToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Save airports
            for (Airport a : InitialData.getAirports()) {
                writer.write(String.format("Airport,%s,%s,%d,%d\n",
                        escapeCSV(a.getName()), escapeCSV(a.getTag()), a.getX(), a.getY()));
            }

            // Save flights
            for (Flight f : InitialData.getFlights()) {
                writer.write(String.format("Flight,%s,%s,%d,%d,%d\n",
                        escapeCSV(f.getStartAirport().getTag()), escapeCSV(f.getEndAirport().getTag()),
                        f.getHour(), f.getMinute	(), f.getDuration()));
            }
            return true;
        } catch (IOException e) {
            showErrorDialog("Error occurred while saving!");
            return false;
        }
    }

    public static boolean loadFromCSV(String filePath, MainWindow owner) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	        ArrayList<Airport> airportContainer = new ArrayList<>();
	        ArrayList<Flight> flightContainer = new ArrayList<>();
            Map<String, Airport> tempAirports = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);

                String type = parts[0].trim();
                if ("Airport".equals(type)) {
                    if (parts.length != 5) {
                        showErrorDialog("Airport row must have 5 fields!");
                        return false;
                    }
                    try {
                        String name = parts[1].trim();
                        String tag = parts[2].trim();

                        if (!ErrorHandler.isValidTagLength(tag)) {
                        		showErrorDialog("Airport tags must be three letters. Please check the input!");
                        		return false;
                        }
                        tag = tag.toUpperCase();
                        
                        int x = Integer.parseInt(parts[3].trim());
                        int y = Integer.parseInt(parts[4].trim());
                        if (x < -90 || x > 90 || y < -90 || y >90) {
                        		showErrorDialog("Coordinates must be integers between -90 and 90! Please check the input");
                        		return false;
                        }
                        Airport a = new Airport(owner.map.getWidth() / 180.0, x, y, 1, name, tag);
                        if (!ErrorHandler.isUniqueTagCSV(a, airportContainer)) {
                        		showErrorDialog("Airport tags must be unique identifiers! Please check the input.");
                        		return false;
                        }
                        airportContainer.add(a);
                        tempAirports.put(tag, a);
                    } catch (NumberFormatException e) {
                        showErrorDialog("Invalid coordinates in an Airport row! Please check the input.");
                        return false;
                    }
                } else if ("Flight".equals(type)) {
                    if (parts.length != 6) {
                        showErrorDialog("Flight row must have 6 fields. Please check the input.");
                        return false;
                    }
                    try {
                        String startTag = parts[1].trim();
                        String endTag = parts[2].trim();
                        int startHour = Integer.parseUnsignedInt(parts[3].trim());
                        int startMinute = Integer.parseUnsignedInt(parts[4].trim());
                        if (startMinute >= 60 || startMinute < 0) {
                        		showErrorDialog("Start minutes must be between 0 and 59. Please check if the input is correct.");
                        		return false;
                        }
                        int duration = Integer.parseUnsignedInt(parts[5].trim());
                        if (duration <= 0) {
	                        	showErrorDialog("Duration must be greater than 0. Please check if the input is correct.");
	                    		return false;
                        }
                        Airport start = tempAirports.get(startTag);
                        Airport end = tempAirports.get(endTag);
                        if (start == null || end == null) {
                            showErrorDialog("Flight row contains an Airport that can't be found! Flight must be declared after the airport, please check the input.");
                            return false;
                        }
                        Flight f = new Flight(1, start, end, startHour, startMinute, duration);
                        flightContainer.add(f);
                    } catch (NumberFormatException e) {
                        showErrorDialog("Flight row contains invalid start time or duration! Please check the input.");
                        return false;
                    }
                } else {
                    showErrorDialog("Row type is not Airport or Flight. Please check the input.");
                    return false;
                }
            }
    		    InitialData.getAirports().clear();
            InitialData.getFlights().clear();
            for (Airport a : airportContainer) {
            		InitialData.addAirport(a);
            }
            for (Flight f : flightContainer) {
        			InitialData.addFlight(f);
            }
            owner.map.clearModels();
            owner.map.loadAirports();
            owner.map.unselectModel();
            return true;
        } catch (java.io.FileNotFoundException e) {
            showErrorDialog("File '" + filePath + "' can't be found.");
            return false;
        } catch (IOException e) {
            showErrorDialog("Error while reading the file.");
            return false;
        }
        
    }

    private static String escapeCSV(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}