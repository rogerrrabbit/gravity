package vehicles;

import tools.Pair;
import graphics.Animation;
import graphics.ImageCache.Sprites;

public class LandingModule extends Vessel {

	private static final long serialVersionUID = 6708581936907075416L;

	private Animation body;

	private static final int LMRadius = 30;

	private static final float blobMaxThrottle = 100;
	private static final float blobThrottleAbs = -.1f;
	private static final float throttleStepZ = .01f;
	private static final float throttleStepX = .01f;
	private static final float throttleMin = .0001f;

	public LandingModule() {
		this(0, 0);
	}
	
	public LandingModule(Pair location) {
		this((int)location.fx, (int)location.fy);
	}
	
	public LandingModule(int x, int y) {
		this(x, y, 0, 0);
	}
	
	public LandingModule(Pair location, float initSpeedX, float initSpeedY) {
		this((int)location.fx, (int)location.fy, initSpeedX, initSpeedY);
	}
	
	public LandingModule(int x, int y, float initSpeedX, float initSpeedY) {
		super(9.f, LMRadius, 3*(float)Math.PI/2, 100.f, initSpeedX, initSpeedY);
		addVisibleObject(body = new Animation(Sprites.ANIMATION_LEM, (float) (Math.PI/2))).setVisible(true);
		setLocation(x, y);
	}

	private void updateThrottleValues(float y, float x) {
		if (Math.abs(getThrottleX())+Math.abs(getThrottleY()) < blobMaxThrottle) {
			throttleX(x*throttleStepX);
			throttleY(y*throttleStepZ);
		}
	}

	public void goLeft(int units) {
		updateThrottleValues(-units/2.f, units/2.f);
	}

	public void goRight(int units) {
		updateThrottleValues(units/2.f, units/2.f);
	}

	public void goForward(int units) {
		updateThrottleValues(0, units);
	}

	public void goBackward(int units) {
	}
	
	protected void onFreeze() {
		resetThrottle();
	}
	
	protected void onTankEmpty(){
	}

	protected void onTimeCycle(){
		throttleX(getThrottleX()*blobThrottleAbs);
		throttleY(getThrottleY()*blobThrottleAbs);

		/* heading is kept synced with gravity field direction */
		setHeading((float) (getGforce().getAngle() + Math.PI));
		
		if (body!=null) {
			/* update image orientation */
			body.setOrientation(getHeading());
			
			/* update engine graphics */
			if (getThrottleY()>throttleMin) {
				body.setImageIndex(1);
			} else if (getThrottleY()<-throttleMin){
				body.setImageIndex(2);
			} else if (getThrottleX()>throttleMin){
				body.setImageIndex(3);
			} else {
				body.setImageIndex(0);
			}
		}
	}
}
