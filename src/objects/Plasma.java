package objects;

import tools.Pair;
import graphics.Sphere;
import gravity.*;

public class Plasma extends Body{
	private static final long serialVersionUID = 5431257701696702374L;

	private static final float ballRadius = 10;
	private static final float energyLevel = 15;

	public Plasma(Pair location) {
		this(location, 0, 0);
	}
	
	public Plasma(float x, float y) {
		this(x, y, 0, 0);
	}

	public Plasma(Pair location, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, initSpeedX, initSpeedY);
	}
	
	public Plasma(float x, float y, float initSpeedX, float initSpeedY) {
		super(0, ballRadius, 0, initSpeedX, initSpeedY);
		setLocation(x, y);

		addVisibleObject(new Sphere(ballRadius*2).setFill(true).setBorder(false).setVisible(true));
	}

	public float getEnergy() {
		return energyLevel;
	}
	
	/**
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		if (m instanceof Plasma) { return false; }
		return true;
	}

	public void run() {
		
	}
}