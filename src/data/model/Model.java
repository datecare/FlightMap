package data.model;

import java.awt.Graphics;
import helper.SimulationClock;

/**
 * This abstract class is a general Model of things that can be painted on a 2D canvas,
 * being defined by its integer x, y position and width.
 */
public abstract class Model {
	protected int x, y, vx, vy, width;
	protected double scale;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public static int realX(int vx, double scale) {
		return (int) ((vx + 90) * scale);
	}
	
	public static int realY(int vy, double scale) {		
	    return (int) ((90 - vy) * scale);
	}

	public Model(int vx, int vy, int width, double scale) {
		this.vx = vx;
		this.vy = vy;
		this.x = realX(vx, scale);
		this.y = realY(vy, scale);
		this.width = width;
		this.scale = scale;
	}
	
	public abstract void paint(Graphics g, SimulationClock timer);
	public boolean isInside(int x, int y) {
		int scaleApr = (int)scale;
		if(x >= this.x - 2*scaleApr
				&& x < this.x + 2*scaleApr - 1
				&& y >= this.y - 2*scaleApr
				&& y < this.y + 2*scale - 1
			) return true;
		return false;
	}

}
