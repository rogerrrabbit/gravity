package vehicles;

import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class Rocket extends Vessel {

	private static final long serialVersionUID = -4988093974371786955L;

	private Sprite body;

	private static final int blobRadius = 30;
	private static final float blobDensity = .01f;
	private static final float blobMaxThrottle = 100;
	private static final float blobSizeFuelRatio = 100f;
	private static final float blobThrottleAbs = -.1f;
	private static final float throttleStepZ = .01f;
	private static final float throttleStepX = .01f;

	public Rocket() {
		this(0, 0);
	}
	
	public Rocket(int x, int y) {
		this(x, y, blobRadius);
	}

	public Rocket(int x, int y, int radius) {
		this(x, y, radius, 0, 0);
	}

	public Rocket(int x, int y, int radius, float initSpeedX, float initSpeedY) {
		super(blobDensity*radius*radius, radius, 3*(float)Math.PI/2, blobSizeFuelRatio*radius, initSpeedX, initSpeedY);
		addVisibleObject(body = new Sprite(Sprites.SPRITE_ROCKET, (float)Math.PI/4)).setVisible(true);
		setLocation(x, y);
	}

	private void updateThrottleValues(float z, float x) {
		if (Math.abs(getThrottleX())+Math.abs(getThrottleZ()) < blobMaxThrottle) {
			throttleX(x*throttleStepX);
			throttleZ(z*throttleStepZ);
		}
	}

	//push left = turn left
	public void goLeft(int units) {
		updateThrottleValues(-units, 0);
	}

	//push right = turn right
	public void goRight(int units) {
		updateThrottleValues(units, 0);
	}

	//push up = simple thrust upward
	public void goForward(int units) {
		updateThrottleValues(0, units);
	}

	//push down = simple thrust downward
	public void goBackward(int units) {
		updateThrottleValues(0, -units);
	}
	
	protected void onFreeze() {
		resetThrottle();
	}

	protected void onTankEmpty(){
	}
	
	protected void onTimeCycle(){
		throttleX(getThrottleX()*blobThrottleAbs);
		throttleZ(getThrottleZ()*blobThrottleAbs);
		/* update image orientation */
		if (body!=null) {
			body.setOrientation(getHeading());	
		}
	}
	
	public Vessel dropVessel() {
		return (Vessel) Space.getInstance().insertMass(new LandingModule(
				calculateOuterCoordinates(new Pair(-2*getDiameter(), 0)),
				getSpeedX(), getSpeedY()));
	}
}
