package gui.input;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import data.InitialData;
import data.model.Airport;
import data.model.Flight;
import error.ErrorDialog;
import error.ErrorHandler;
import gui.MainWindow;

/**
 * This class is a Dialog designed for manual Airport and Flight input. 
 */
public class InputDialog extends Dialog {

    // Airport input fields
    private final TextField airportNameField = new TextField(15);
    private final TextField airportTagField = new TextField(5);
    private final TextField airportXField = new TextField(10);
    private final TextField airportYField = new TextField(10);    

    // Flight input fields
    private final Choice flightStartAirportField = new Choice();
    private final Choice flightEndAirportField = new Choice();
    private final TextField flightStartHourField = new TextField(3);
    private final TextField flightStartMinuteField = new TextField(3);
    private final TextField flightDurationField = new TextField(5);

    public InputDialog(MainWindow parent) {
        super(parent, "Add Airports and Flights", true);
        
        setLayout(new GridLayout(0, 2, 10, 5));

        // --- Airport Section ---
        add(new Label("--- Airport ---"));
        add(new Label("")); 

        add(new Label("Name:"));
        add(airportNameField);

        add(new Label("Tag (3 uppercase):"));
        add(airportTagField);

        add(new Label("X: (-90, 90)"));
        add(airportXField);
        
        add(new Label("Y: (-90, 90)"));
        add(airportYField);

        Button addAirportButton = new Button("Add Airport");
        add(addAirportButton);
        Button clearAirportButton = new Button("Clear Airport");
        add(clearAirportButton);

        // --- Flight Section ---
        add(new Label("--- Flight ---"));
        add(new Label("")); 

        add(new Label("Choose Starting Airport"));
        add(flightStartAirportField);

        add(new Label("Choose Destination Airport"));
        add(flightEndAirportField);
        
        updateChoose();
        
        flightStartAirportField.addItemListener(e -> {
        		String endAirportText = flightEndAirportField.getSelectedItem();
        		updateEnd();
        		String startAirportText = flightStartAirportField.getSelectedItem();
        		if (endAirportText != null && endAirportText != startAirportText)
        			flightEndAirportField.select(endAirportText);
        });
        
        add(new Label("Start Time (Hours:Minutes):"));
        
        Panel timePanel = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.add(flightStartHourField);
        timePanel.add(new Label(":"));
        timePanel.add(flightStartMinuteField);
        add(timePanel);

        add(new Label("Duration (minutes):"));
        add(flightDurationField);

        Button addFlightButton = new Button("Add Flight");
        add(addFlightButton);
        Button clearFlightButton = new Button("Clear Flight");
        add(clearFlightButton);
        
        add(new Label(""));

        // --- Input Validation ---
        airportTagField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = airportTagField.getText();
                
                // Allow backspace and delete
                if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                    return;
                }
                
                // Only allow letters, limit to 3 characters
                if (!Character.isLetter(c) || text.length() >= 3) {
                    e.consume(); // Ignore non-letter or if length >= 3
                    return;
                }
                
