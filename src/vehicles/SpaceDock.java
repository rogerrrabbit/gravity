package vehicles;

import graphics.Sphere;
import graphics.VisibleObject;
import gravity.Body;
import gravity.Space;

import java.awt.Graphics;
import java.util.Vector;

import tools.Pair;

public class SpaceDock extends Vessel {

	private static final long serialVersionUID = 8238020066025216212L;

	private Vector<Dock> dockedObjects;

	private static final int blobRadius = 200;
	private static final float blobDensity = .1f;

	public SpaceDock() {
		this (0, 0);
	}
	
	public SpaceDock(int x, int y) {
		this(x, y, 0);
	}
	
	public SpaceDock(int x, int y, float spin) {
		super(blobDensity*blobRadius*blobRadius, blobRadius, 0, 0, 0, 0);
		dockedObjects = new Vector<Dock>();
		setLocation(x, y);
		setSpin(spin);
		addVisibleObject(new Sphere(blobRadius)).setVisible(true);
	}

	public boolean absorbEnergy(Body m) {
		//if landing was okay
		synchronized (dockedObjects) {
			if (m instanceof Vessel) {
				((Vessel)m).onDocked();
				Space.getInstance().removeMass(m);
				Dock d = new Dock(m, calculateInnerCoordinates(m.getLocation()), m.getHeading() - getHeading());
				setDockedObjectLocation(d);
				dockedObjects.add(d);
			}
		}
		return false;
	}

	public synchronized void paintGraphics(Graphics g) {
		for (VisibleObject v : objects) {
			v.paintGraphics(g, getLocation());
		}
		for (Dock b : dockedObjects) {
			b.body.paintGraphics(g);
		}
	}
	
	protected void onTankEmpty(){
	}
	
	protected void onFreeze() {
	}

	public void goLeft(int units) {
	}

	public void goRight(int units) {
	}

	public void goForward(int units) {
	}

	public void goBackward(int units) {
	}
	
	private void setDockedObjectLocation(Dock d) {
		d.body.setLocation(calculateOuterCoordinates(d.coordinates));
		d.body.setHeading(d.orientation + getHeading());
	}
	
	protected void onTimeCycle(){
		synchronized (dockedObjects) {
			for (Dock d : dockedObjects) {
				setDockedObjectLocation(d);
			}
		}
	}
	
	private class Dock {
		private Body body;
		private Pair coordinates;
		private float orientation;
		private Dock(Body b, Pair c) {
			this(b, c, 0);
		}
		private Dock(Body b, Pair c, float o) {
			body = b;
			coordinates = c;
			orientation = o;
		}
	}
}
