package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import data.InitialData;
import data.model.Airport;
import data.model.Flight;
import gui.input.InputDialog;
import helper.FileSystem;
import helper.KillerThread;
import logic.Simulation;

/**
 * The Main Window of the project, containing the following:
 * - Center Panel: a MapScene object (Canvas) that displays the map 
 * - Right Panel: a list of Airport and Flights that have been added/loaded
 * - Bottom Panel: Start and Pause buttons, and the Simulation Clock
 * - A Killer Thread that kills the app after KILL_TIME
 */
public class MainWindow extends Frame {

	private final int KILL_TIME = 60;
	private final int REMINDER_TIME = 55; 
	
	Panel centerPanel = new Panel();
	Panel rightPanel = new Panel(new GridLayout(2, 1));
	Label time = new Label();
	private 	Panel airportsContainerPanel = new Panel(new GridLayout(0, 1));
	private Panel flightsContainerPanel = new Panel(new GridLayout(0, 1));
	public MapScene map = new MapScene(this);
	private Panel bottomPanel = new Panel();
	private Label timeText = new Label("Time: ");
	private Button start = new Button("Start Simulation");
	private Button pause = new Button("Pause");
	private boolean first_run = true;
	private boolean paused = false;
	private int dim;
	public KillerThread killer;
	boolean[] killerPauseActions = new boolean[3];

	public MainWindow() {
		setTitle("Flight Simulator");
		setResizable(false);
		setBounds(250, 100, 1200, 800);
			
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		
		populateWindow();
		pack();
		setVisible(true);
		
		killer = new KillerThread(this, KILL_TIME, REMINDER_TIME);
	}
	
	private void populateWindow() {
	    dim = getWidth() / 2;
	    
	    setupMenuBar();
	    centerPanelSetup();
	    	bottomPanelSetup();
	    	rightPanelSetup();
	}
	