                // Convert to uppercase
                e.setKeyChar(Character.toUpperCase(c));
            }
        });

        // X and Y Coordinates: minus sign and numbers within [-90, 90]
        KeyAdapter coordinateValidator = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                TextField field = (TextField) e.getSource();
                String text = field.getText();
                
                // Allow backspace, delete, minus sign (only at start)
                if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                    return;
                }
                if (c == '-' && text.isEmpty()) {
                    return;
                }
                
                // Only allow digits
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                
                // Check if the resulting number would be valid
                try {
                    String newText = text + c;
                    int value = Integer.parseInt(newText);
                    if (value > 90) {
                        field.setText("90");
                        e.consume();
                    } else if (value < -90) {
                        field.setText("-90");
                        e.consume();
                    }
                } catch (NumberFormatException ex) {
                    e.consume();
                }
            }
        };

        airportXField.addKeyListener(coordinateValidator);
        airportYField.addKeyListener(coordinateValidator);

        // --- Event Handling ---
        
        // Handles "Add Airport" button click
        addAirportButton.addActionListener(e -> {
        		String name = airportNameField.getText();
        		String tag = airportTagField.getText();
        		if (!ErrorHandler.isValidTagLength(tag)) {
        			new ErrorDialog(this, "Tag needs to be three uppercase letters!");
        		}
        		else {
 
            		String x = airportXField.getText();
            		String y = airportYField.getText();
            		if (!ErrorHandler.isValidCoordinate(x) || !ErrorHandler.isValidCoordinate(y))
            		{
            			new ErrorDialog(this, "Coordinates need to be numbers between -90 and 90!");
            		}
            		else {
            			Airport port = new Airport(parent.map.getWidth() / 180.0, Integer.parseInt(x),
            					Integer.parseInt(y), 1, name, tag);
            			if (!ErrorHandler.isUniqueTag(port)) {
            				new ErrorDialog(this, "Tag must be an unique identifier!");
            			}
            			else {
                			InitialData.addAirport(port);
                			parent.map.addModel(port);
            				parent.updateAirportsAndFlights();
            				clearAirportFields();
            				updateChoose();
            			}
             	}
        		}
        });

        
        // Handles "Add Flight" button click
        addFlightButton.addActionListener(e -> {
        		if (flightStartAirportField.getSelectedItem() == null || 
        				flightEndAirportField.getSelectedItem() == null)
        			new ErrorDialog(this, "No Airports are selected. Chances are you have not added them yet.");
        		else {
            		String startAirportTag = flightStartAirportField.getSelectedItem().substring(1,4);
            		Airport start = InitialData.findByTag(startAirportTag);
            		String endAirportTag = flightEndAirportField.getSelectedItem().substring(1,4);
            		Airport end = InitialData.findByTag(endAirportTag);
            		try {
            			int hours = Integer.parseUnsignedInt(flightStartHourField.getText());
            			int minutes = Integer.parseUnsignedInt(flightStartMinuteField.getText());
            			if (minutes >= 60 || minutes < 0)
                			new ErrorDialog(this, "Minutes must be between 0 and 59.");
            			else {
                			int duration = Integer.parseUnsignedInt(flightDurationField.getText());
                			if (duration <= 0) {
                    			new ErrorDialog(this, "Duration must be greater than 0");
                			}
                			else {
                       		Flight f = new Flight(1, start, end, hours, minutes, duration); 
                    			InitialData.addFlight(f);
                    			clearFlightFields();
                				parent.updateAirportsAndFlights();
                			}
            			}
            		}
            		catch (NumberFormatException ex) {
            			new ErrorDialog(this, "Hours, minutes and duration need to be positive integers");
            		}
        		}

        });

        // Handles "Clear" button clicks
        clearAirportButton.addActionListener(e -> {
        		clearAirportFields();
        });
        
        clearFlightButton.addActionListener(e -> {
        		clearFlightFields();
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); 
            }
        });

        pack(); 
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void clearAirportFields() {
        airportNameField.setText("");
        airportTagField.setText("");
        airportXField.setText("");
        airportYField.setText("");
    }
    
    private void clearFlightFields() {
        flightStartHourField.setText("");
        flightStartMinuteField.setText("");
        flightDurationField.setText("");
    }
    private void updateChoose() {
    		flightStartAirportField.removeAll();
        for (Airport i : InitialData.getAirports()) {
            flightStartAirportField.add("[" + i.getTag() + "] " + i.getName());
        }
        updateEnd();
    }
    
    private void updateEnd() {
		flightEndAirportField.removeAll();
        for (Airport i : InitialData.getAirports()) {
            flightEndAirportField.add("[" + i.getTag() + "] " + i.getName() );
        }
        	removeStartFromEnd();
    }
    
    private void removeStartFromEnd() {
        if (flightStartAirportField.getSelectedItem() != null) 
        	flightEndAirportField.remove(flightStartAirportField.getSelectedItem());
    }
}