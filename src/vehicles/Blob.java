package vehicles;

import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.Body;

public class Blob extends Vessel {

	private static final long serialVersionUID = -1076300242632818823L;

	private Sprite body;
	
	private static final int blobRadius = 10;
	private static final float blobDensity = .1f;
	private static final float blobMaxThrottle = 100;
	private static final float blobSizeFuelRatio = 1000f;
	private static final float blobThrottleAbs = -.1f;
	private static final float blobThrottleStep = .01f;

	public Blob() {
		this(0, 0);
	}
	
	public Blob(int x, int y) {
		this(x, y, blobRadius);
	}

	public Blob(int x, int y, int radius) {
		this(x, y, radius, 0, 0);
	}

	public Blob(int x, int y, int radius, float initSpeedX, float initSpeedY) {
		super(blobDensity*radius*radius, radius, 0, blobSizeFuelRatio*radius, initSpeedX, initSpeedY);
		setLocation(x, y);
		addVisibleObject(body = new Sprite(Sprites.SPRITE_BLOB)).setVisible(true);
		start();
	}

	private void updateThrottleValues(int x, int y) {
		if (Math.abs(getThrottleX())+Math.abs(getThrottleY()) < blobMaxThrottle) {
		/* augmentation linéaire */
			throttleX(blobThrottleStep*x);
			throttleY(blobThrottleStep*y);
		}
	}

	private void updateBlobRadius() {
		setDiameter((int)Math.sqrt(getMass()/blobDensity));
		body.setDiameter(getDiameter());
	}
	
	//push left = simple thrust to left
	public void goLeft(int units) {
		updateThrottleValues(-units, 0);
	}

	//push right = simple thrust to right
	public void goRight(int units) {
		updateThrottleValues(units, 0);
	}

	//push up = simple thrust upward
	public void goForward(int units) {
		updateThrottleValues(0, -units);
	}

	//push down = simple thrust downward
	public void goBackward(int units) {
		updateThrottleValues(0, units);
	}

	protected void onFreeze() {
		resetThrottle();
	}
	
	/**
	 * When hit by a smaller object, a blob increases its own mass and speed
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		incSpeed((m.getSpeedX()+m.getSpeedX()*m.getMass())/(getMass()*2),
				 (m.getSpeedY()+m.getSpeedY()*m.getMass())/(getMass()*2));
		setMass(getMass() + m.getMass());
		return true;
	}

	protected void onTankEmpty(){
	}
	
	protected void onTimeCycle(){
		updateBlobRadius();
		throttleX(getThrottleX()*blobThrottleAbs);
		throttleY(getThrottleY()*blobThrottleAbs);
	}
}
