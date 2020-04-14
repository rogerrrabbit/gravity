package gravity;

import graphics.VisibleObject;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.Vector;

import tools.Pair;

public abstract class Mass implements Serializable {

	private static final long serialVersionUID = 5559742312376956317L;

	private float mass;
	private Pair location;
	private Pair gOffset;

	private boolean docked = false;

	private Body parent = null;
	
	public Body getParent() {
		return parent;
	}

	public void setParent(Body parent) {
		this.parent = parent;
	}
	
	public Vector<VisibleObject> objects;
	
	public static float calculateDistance(Mass m1, Mass m2) {
		return (float) Math.sqrt(	(m1.getLocationX() - m2.getLocationX())*(m1.getLocationX() - m2.getLocationX()) + 
									(m1.getLocationY() - m2.getLocationY())*(m1.getLocationY() - m2.getLocationY()));
	}

	public synchronized VisibleObject addVisibleObject(VisibleObject o) {
		objects.add(o);
		return o;
	}

	public synchronized void removeVisibleObject(VisibleObject o) {
		objects.remove(o);
	}

	public Mass(float mass) {
		this.mass = mass;
		location = new Pair(0,0);
		gOffset = new Pair(0,0);
		objects = new Vector<VisibleObject>();
	}
	
	public synchronized void setLocation(float x, float y) {
		location.fx = x;
		location.fy = y;
	}

	public synchronized void setLocation(Pair p) {
		location.fx = p.fx;
		location.fy = p.fy;
	}
	
	public float getgOffsetX() {
		return gOffset.fx;
	}

	public void setgOffsetX(float gOffsetX) {
		this.gOffset.fx = gOffsetX;
	}

	public float getgOffsetY() {
		return gOffset.fy;
	}

	public void setgOffsetY(float gOffsetY) {
		this.gOffset.fy = gOffsetY;
	}
	
	public float getMass() {
		return mass;
	}

	public synchronized void setMass(float mass) {
		this.mass = mass;
	}
	
	protected float getEnergy() {
		return getMass();
	}
	
	public Pair getLocation() {
		return location;
	}
	
	public float getLocationX() {
		return location.fx;
	}

	public float getLocationY() {
		return location.fy;
	}
	
	public boolean isDocked() {
		return docked;
	}

	public void setDocked(boolean docked) {
		this.docked = docked;
	}
	
	public synchronized void paintGraphics(Graphics g) {
		for (VisibleObject v : objects) {
			v.paintGraphics(g, getLocation());
		}
	}
	
	protected synchronized void initGraphics() {
		for (VisibleObject v : objects) {
			v.init();
		}
	}
	
	protected void onRemove() {
	}
	
	protected void updateForces(float gameSpeed) {
	}
}