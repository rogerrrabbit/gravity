package graphics;

import java.awt.Graphics;
import java.io.Serializable;

import tools.Pair;

public abstract class VisibleObject implements Serializable {

	private static final long serialVersionUID = -82377253549096592L;

	private boolean visible = false;
	
	private int offsetX = 0;
	private int offsetY = 0;
	
	public boolean isVisible() {
		return visible;
	}
	public VisibleObject setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public boolean paintGraphics(Graphics g, Pair location) {
		return paintGraphics(g, (int)location.fx, (int)location.fy);
	}
	
	public boolean paintGraphics(Graphics g, int x, int y) {
		if (visible) {
			return draw(g, x + offsetX, y + offsetY);
		}
		return false;
	}
	
	public void setDiameter(float radius) {
	}
	
	public void setOrientation(float orientation) {
	}

	public abstract boolean draw(Graphics g, int x, int y);
	
	public boolean init() {
		return true;
	}
	
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
}
