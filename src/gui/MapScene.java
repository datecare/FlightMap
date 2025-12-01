package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import data.InitialData;
import data.model.Airport;
import data.model.Flight;
import data.model.Model;
import helper.SimulationClock;
import logic.Simulation;


/**
 * This class displays Models on a map.
 * 
 * <p> Handles Model selection and "flashing"
 */
public class MapScene extends Canvas {
	
	private MainWindow parent;
	public Simulation simulation;
	ArrayList<Model> modelsToPaint = new ArrayList<>();
	ArrayList<Model> bgModels = new ArrayList<>();
	private final Object modelsLock = new Object();
	Model selectedModel = null;
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> flashTask;
    Rectangle r;
    int px;
    int py;
	double scale;
	
	public MapScene(MainWindow parent) {
		this.parent = parent;
		addSelectModelListener();
	}
	
	public void addModel(Model m) {
		synchronized (modelsLock) {
			modelsToPaint.add(m);
		}
	}
	
	public void removeModel(Model m) {
		synchronized (modelsLock) {
			modelsToPaint.remove(m);
		}
	}
	
	public void clearModels() {
		synchronized (modelsLock) {
			modelsToPaint.clear();
		}
	}
	
	public void loadAirports() {
		synchronized (modelsLock) {
			for (Airport a : InitialData.getAirports()) {
				modelsToPaint.add(a);
			}
		}
	}
	
	public ArrayList<Model> getDisplayed() {
		synchronized (modelsLock) {
			return modelsToPaint;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		paintTick();
	}
	
	/**
	 * Paints the map
	 */
	public void paintTick() {
		Graphics g = this.getGraphics();
	    int width = getWidth();
	    
	    SimulationClock timer = null;
	    if (simulation != null) timer = simulation.timer;
	    
	    scale = (double) width / 180.0;
	    
	    drawBackground(g, timer);

	    synchronized (modelsLock) {
		    for (Model m : modelsToPaint) {
	    		m.paint(g, timer);
	    }
	    }

	}
	
	private void drawBackground(Graphics g, SimulationClock timer) {
	    g.setColor(Color.BLUE);
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	    

	    if (r != null) {
	    	g.setColor(Color.BLUE);
	    	g.drawRect(r.x, r.y, r.width, r.height);
	    	System.out.println("drawing rect");
	    }
	    
		
	}

	/**
	 * Handles Model flashing by creating a task that runs every 500ms
	 * and schedules it at the service scheduler.
	 */
	private void flashModel() {
	    if (flashTask != null && !flashTask.isDone()) {
	        flashTask.cancel(true);
	    }
	    if (selectedModel == null) {
	    		repaint();
	    		return;
	    }
		flashTask = service.scheduleAtFixedRate(new Runnable()
        {
            public void run()
            {
            		((Airport)selectedModel).toggleFlash();
                repaint();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Adds a listener that listens for selection of models via mouse click
	 */
	private void addSelectModelListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(Model m : modelsToPaint) {
					if (m instanceof Airport) {
						Airport port = (Airport) m;
						int px = port.getX();
						int py = port.getY();
						int scaleApr = (int)scale;
						if (port.isInside(e.getX(), e.getY()))
						{
							parent.killer.userAction();
							if (selectedModel != null && ((Airport)selectedModel).isFlashing()) {
								((Airport)selectedModel).toggleFlash();
							}
							if (selectedModel != port) {
								selectedModel = port;
								parent.killerPauseActions[1] = true;
							}
							else {
								unselectModel();
							}
							parent.killerRefresh();
						}
						flashModel();
				    }
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				px = e.getX(); py = e.getY();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				r = null;
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int realx = Math.min(x, px);
				int realy = Math.min(y, py);
				int width = Math.abs(x - px);
				int height = Math.abs(y - py);
				r = new Rectangle(realx, realy, width, height);
				repaint();
			}
		});
	}
	
	public void unselectModel() {
		selectedModel = null;
		parent.killerPauseActions[1] = false;
		parent.killerRefresh();
		flashModel();
	}
}