	public void setupMenuBar() {
		Menu fileMenu = new Menu("File");
		MenuItem save = new MenuItem("save to a CSV file", new MenuShortcut(KeyEvent.VK_S));
		MenuItem load = new MenuItem("load a CSV file", new MenuShortcut(KeyEvent.VK_L));
		fileMenu.add(save);
		fileMenu.add(load);
		
		save.addActionListener((ae) -> {
			killer.userAction();
			killerPauseActions[2] = true;
			killerRefresh();
			
		    FileDialog fd = new FileDialog(this, "Save CSV File", FileDialog.SAVE);
		    fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".csv"));

		    fd.setVisible(true); 
		    
		    killerPauseActions[2] = false;
		    killerRefresh();	    		    
		    
		    String directory = fd.getDirectory();
		    String fileName = fd.getFile();
		    if (fileName != null && directory != null) {
		        String filePath = directory + fileName;  
		        FileSystem.saveToCSV(filePath);
		    } 
		});
		
		load.addActionListener((ae) -> {
			killer.userAction();
			killerPauseActions[2] = true;
			killerRefresh();
			
		    FileDialog fd = new FileDialog(this, "Load CSV File", FileDialog.LOAD);
		    fd.setFile("simulation.csv");  
		    fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".csv"));
		    fd.setVisible(true);  
		    killerPauseActions[2] = false;
		    killerRefresh();
		    
		    String directory = fd.getDirectory();
		    String fileName = fd.getFile();
		    if (fileName != null && directory != null) {  
		        String filePath = directory + fileName; 
		        if (FileSystem.loadFromCSV(filePath, this)) {
		            updateAirportsAndFlights();
			    		if (map.simulation != null) {
			    			map.simulation.finish();
				    		map.simulation = new Simulation(map, time, paused);
			    		}
		            map.repaint();
		            
		        }
		    }
		});
		
		Menu addMenu = new Menu("Add");
		MenuItem input = new MenuItem("Input", new MenuShortcut(KeyEvent.VK_A));
		
		input.addActionListener((ae) -> {
			killer.userAction();
			killerPauseActions[2] = true;
			killerRefresh();
			
			new InputDialog(this);

			killerPauseActions[2] = false;
		    killerRefresh();
		});
		
		addMenu.add(input);
				
		MenuBar menuBar = new MenuBar();
		menuBar.add(fileMenu);
		menuBar.add(addMenu);
		setMenuBar(menuBar);
	}
	
	public void centerPanelSetup() {
	    map.setPreferredSize(new Dimension(dim, dim));
	    map.setBackground(Color.green);
	    
		centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
	    centerPanel.add(map);
	    add(centerPanel, BorderLayout.CENTER);
	}
	
	
	public void bottomPanelSetup() {
		start.addActionListener((ae) -> {
			killer.userAction();
	    		if (first_run) {
	    			killerPauseActions[0] = true;
	    			killerRefresh();
	    			start.setLabel("Restart Simulation");
	    			pause.setVisible(true);
	    			first_run = false;
	    			pause.revalidate();
	    			timeText.setVisible(true);
	    			timeText.revalidate();
	    		}
	
			if (map.simulation != null) map.simulation.finish();
			map.simulation = new Simulation(map, time, paused);
			
			map.repaint();
			map.requestFocus();
		});
    
		pause.addActionListener((ae) -> {
	        killer.userAction();
	    		if (paused) {
	    			pause.setLabel("Pause");
	    			map.simulation.timer.go();
	    			killerPauseActions[0] = true;
	    		}
	    		else {
	    			pause.setLabel("Unpause");
	    			map.simulation.timer.pause();
	    			killerPauseActions[0] = false;
	    		}
	    		killerRefresh();
	    		paused = !paused;
	    		pause.revalidate();
	    		map.requestFocus();
		});
    
	    timeText.setVisible(false);
	    bottomPanel.add(timeText);
	    bottomPanel.add(time);
	    bottomPanel.add(start);
	    
	    pause.setVisible(false);
	    bottomPanel.add(pause);
	    
	    bottomPanel.setBackground(Color.LIGHT_GRAY);
	    add(bottomPanel, BorderLayout.SOUTH);
	}
	
	
	public void rightPanelSetup() {
	    Panel rightPanel = new Panel(new BorderLayout());
	    	Panel listsContainer = new Panel(new GridLayout(2, 1));

	    // --- Airports Section ---
	    Panel airportsSection = new Panel(new BorderLayout());
	    Label airportsTitle = new Label("Airports");
	    airportsTitle.setAlignment(Label.CENTER);
	    airportsSection.add(airportsTitle, BorderLayout.NORTH);
	    
	    ScrollPane airportsScroll = new ScrollPane();
	    airportsScroll.add(airportsContainerPanel);
	    airportsSection.add(airportsScroll, BorderLayout.CENTER);

	    // --- Flights Section ---
	    Panel flightsSection = new Panel(new BorderLayout());
	    Label flightsTitle = new Label("Flights");
	    flightsTitle.setAlignment(Label.CENTER);
	    flightsSection.add(flightsTitle, BorderLayout.NORTH);
	    
	    ScrollPane flightsScroll = new ScrollPane();
	    flightsScroll.add(flightsContainerPanel);
	    flightsSection.add(flightsScroll, BorderLayout.CENTER);
	    
	    updateAirportsAndFlights();

	    listsContainer.add(airportsSection);
	    listsContainer.add(flightsSection);
	    
	    rightPanel.add(listsContainer, BorderLayout.CENTER);
	    
	    rightPanel.setPreferredSize(new Dimension(dim, dim));
	    
	    add(rightPanel, BorderLayout.EAST);
	}
	
	public void updateAirportsAndFlights() {
	    airportsContainerPanel.removeAll();
	    flightsContainerPanel.removeAll();

	    for (Airport airport : InitialData.getAirports()) {
	        Panel airportEntryPanel = new Panel();
	        Checkbox airportCheckbox = new Checkbox(airport.toString(), true);
	        
	        airportCheckbox.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent e) {
	                airport.toggleActive();
	                if (airport.isActive()) map.addModel(airport);
	                else map.removeModel(airport);
	                killer.userAction();
	                map.repaint();
	            }
	       });

	        airportEntryPanel.add(airportCheckbox);
	        airportsContainerPanel.add(airportEntryPanel);
	    }

	    for (Flight flight : InitialData.getFlights()) {
	        Panel flightEntryPanel = new Panel();
	        flightEntryPanel.add(new Label(flight.toString()));
	        flightsContainerPanel.add(flightEntryPanel);
	    }

	    airportsContainerPanel.revalidate();
	    airportsContainerPanel.repaint();
	    flightsContainerPanel.revalidate();
	    flightsContainerPanel.repaint();
	    
	    airportsContainerPanel.getParent().revalidate();
	    airportsContainerPanel.getParent().repaint();
	    flightsContainerPanel.getParent().revalidate();
	    flightsContainerPanel.getParent().repaint();
	    
	    map.revalidate();
	    map.repaint();
	}
	
	/**
	 * Pauses the kill timer if the simulation is ongoing, the player is in a dialog, or
	 * if an airport is selected. Unpauses when those situations are over/paused.
	 */
	void killerRefresh() {
		boolean existsAction = false;
		for (boolean action : killerPauseActions) {
			if (action) existsAction = true;
		}
		if (existsAction) killer.pause();
		else killer.unpause();
	}
	
	/**
	 * Terminates the threads and disposes the window.
	 */
	public void quit() {
		if (map.simulation != null) map.simulation.finish();
	    if (map.flashTask != null && !map.flashTask.isDone()) {
	        map.flashTask.cancel(true);
	    }
	    map.service.shutdownNow();
		dispose();
	    killer.interrupt();
	}

	public static void main(String[] args) {
		new MainWindow();
	}
}
